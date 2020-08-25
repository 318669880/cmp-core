
ProjectApp.controller('ModelManagerController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, $state, $filter,Translator) {
    $scope._localData = {};
    /**
     * 索引服务工具
     * @constructor
     */
    let IndexServer = function() {
        this._init_address = null;
        this._loadDataUrl = 'modelManager/indexServer/query';
        this._saveDataUrl = 'modelManager/indexServer/save';
        this.address = null;
        this.validate = false;
        this.autoNext = true;
        this.model_env = 'host';
        this.onLine = true;
        this.initialize.apply(this , arguments);
    };
    IndexServer.prototype = {
        initialize: function () {

        },

        loadData: function() {
            let _self = this;
            $scope.executeAjax(this._loadDataUrl,'GET',{},response => {

                _self._init_address = response.modelAddress
                _self.address = response.modelAddress;
                _self.model_env = response.env;
                _self._init_onLine = !!response.onLine;
                _self.onLine = !!response.onLine;


                if (response.validate === 1 && _self.autoNext) {
                    _self.validate = true;
                    // 如果验证通过 默认展示第2页
                    _self.validateAddress(true);
                }
            })
        },

        saveData: function() {
            this.validate = this.validateAddress();
            return this.validate;
        },

        validateAddress: function(isvalidate) {
            if( !this.onLine ){
                this.address = window.location.origin + "/indexServer";
            }
            let _self = this;
            if(!this.address){
                $scope.showError('i18n_name_require', '索引服务不能为空');
                this.validate = false;
                return;
            }

            $scope.executeAjax(this.address+"/json/data.js",'GET', {remarks: 'query_version_json'}, function(text){
                try {
                    if(!!text && text.indexOf('let templateDate') !=-1){
                        let json = text.substr(text.indexOf("{"));
                        $scope.model_version_info = JSON.parse(json);
                        for (let x in $scope.model_version_info) {
                            $scope.model_version_info[x].forEach(basicModel => {
                                basicModel.lastRevision = null;
                                let icon = basicModel.icon;
                                if(icon.indexOf(this.address) == -1){
                                    icon = (this.address.endsWith("/")? this.address : (this.address+"/")) + icon;
                                    basicModel.icon = icon;
                                }
                            })
                        }
                        if(isvalidate){
                            $scope.wizard.api.next();
                            this.autoNext = false;
                            return;
                        }
                        this.validateSave();
                    }
                }catch (e) {
                    this.validate = false;
                    $scope.showError(null,'索引地址无效');
                    throw e;
                }
            }.bind(this));
            this.validate = false;
            return false;
        },

        validateSave: function() {
            if(this._init_address === this.address  && this._init_onLine === this.onLine){
                // 这说明 索引服务没有改过 为提升那么一点客户体验 那就不走后台了保存了
                this.validate = true;
                $scope.wizard.continue();
                return true;
            }
            //let saveSuccess = false;
            let _self = this;

            let param = {
                modelAddress : this.address,             // 索引服务地址
                validate : 1,                            // 验证结果
                onLine : this.onLine                   // 环境 默认是host 可选 k8s
            }
            $scope.executeAjax(this._saveDataUrl,'POST',param,res => {
                _self.model_env =  res.env;
                _self._init_address = res.address;
                _self._init_onLine = res.onLine;
                _self.validate = true;
                $scope.wizard.continue();
            })
            this.validate = false;
            return false;
        },
        changeOnline: function () {
            if(this._init_onLine === this.onLine){
                this.address = this._init_address;
                return;
            }
            this.address = "";
        }

    };
    /**
     * 模块安装工具
     * @constructor
     */
    let ModelInstaller = function() {
        this._loadLocalDatasUrl = 'modelManager/indexInstaller/modelInstallInfos';
        this._batchInstallUrl = 'modelManager/operate/readyInstall';
        this._loadNodeDataUrl = 'modelManager/model/nodes';
        this._installValidate = false;
        this.initialize.apply(this , arguments);
    }
    ModelInstaller.prototype = {
        initialize: function () {

            this.conditions = [
                {key: "name", name: Translator.get("i18n_model_field_name"), directive: "filter-contains"},
                {key: "module", name: Translator.get("i18n_model_field_module"), directive: "filter-contains"}
            ];

            // 用于传入后台的参数
            this.filters = [];

            this._initColumns();
        },

        _initColumns: function () {

            let checkColumn = {
                default: true,
                sort: false,
                type: "checkbox",
                checkValue: false,
                change: function (checked) {
                    $scope.installableItems.forEach(function (item) {
                        item.enable = checked;
                    });
                }.bind(this),
                width: "40px"
            }
            this.installableColumns = [
                checkColumn,
                {value: Translator.get("i18n_model_field_name"), key: "name", sort: false},
                {value: Translator.get("i18n_model_field_module"), key: "module", sort: false},
                {value: Translator.get("i18n_model_field_available_version"), key: "lastRevision", sort: false},
                {value: Translator.get("i18n_model_field_overview"), key: "overview", sort: false},
                // {value: '操作', key: "name", sort: false}
            ];



            this.installupdateColumns = [
                {value: Translator.get("i18n_model_field_name"), key: "name", sort: false},
                {value: Translator.get("i18n_model_field_module"), key: "module", sort: false},
                {value: Translator.get("i18n_model_field_current_version"), key: "current_version", sort: false},
                {value: Translator.get("i18n_model_field_install_time"), key: "installTime", sort: false},
                {value: Translator.get("i18n_model_field_available_version"), key: "lastRevision", sort: false},
                {value: Translator.get("i18n_model_field_overview"), key: "overview", sort: false}
            ];
        },

        _clearData: function () {

            $scope.installableItems = null;
            $scope.installupdateItems = null;
            $scope.installedItems = null;
            $scope.currentRevisions = null;
            $scope.currentUpdateRevisions = null;
            this._installValidate = false;
            $scope._nodeData = null;
            $scope.items = [];
        },

        loadData: function () {
            this._clearData();
            // 获取json文件中模块数据与本地数据库对比
            let _self = this;
            $scope.executeAjax(this._loadLocalDatasUrl,'GET',null,(res) => {
                !!res && res.forEach(item => {
                    $scope._localData[item.module] = item;
                });
                _self.loadInstallable();_self.loadUpdates();
                $scope.list();
            });
        },

        loadNodeData: function () {
            $scope.executeAjax(this._loadNodeDataUrl,"POST",null,function(res) {
                $scope._nodeData = Object.create({});
                res.forEach(function(node){
                    let model_uuid = node.modelBasicUuid;
                    $scope._nodeData[model_uuid] = $scope._nodeData.hasOwnProperty(model_uuid) ? $scope._nodeData[model_uuid] : new Array();
                    $scope._nodeData[model_uuid].push(node);
                }.bind(this));
            }.bind(this));
        },

        saveData: function () {
            this._installValidate = !!$scope._localData;
            // 存储到本地数据库
            if(!this._installValidate){
                $scope.showError('i18n_model_check_no','请先安装至少一个模块');
                return false;
            }
            return true;
        },


        // 加载可安装数据
        loadInstallable: function () {
            let _self = this;
            let models = []
            for (let modelVersionInfoKey in $scope.model_version_info) {
                let item = $scope.model_version_info[modelVersionInfoKey];
                (item instanceof Array) && (models = models.concat(item));
            }
            $scope.installableItems = models.filter(model => {
                let available = true;
                available = !$scope._localData.hasOwnProperty(model.module);
                if(!available) return false;
                let condition = FilterSearch.convert(this.filters);
                if(!!condition){
                    if(condition.name){
                        let name = condition.name.slice(1,-1);
                        available = model.name.indexOf(name) != -1;
                    }
                    if(condition.module){
                        let module = condition.module.slice(1,-1);
                        available = model.module.indexOf(module) != -1
                    }
                }
                return available;
            }).map(model => {
                model.lastRevision = null;
                //model.remoteImageUrl = model.icon.indexOf($scope.indexServer.address)==-1 ? ($scope.indexServer.address+ "/" + model.icon) : model.icon;
                model.enable = false;
                model.lastRevision = _self._lastVersion(model);
                model._versionEdit = false;//默认是非编辑状态
                return model;
            });
        },




        editVersion: function (item) {
            this.endEditAll();
            if(item._versionEdit) return;
            item.last_select_value = item.last_select_value  || item.lastRevision;
            $scope.currentRevisions = item.revisions.concat([{
                "revision": "取消",
                "description": "selectcancel",
                "created": "cancel",
                "downloadUrl": "cancel"
            }]);
            item._versionEdit = true;
        },

        doneEditVersion: function (item) {
            if(item.lastRevision === '取消'){
                item.lastRevision = item.last_select_value
            }
            item.last_version = this.getVersionInfo(item.module,item.lastRevision);

            item._versionEdit = false;
        },

        endEditAll: function () {
            $scope.installableItems.forEach( item => item._versionEdit = false);
        },

        //  执行安装
        executeInstall: function (nodeId) {
            let _self = this;
            let param = $scope.installableItems.filter(model => model.enable === true).map(model => {
                let dto = Object.create({});
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                let modelVersion = model.last_version;
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            if (param.length == 0 ){
                $scope.showWarn('i18n_model_check_no',"请至少选择一个模块！");
                return;
            }
            nodeId = nodeId || "-1";
            let callBackMethod = function(){
                $scope.closeInformation();
                _self.loadData();
            };
            $scope.loadingLayer = HttpUtils.post(this._batchInstallUrl+"/"+nodeId, param, callBackMethod,callBackMethod);


        },

        // 加载可更新数据
        loadUpdates: function () {
            let _self = this;
            let models = []
            for (let modelVersionInfoKey in $scope.model_version_info) {
                let item = $scope.model_version_info[modelVersionInfoKey];
                (item instanceof Array) && (models = models.concat(item));
            }
            $scope.installupdateItems = models.filter(model => {
                let localData = $scope._localData[model.module];
                if (!localData) return false;
                let lastVersion = _self._lastVersion(model);
                return localData.lastRevision !== lastVersion;
            }).map(model => {
                model.current_version = $scope._localData[model.module].lastRevision;
                model.lastRevision = model.last_version.revision;
                model._versionEdit = false;
                model.installTime = $scope._localData[model.module].installTime;
                model.icon = $scope._localData[model.module].icon;
                model._update_options = model.revisions.filter(revision => {
                    let tempTime = new Date(revision.created).getTime();
                    let itemVersionTime = new Date(_self.getVersionInfo(model.module,model.current_version).created).getTime()
                    return tempTime > itemVersionTime;
                });
                return model;
            });

        },
        editUpdateVersion: function(item) {
            this.endEditAllUpdate();
            if(item._versionEdit) return;
            item.last_select_value = item.last_select_value  || item.lastRevision;
            $scope.currentUpdateRevisions = item._update_options.concat([{
                "revision": "取消",
                "description": "selectcancel",
                "created": "cancel",
                "downloadUrl": "cancel"
            }]);
            item._versionEdit = true;
        },


        endEditAllUpdate: function() {
            $scope.installupdateItems.forEach( item => item._versionEdit = false);
        },


        //  执行更新
        executeUpdate: function () {
            let _self = this;
            let opmodel = null;
            let param = $scope.installupdateItems.filter(model => model.enable === true).map(model => {
                opmodel = model;
                let dto = Object.create({});
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                let modelVersion = model.last_version;
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            $scope.executeAjax(this._batchInstallUrl,'POST',param, (resp) => {
                opmodel.enable = false;
                _self.loadData();
            })
            opmodel.enable = false;
        },




        _lastVersion: function(model) {
            let last_version = null;
            model.revisions.forEach( version => {
                if(!last_version) {
                    last_version = version;
                }else{
                    version.created && (new Date(version.created).getTime() > new Date(last_version.created).getTime()) && (last_version = version)
                }
            })
            model.last_version = last_version;
            return last_version.revision;
        },

        getVersionInfo: function (module,versionNum) {
            let result = null;
            let models = []
            let cmodel = null;
            for (let modelVersionInfoKey in $scope.model_version_info) {
                let item = $scope.model_version_info[modelVersionInfoKey];
                (item instanceof Array) && (models = models.concat(item));
            }
            models.some(model => {
                cmodel = model;
                return model.module === module
            }) && cmodel.revisions.some(versionInfo => {
                result = versionInfo;
                return versionInfo.revision === versionNum
            })
            return result;
        },

        allNodeStart: function () {
            let param = $scope.items.filter(item => item.enable===true);
            if(!param || param.length===0){
                //Notification.warn("！");
                $scope.showWarn('i18n_model_check_no','请至少选择一个模块');
                return;
            }
            param = param.filter(item => {
                return item.status.indexOf("stopped") != -1;
            });
            if(!param || param.length===0){
                $scope.showWarn('i18n_model_check_no_stop','请至少选择一个停止状态的模块');
                return;
            }
            param = param.map(item => item.module);
            HttpUtils.post('modelManager/operate/module/start',param,resp => {
                $scope.list();
            },err => {
                $scope.list();
            })
        },

        allNodeStop: function () {
            let param = $scope.items.filter(item => item.enable===true);
            if(!param || param.length===0){
                $scope.showWarn('i18n_model_check_no','请至少选择一个模块');
                return;
            }
            param = param.filter(item => {
                let arr = item.statuInfo.split("/");
                return arr[0] != 0;
            });
            if(!param || param.length===0){
                Notification.warn("！");
                $scope.showWarn('i18n_model_check_no_start','请至少选择一个启动状态的模块');
                return;
            }
            param = param.map(item => item.module);
            HttpUtils.post('modelManager/operate/module/stop',param,resp => {
                $scope.list();
            },err => {
                $scope.list();
            })
        }

    };

    let ModelShow = function(){
        $scope.cards = [];//需要展示的卡片
        this.initialize.apply(this , arguments);
    };
    ModelShow.prototype = {
        initialize: function () {
            //this._initCards();
        },
        _initCards: function () {
            $scope.cards = [];
            angular.forEach($scope._localData, (model,module) => {
                let status_array = model.status.split(",");
                let runing_array = status_array.filter(item => item==='running');
                let statuInfo = runing_array.length + "/" + status_array.length;

                if(!!runing_array && runing_array.length > 0 ){
                    model.active = true;
                    model.module_status = "running";
                }


                let card = {
                    name: model.name,
                    module: module,
                    content: model.overview,
                    auth: true,
                    status: model.status,
                    statuInfo: statuInfo,
                    module_status: model.module_status || 'stopped',
                    active: model.active || false
                };
                $scope.cards.push(card);
            })
        },
        openLog: function (item) {
            sessionStorage.setItem("ModuleToLogParam", angular.toJson({
                    label: item.name,
                    value: item.module
                }
            ));
            $state.go("log/system")
        },
        search: function (param) {
            this._initCards();
            $scope.cards = !!param && $scope.cards.filter(card => (card.name.indexOf(param) != -1) || (card.module.indexOf(param) != -1)) || $scope.cards;
        }
    }

    let ModelNodeWs = function(){
        this.ws_url = null;
        this.ws_uid = "1";//这里与后台对应
        this.ws = null;
        this.socket = null;
        this.stompClient = null;
        this.initialize.apply(this , arguments);
    }
    ModelNodeWs.prototype = {
        initialize: function () {
            /*let pre = window.location.origin.indexOf("https") !=-1 ? "https" : "http";
            this.ws_url = window.location.origin.replace(pre,"ws");
            this.ws_url += "/websocket";*/
            this.ws_url = window.location.origin+"/websocket";
            this.connect();
        },
        connect: function () {
            this.socket = new SockJS(this.ws_url);
            this.stompClient = Stomp.over(this.socket);//使用STMOP子协议的WebSocket客户端
            this.stompClient.connect({},function(frame){//连接WebSocket服务端
                console.log('Connected:' + frame);
                //通过stompClient.subscribe订阅/topic/getResponse 目标(destination)发送的消息
                this.stompClient.subscribe('/topic/getResponse',function(response){
                    this.parseMessage(JSON.parse(response.body));
                }.bind(this));
            }.bind(this));
        },
        disconnect: function () {
            if(this.stompClient != null) {
                this.stompClient.disconnect();
            }
            console.log("Disconnected");
        },
        parseMessage: function (obj) {
            // 如果obj为true那么刷新数据
            !!obj && $scope.modelInstaller.loadData();
        }
    };

    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t" + window.appversion;
    $scope.indexServer = new IndexServer();
    $scope.modelInstaller = new ModelInstaller();
    $scope.modelShow = new ModelShow();
    $scope.modelNodeWs = new ModelNodeWs();
    $scope.wizard = {
        setting: {
            title: $filter('translator')('i18n_title', '标题'),
            subtitle: $filter('translator')('i18n_sub_title', '子标题'),
            closeText: $filter('translator')('i18n_cancel', '取消'),
            submitText: $filter('translator')('i18n_save', '保存'),
            nextText: $filter('translator')('i18n_next', '下一步'),
            prevText: $filter('translator')('i18n_previous', '上一步'),
        },
        // 按顺序显示,id必须唯一并需要与页面中的id一致，select为分步初始化方法，next为下一步方法(最后一步时作为提交方法)
        steps: [
            {
                id: "1",
                name: $filter('translator')('i18n_index_server', '索引服务'),
                select: function () {
                    $scope.indexServer.loadData();

                },
                next: function () {
                    return $scope.indexServer.saveData();
                }
            },
            {
                id: "2",
                name: $filter('translator')('i18n_model_installer', '模块安装'),
                select: function () {
                    $scope.modelInstaller.loadData();

                },
                next: function () {

                    return $scope.modelInstaller.saveData();
                }
            },
            {
                id: "3",
                name: $filter('translator')('i18n_model_show', '模块展示'),
                select: function () {
                    for (let i = 0; i < 10; i++) {
                        $scope.modelShow._initCards();
                    }
                },
                next: function () {
                    return true;
                }
            }
        ],
        // 嵌入页面需要指定关闭方法
        close: function () {
            $scope.closeToggleForm();
            $scope.show = false;
        }
    };

    $scope.show = true;

    $scope.showError = function (template,defaultMessage) {
        return Notification.info($filter('translator')(template, defaultMessage)) && false;
    }
    $scope.showWarn = function(template,defaultMessage) {
        return Notification.warn($filter('translator')(template, defaultMessage))  && false;
    }

    $scope.executeAjax = function (url,type,param,success){
        let resp = function (response) {
            if (response.success) {
                success && (success instanceof Function) && success(response.data);
            } else {
                Notification.danger(response.message);
            }
        }
        let error = null;
        if(!!param && param.hasOwnProperty('remarks') && param['remarks'] === 'query_version_json' ) {
            // 这里特殊处理 因为索引服务器url返回的数据 跟我们后台包装的返回结果 数据结构格式不一致 不能用我们这一套解析结果
            resp = success;
            error = success;
        }
        $scope.loadingLayer = (!!type && (type.toUpperCase() === 'GET')) ? HttpUtils.get(url, resp,error) : HttpUtils.post(url, param, resp,error);
    }



    $scope.conditions = [
        {key: "name", name: '名称', directive: "filter-contains"},
        {key: "module", name: '模块', directive: "filter-contains"}
    ];

    // 用于传入后台的参数
    $scope.filters = [];
    $scope.columns = [
        {
            default: true,
            sort: false,
            type: "checkbox",
            checkValue: false,
            change: function (checked) {
                $scope.items.forEach(function (item) {
                    item.enable = checked;
                });
            }.bind(this),
            width: "40px"
        },
        {value: Translator.get("i18n_model_field_name"), key: "name", sort: false},
        {value: Translator.get("i18n_model_field_module"), key: "module", sort: false},
        {value: Translator.get("i18n_model_field_status"), key: "currentStatus", sort: false},
        {value: Translator.get("i18n_model_field_current_version"), key: "lastRevision", sort: false},
        {value: Translator.get("i18n_model_field_install_time"), key: "installTime", sort: false},
        {value: Translator.get("i18n_model_field_overview"), key: "overview", sort: false}
    ];

    $scope.list = function (sortObj) {
        $scope.items = [];
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "modelManager/runner", condition, function (rep) {
            //$scope.load = true;
            $scope.formatModuleStatus();
            $scope.modelInstaller.loadNodeData();
        });
    };

    $scope.formatModuleStatus = function(){
        $scope.items.forEach(item => {
            let status_array = item.status.split(",");
            let runing_array = status_array.filter(item => item==='running');
            item.enable = false;
            item.statuInfo = runing_array.length + "/" + status_array.length;
        })
    }

    $scope.openNodeInfo = function (item) {
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.current_module = item.module;
        $scope.model_name = item.name;
        $scope.infoUrl = 'project/html/model_manager/node-list.html' + '?_t=' + Math.random();
        $scope.is_mc = false;
        $scope.toggleInfoForm(true);
    };

    $scope.mcNodeInfo = function (item) {

        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        item.enable = true;
        $scope.selected = item.$$hashKey;
        $scope.current_module = "management-center";
        $scope.model_name = item.name;
        $scope.infoUrl = 'project/html/model_manager/node-list.html' + '?_t=' + Math.random();
        $scope.is_mc = true;
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.item = {};
        $scope.current_module = null;
        $scope.model_name = null;
        $scope.selected = "";
        $scope.infoUrl = null;
        $scope.toggleInfoForm(false);
    };


    $scope.startModule = function (item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }
        if (module_arr.length == 0 ){
            Notification.warn("请选择模块！");
            return;
        }
        let obj = {
            title: $filter('translator')('i18n_apply_reason', 'Pod 数量'),
            text: $filter('translator')('i18n_apply_reason', 'Pod 数量'),
            required: true,
            type:'number',
            init: 1
        };

        Notification.prompt(obj, function (result) {
            let pod_number = result;

            //TODO
        });

    }

    $scope.startK8sModule = function (item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }
        if (module_arr.length == 0 ){
            $scope.showWarn('i18n_model_check_no','请至少选择一个模块')
            return;
        }
        let obj = {
            title: $filter('translator')('i18n_apply_reason', 'Pod 数量'),
            text: $filter('translator')('i18n_apply_reason', 'Pod 数量'),
            required: true,
            type:'number',
            init: 1
        };

        Notification.prompt(obj, function (result) {
            let pod_number = result;
            $scope.loadingLayer = HttpUtils.post('k8s-operator-module/start/' , {modules: module_arr}, function (resp) {
                Notification.info($filter('translator')('i18n_model_result', "启动结果，请查看日志.")) ;
            }, function (resp) {
            });
        });
    }

    $scope.stopK8sModule = function (item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }
        if (module_arr.length == 0 ){
            $scope.showWarn('i18n_model_check_no','请至少选择一个模块')
            return;
        }

        $scope.loadingLayer = HttpUtils.post('k8s-operator-module/stop/' , {modules: module_arr}, function (resp) {
            Notification.info($filter('translator')('i18n_model_result', "启动结果，请查看日志.")) ;
        }, function (resp) {
        });
    }


});

