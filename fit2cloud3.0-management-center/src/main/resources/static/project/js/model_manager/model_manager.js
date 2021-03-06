ProjectApp.controller('ModelManagerController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, $state, $filter,Translator, eyeService, ProhibitPrompts) {
    $scope._localData = {};
    $scope._eurekaData = {};
    /**
     * 索引服务工具
     * @constructor
     */
    let IndexServer = function() {
        this._loadAddressUrl = 'modelManager/indexServer/address';
        this._indexDataUrl = "modelManager/indexServer/indexData";
        this._init_address = null;
        this._loadDataUrl = 'modelManager/indexServer/query';
        this._saveDataUrl = 'modelManager/indexServer/save';
        this.address = null;
        this.validate = false;
        this.autoNext = true;
        this.model_env = 'host';
        this.onLine = false;
        this.dockerRegistry = {};
        this._dockerRegistry = {};
        this.manager_uuid = null;
        this.initialize.apply(this , arguments);
    };

    $scope.view = function () {
        eyeService.view("#password", "#eye");
    };

    IndexServer.prototype = {
        initialize: function () {
        },
        initAddress: function () {
            if(!this.address){
                // this.address = 'https://f2c-index.oss-cn-beijing.aliyuncs.com';
                if (this.onLine){//默认保存在线索引服务
                    this.saveData();
                }
            }
        },

        loadData: function() {
            let _self = this;
            $scope.executeAjax(this._loadDataUrl,'GET',{},response => {
                console.log(response);
                _self.manager_uuid = response.uuid;
                _self._init_address = response.modelAddress
                _self.address = response.modelAddress;
                _self.model_env = response.env;
                _self._init_onLine = !!response.onLine;
                _self.onLine = !!response.onLine;
                _self.dockerRegistry = angular.fromJson(response.dockerRegistry);
                _self._dockerRegistry = angular.fromJson(response.dockerRegistry);

                if (response.validate === 1 && _self.autoNext) {
                    _self.validate = true;
                    // 如果验证通过 默认展示第2页
                    _self.validateAddress(true);
                }
                _self.initAddress();
            })
        },

        view: function (index) {
            eyeService.view('#password' + index, '#eye' + index);
        },

        changeType: function (id) {
            ProhibitPrompts.changeType(id);
        },

        saveData: function() {
            this.validate = this.validateAddress();
            return this.validate;
        },

        validateAddress: function(isvalidate) {
            /*if( !this.onLine ){
                //离线使用同源索引服务
                //this.address = "http://62.234.205.170/nexus/repository/maven-releases/";
                this.address = window.location.origin + "/nexus/repository/maven-releases/";
            }*/
            let _self = this;
            if(!this.address){
                $scope.showError('i18n_name_require', '索引服务不能为空');
                this.validate = false;
                return;
            }
            if(!this.onLine && !this.dockerRegistry.url){
                $scope.showError('i18n_docker_registry_url_require', '镜像仓库地址不能为空');
                this.validate = false;
                return;
            }
            if(!this.onLine && !this.dockerRegistry.user){
                $scope.showError('i18n_docker_registry_user_require', '镜像仓库用户名不能为空');
                this.validate = false;
                return;
            }
            if(!this.onLine && !this.dockerRegistry.passwd){
                $scope.showError('i18n_docker_registry_passwd_require', '镜像仓库密码不能为空');
                this.validate = false;
                return;
            }
            let dataUrl = (this.address.endsWith("/") ? this.address : (this.address+"/"))+"json/data.js";
            /*if(!this.onLine){
                let baseUrl = this.address.endsWith("/") ? this.address : (this.address+"/");
                dataUrl = baseUrl + "com/fit2cloud/indexserver/3.0/indexserver-3.0.js";
            }*/
            $scope.executeAjax(this._indexDataUrl,'POST', {dataUrl: dataUrl}, function(text){
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
                            $scope.switchTab(0);
                            this.autoNext = false;
                            return;
                        }
                        this.validateSave();
                    }else {
                        this.validate = false;
                        $scope.showError('i18n_index_server_addr_valid','索引地址无效');
                    }
                }catch (e) {
                    this.validate = false;
                    $scope.showError('i18n_index_server_addr_valid','索引地址无效');
                    throw e;
                }
            }.bind(this));
            this.validate = false;
            return false;
        },

        validateSave: function() {
            if(!!this.manager_uuid && this._init_address === this.address  && this._init_onLine === this.onLine && angular.toJson(this._dockerRegistry) === angular.toJson(this.dockerRegistry)){
                // 这说明 索引服务没有改过 为提升那么一点客户体验 那就不走后台了保存了
                this.validate = true;
                $scope.switchTab(0)
                return true;
            }
            //let saveSuccess = false;
            let _self = this;

            let param = {
                modelAddress : this.address,             // 索引服务地址
                validate : 1,                            // 验证结果
                onLine : this.onLine                     // 环境 默认是host 可选 k8s

            }
            !!this.dockerRegistry && angular.extend(param,{dockerRegistry: angular.toJson(this.dockerRegistry)});
            $scope.executeAjax(this._saveDataUrl,'POST',param,res => {
                _self.model_env =  res.env;
                _self._init_address = res.address;
                _self._init_onLine = res.onLine;
                _self.validate = true;
                $scope.switchTab(0)
            })
            this.validate = false;
            return false;
        },
        changeOnline: function (onLine) {
            if(this._init_onLine === this.onLine){
                this.address = this._init_address;
                return;
            }
            if(onLine){
                this.address = "";
                // this.address = 'https://f2c-index.oss-cn-beijing.aliyuncs.com';
            }else {
                this.address = "";
            }
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
                /*{value: Translator.get("i18n_model_field_module"), key: "module", sort: false},*/
                {value: Translator.get("i18n_model_field_available_version"), key: "lastRevision", sort: false},
                {value: Translator.get("i18n_model_field_release_time"), key: "created", sort: false},
                {value: Translator.get("i18n_model_field_overview"), key: "overview", sort: false},
                // {value: '操作', key: "name", sort: false}
            ];



            this.installupdateColumns = [
                {value: Translator.get("i18n_model_field_name"), key: "name", sort: false},
                /*{value: Translator.get("i18n_model_field_module"), key: "module", sort: false},*/
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
            $scope._localData = {};
            $scope.executeAjax(this._loadLocalDatasUrl,'GET',null,(res) => {
                !!res && res.forEach(item => {
                    $scope._localData[item.module] = item;
                });
                _self.loadInstallable();_self.loadUpdates();
                $scope.list();
            });
        },

        loadNodeData: function (callBack) {
            $scope.executeAjax(this._loadNodeDataUrl,"POST",null,function(res) {
                $scope._nodeData = Object.create({});
                res.forEach(function(node){
                    let model_uuid = node.modelBasicUuid;
                    $scope._nodeData[model_uuid] = $scope._nodeData.hasOwnProperty(model_uuid) ? $scope._nodeData[model_uuid] : new Array();
                    $scope._nodeData[model_uuid].push(node);
                }.bind(this));
                if(!!callBack && (callBack instanceof Function))
                    callBack();
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
                //model.enable = false;
                model.lastRevision = _self._lastVersion(model);
                model._versionEdit = false;//默认是非编辑状态
                model.fName = Translator.get("i18n_model_"+model.module) || model.name;
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


        executeInstall_k8s: function(item){
            let _self = this;
            let param = $scope.installableItems.filter(model => model.module === item.module).map(model => {
                let dto = {};
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                dto.modelBasic.podNum = 0 ;
                let modelVersion = angular.copy(model.last_version);
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            this.executeInstall(null, param);
        },

        installK8sBatch: function(){
            let _self = this;
            let param = $scope.installableItems.filter(model => model.enable === true).map(model => {
                let dto = {};
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                dto.modelBasic.podNum = 0 ;
                let modelVersion = angular.copy(model.last_version);
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            this.executeInstall(null, param);
        },

        installModules: function(){
            let _self = this;
            let param = $scope.installableItems.filter(model => model.enable === true).map(model => {
                let dto = {};
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                let modelVersion = angular.copy(model.last_version);
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            this.executeInstall(null, param);
        },

        installModule: function(item){
            let _self = this;
            let param = $scope.installableItems.filter(model => model.module === item.module).map(model => {
                let dto = {};
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                let modelVersion = angular.copy(model.last_version);
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            this.executeInstall(null, param);
        },
        installModuleNode: function(nodeId){
            let _self = this;
            let param = $scope.installableItems.filter(model => model.name === $scope.model_name).map(model => {
                let dto = {};
                model.lastRevision = model.lastRevision || _self._lastVersion(model);
                dto.modelBasic = model;
                let modelVersion = angular.copy(model.last_version);
                modelVersion.created = new Date(modelVersion.created).getTime();
                dto.modelVersion = modelVersion
                return dto;
            });
            this.executeInstall(nodeId, param);
        },


        //  执行安装
        executeInstall: function (nodeId, param) {
            let _self = this;
            if (param.length == 0 ){
                $scope.showWarn('i18n_model_check_no',"请至少选择一个模块！");
                return;
            }
            Notification.confirm(Translator.get("i18n_model_install_confirm"),  () => {
                nodeId = nodeId || "-1";
                let callBackMethod = function(){
                    $scope.closeInformation();
                    _self.loadData();
                };
                $scope.loadingLayer = HttpUtils.post(this._batchInstallUrl+"/"+nodeId+"/add", param, callBackMethod,callBackMethod);
            });
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
                model.fName = Translator.get("i18n_model_"+model.module) || model.name;
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
        executeUpdate: function (item) {
            let _self = this;
            let opmodel = null;
            let param = $scope.installupdateItems.filter(model => model.module === item.module).map(model => {
                opmodel = model;
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
            Notification.confirm(Translator.get("i18n_model_update_confirm"),  () => {

                let callBackMethod = function(){
                    $scope.closeInformation();
                    _self.loadData();
                };
                $scope.loadingLayer = HttpUtils.post(this._batchInstallUrl+"/-1/update", param, callBackMethod,callBackMethod);
            });
            //opmodel.enable = false;
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
        this.topic_url = "modelManager/model/topics";
        this.ws_url = null;
        this.ws_uid = "1";//这里与后台对应
        this.ws = null;
        this.socket = null;
        this.stompClient = null;
        this.initialize.apply(this , arguments);
    }
    ModelNodeWs.prototype = {
        initialize: function () {
            this.ws_url = window.location.origin+"/management-center/websocket";
            //this.ws_url = window.location.origin+"/websocket";
            HttpUtils.post(this.topic_url, null, function(res){
                this.connect(res.data);
            }.bind(this), error => {

            })

        },
        connect: function (topics) {
            this.socket = new SockJS(this.ws_url);
            this.stompClient = Stomp.over(this.socket);//使用STMOP子协议的WebSocket客户端
            this.stompClient.connect({},function(frame){//连接WebSocket服务端
                //console.log('Connected:' + frame);
                //通过stompClient.subscribe订阅/topic/getResponse 目标(destination)发送的消息
                topics.forEach(function(topic) {
                    if (topic.name.startsWith($scope.indexServer.model_env.toLocaleUpperCase())){
                        this.stompClient.subscribe(topic.value,function(response){
                            let res = JSON.parse(response.body);
                            if (res.type == 'K8S_MODEL_UNINSTALL'){
                                $scope.modelInstaller.loadData();
                                return
                            }
                            if ($scope.indexServer.model_env == 'host'){
                                this.parseHostMessage(res);
                            }else{
                                this.parseK8sMessage(res);
                            }

                        }.bind(this));
                    }
                }.bind(this));


            }.bind(this));
        },
        disconnect: function () {
            if(this.stompClient != null) {
                this.stompClient.disconnect();
            }
            //console.log("Disconnected");
        },
        parseHostMessage: function (obj) {
            $scope.formatModuleStatus();
            if(!!$scope.selected ){
                $scope.$broadcast('onNodeRefresh', 'true');
            }
        },
        parseK8sMessage: function (obj) {
            $scope.formatModuleStatus();
            if(!!$scope.selected ){
                $scope.$broadcast('onNodeRefresh', 'true');
            }
        }
    };


    $scope.indexServer = new IndexServer();
    $scope.modelInstaller = new ModelInstaller();
    /*$scope.modelShow = new ModelShow();*/
    $scope.modelNodeWs = new ModelNodeWs();


    $scope.show = true;

    $scope.showError = function (template,defaultMessage) {
        return Notification.danger($filter('translator')(template, defaultMessage)) && false;
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
        {key: "name", name: Translator.get("i18n_workspace_name"), directive: "filter-contains"},
        {key: "module", name: Translator.get("i18n_module"), directive: "filter-contains"}
    ];

    // 用于传入后台的参数
    $scope.filters = [];
    $scope.columns = [
        /*{
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
        },*/
        {value: Translator.get("i18n_model_field_name"), key: "name", sort: false},
        /*{value: Translator.get("i18n_model_field_module"), key: "module", sort: false},*/
        /*{value: Translator.get("i18n_model_field_status"), key: "currentStatus", sort: false},*/
        {value: Translator.get("i18n_model_field_status_new"), key: "currentStatus", sort: false},
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
            $scope.formatModuleStatus();
            $scope.formatItemName();
        });
    };

    $scope.formatItemName = function () {
        $scope.items.forEach(item => item.fName = (Translator.get("i18n_model_"+item.module)) || item.name);
    };
    $scope.formatModuleStatus = function(){
        if($scope.indexServer.model_env=='host'){
            $scope.modelInstaller.loadNodeData($scope.fillNodeData);
        }else {
            $scope.loadEurekaData($scope.fillEurekaData);
        }
    };
    $scope.fillNodeData = function (is_fresh) {
        $scope.items.forEach((item ,index) => {
            let status_array = item.status.split(",");
            //let runing_array = !!is_fresh && $scope._nodeData[item.module] && $scope._nodeData[item.module].filter(item => item.nodeStatus==='running') || status_array.filter(item => item==='running');
            let runing_array = $scope._nodeData[item.module].filter(item => item.nodeStatus==='running');
            //item.enable = false;
            let statuInfo = (!!runing_array ? runing_array.length : 0 )+ "/" + (!!status_array ? status_array.length : 0 );
            item.statuInfo = statuInfo;
            $scope.items[index].statuInfo = statuInfo;
            //console.log(item.statuInfo);
        });
    };
    $scope.fillEurekaData = function(){
        $scope.items.forEach(item => {
            let podNum = item.podNum;
            let podInstances = $scope._eurekaData[item.module];
            let runningPods = !!podInstances && podInstances.length || 0;
            //item.enable = false;
            item.runningPods = runningPods;
            item.statuInfo = runningPods + "/" + podNum;
            let prefix = runningPods > podNum ? "i18n_model_k8s-status_shrinke"
                : runningPods == podNum ? false : "i18n_model_k8s-status_expand";
            if (item.currentStatus && item.currentStatus=='uninstalling'){
                prefix = "i18n_model_k8s-status_uninstall";
            }
            if (item.currentStatus && item.currentStatus=='timeOut'){
                prefix = "i18n_model_k8s-status_timeout";
            }
            let _dynamic = runningPods + " -> "+ podNum;
            item.dynamicInfo = !!prefix && (Translator.get(prefix) + ": ("+_dynamic+")") || item.statuInfo;
            let syncStatus = ["i18n_model_k8s-status_shrinke", "i18n_model_k8s-status_expand", "i18n_model_k8s-status_uninstall"];
            if (syncStatus.indexOf(prefix) != -1){
                item.sync = true;
            }else{
                item.sync = false;
            }
        })
    };
    $scope.loadEurekaData = function(callBack) {
        $scope.executeAjax('k8s-operator-module/pods','GET',null,res => {
            $scope._eurekaData = res;
            if(!!callBack && (callBack instanceof Function)){
                callBack(res);
            }
        });
    };


    $scope.openNodeInfo = function (item) {
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.current_module = item.module;
        $scope.model_name = item.name;
        $scope.fName = Translator.get("i18n_model_"+$scope.current_module) || item.name;
        $scope.infoUrl = 'project/html/model_manager/node-list.html' + '?_t=' + Math.random();
        $scope.is_mc = false;
        $scope.toggleInfoForm(true);
    };

    $scope.mcNodeInfo = function (item) {

        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        //item.enable = true;
        $scope.selected = item.$$hashKey;
        $scope.current_module = "management-center";
        $scope.fName = Translator.get("i18n_model_"+$scope.current_module) || item.name;
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
        $scope.fName = null;
        $scope.infoUrl = null;
        $scope.toggleInfoForm(false);
    };



    $scope.toPodNumPage = function(item){
        if (!!item.currentStatus && item.currentStatus == 'uninstalling'){
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.k8sModule = item;
        $scope.infoUrl = 'project/html/model_manager/k8s_pod.html' + '?_t=' + Math.random();
        $scope.toggleInfoForm(true);
    };

    //关闭pod数量调整页面
    $scope.closeToggleForm = function(){
        $scope.selected = "";
        $scope.infoUrl = null;
        $scope.k8sModule = null;
        $scope.toggleInfoForm(false);
    };



    $scope.uninstallK8sModule = function (item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }/*else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }*/
        if (module_arr.length == 0 ){
            $scope.showWarn('i18n_module_null_msg','模块不能为空.')
            return;
        }

        Notification.confirm($filter('translator')("i18n_uninstall_modules_confirm", "确定卸载所选模块？"), function () {
            $scope.loadingLayer = HttpUtils.post('k8s-operator-module/uninstall/' , {modules: module_arr}, function () {
                Notification.info($filter('translator')('i18n_excute_resule', "执行成功，请稍后刷新模块状态.")) ;
                $scope.list();
            }, function (resp) {
            });

        })
    }

    $scope.mcReload = function () {
        let url = "modelManager/model/reload";
        $scope.loadingLayer = HttpUtils.post(url, null, function (resp) {
            if (resp.success && resp.data){
                Notification.info(Translator.get("i18n_reload_mc_success"));
            }else {
                Notification.warn(Translator.get("i18n_reload_mc_faild"));
            }
        }, function (resp) {
            Notification.warn(Translator.get("i18n_reload_mc_faild"));
        });
    }


    $scope.selectedTableIndex = 3;

    $scope.switchTab = function (index) {
        if (index < 0 || index > 3) return;
        $scope.selectedTableIndex = index;
        if (index < 3){
            $scope.modelInstaller.loadData();
        }
    }

    $scope.pageInit = function () {
        //1.首先加载索引服务资源
        $scope.indexServer.loadData();
    }
    $scope.pageInit();

});

ProjectApp.controller('ModelManagerNodeController', function ($scope, HttpUtils, Translator, $state) {

    $scope.columns = [
        {value: Translator.get("i18n_model_node_field_node"), key: "nodeHost", sort: false},
        {value: Translator.get("i18n_model_node_field_status"), key: "nodeStatus", sort: false},
        {value: Translator.get("i18n_model_node_field_create_time"), key: "nodeCreateTime", sort: false},
    ];

    $scope.classMap = {
        running: 'status-button-success',
        installFaild: 'status-button-error',
        startFaild: 'status-button-error',
        stopped: 'status-button-error',
        installing: 'status-button-running',
        starting: 'status-button-running',
        stopping: 'status-button-running',
        stopFaild: 'status-button-error'
    }




    $scope.list = function () {
        if(!$scope.model_name || !$scope.current_module) return;
        let url = "modelManager/node/" + $scope.current_module;
        $scope.formatModuleStatus();
        HttpUtils.paging($scope,  url, {}, resp => {
            $scope.items.forEach(item => {
                item.showLog = !$scope.is_mc && item.nodeStatus.indexOf("Faild") != -1;
                item.showStart = !$scope.is_mc && (item.nodeStatus == 'stopped' || item.nodeStatus == 'startFaild' || item.nodeStatus == 'startTimeOut');
                item.buttonClass = $scope.classMap[item.nodeStatus] || $scope.classMap['stopped'];
                item.loading = (item.nodeStatus.indexOf("ing") != -1 ) && item.nodeStatus != 'running' ;
            })
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
    $scope.showErrorLog = function(item) {
        sessionStorage.setItem("ModuleToLogParam", angular.toJson({
                label: '管理中心',
                value: 'management-center'
            }
        ));
        sessionStorage.setItem("LevelToLogParam", angular.toJson({
                label: 'ERROR',
                value: 'ERROR'
            }
        ));
        $state.go("log/system")
    }

    $scope.$on('onNodeRefresh',(evt,args) => {
        $scope.list();
    })
});

ProjectApp.controller('PodNumController', function($scope, HttpUtils, Translator, $state, $filter, Notification){

    $scope.old_pod_num = angular.copy($scope.k8sModule.podNum) || 0;
    $scope.value = $scope.old_pod_num;
    $scope.valueChanged = function(){
        let changed = $scope.value != $scope.old_pod_num;
        return changed;
    };
    $scope.modifyPodNum = function(){
        let pod_number = $scope.value;
        let item = $scope.k8sModule;
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }

        if (pod_number < 0){
            Notification.warn($filter('translator')('i18n_pod_number_limit', 'Pod 數量不能小于0'));
            return;
        }
        if(pod_number < 1){
            $scope.stopK8sModule(item);
            return;
        }
        let params = {"pod_number": pod_number};
        $scope.loadingLayer = HttpUtils.post('k8s-operator-module/start/' , {modules: module_arr, params: params}, function (resp) {
            $scope.closeToggleForm();
            Notification.info($filter('translator')('i18n_excute_resule', "执行成功，请稍后刷新模块状态.")) ;
            $scope.list();
        }, function (resp) {
        });
    };

    $scope.stopK8sModule = function (item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }
        if (module_arr.length == 0 ){
            $scope.showWarn('i18n_module_null_msg','模块不能为空.')
            return;
        }

        Notification.confirm($filter('translator')("i18n_stop_modules_confirm", "确定停止所选模块？"), function () {
            $scope.loadingLayer = HttpUtils.post('k8s-operator-module/stop/' , {modules: module_arr}, function () {
                $scope.closeToggleForm();
                Notification.info($filter('translator')('i18n_excute_resule', "执行成功，请稍后刷新模块状态.")) ;
                $scope.list();
            }, function (resp) {
            });

        })
    }

});