ProjectApp.controller('ModelManagerNodeController', function ($scope, HttpUtils, Translator) {

    $scope.columns = [
        {value: Translator.get("i18n_model_node_field_node"), key: "nodeHost", sort: false},
        {value: Translator.get("i18n_model_node_field_status"), key: "nodeStatus", sort: false},
        {value: Translator.get("i18n_model_node_field_create_time"), key: "nodeCreateTime", sort: false},
    ];

    $scope.list = function () {
        if(!$scope.model_name || !$scope.current_module) return;
        let url = "modelManager/node/" + $scope.current_module;
        HttpUtils.paging($scope,  url, resp => {
            if($scope.is_mc) {
                /*let sourceModule = $scope.sourceItem.module;
                let nodes = $scope.installableItems.filter(item => item.module === sourceModule)
                $scope.items.forEach(item => {

                })*/
            }
        })
    };
    $scope.list();

    $scope.install = function(item) {
        $scope.loadingLayer = HttpUtils.post('modelManager/operate/node/install/' + $scope.current_module + "/" + item.modelNodeUuid, null, function (resp) {
            $scope.list();
        }, function (resp) {
            $scope.list();
        });
    };
    $scope.start = function(item) {
        $scope.loadingLayer = HttpUtils.post('modelManager/operate/node/start/'+$scope.current_module+"/"+item.modelNodeUuid, null, function (resp) {
            $scope.list();
        }, function (resp) {
            $scope.list();
        });
    };
    $scope.stop = function(item) {
        $scope.loadingLayer = HttpUtils.post('modelManager/operate/node/stop/'+$scope.current_module+"/"+item.modelNodeUuid, null, function (resp) {
            $scope.list();
        }, function (resp) {
            $scope.list();
        });
    };

});








