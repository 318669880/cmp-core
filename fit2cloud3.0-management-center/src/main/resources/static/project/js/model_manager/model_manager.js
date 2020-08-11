



ProjectApp.controller('ModelManagerController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, $state, $filter,Translator) {
    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t" + window.appversion;
    $scope.indexServer = new IndexServer($scope);
    $scope.modelInstaller = new ModelInstaller($scope);

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
            /*{
                id: "3",
                name: $filter('translator')('i18n_model_runner', '模块运行'),
                select: function () {
                    $scope.currentIndex = 3;
                    $scope.list();
                },
                next: function () {
                    return true;
                }
            }*/
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
        {value: '名称', key: "name", sort: false},
        {value: '模块', key: "module", sort: false},
        {value: '是否运行', key: "currentStatus", sort: false},
        {value: '版本', key: "lastRevision", sort: false},
        {value: '安装时间', key: "installTime", sort: false},
        {value: '概诉', key: "overview", sort: false}
    ];

    $scope.list = function (sortObj) {
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
        });
    };

    $scope.start = function(item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }
        $scope.executeAjax('model_manager/operate/start','POST',{module_arr:module_arr}, resp => {
            $scope.list();
        })
    };

    $scope.stop = function(item) {
        let module_arr = [];
        if(item){
            module_arr.push(item.module);
        }else{
            module_arr = $scope.items.filter(item => item.enable).map(item => item.module);
        }
        $scope.executeAjax('model_manager/operate/stop','POST',{module_arr:module_arr}, resp => {
            $scope.list();
        })
    };

    $scope.envs = [
        {
            'value': 'single-vim',
            'name': 'single-vim'
        },{
            'value': 'k8s',
            'name': 'k8s'
        }
    ];



});


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
    this.$scope = null;
    this.autoNext = true;
    this.model_env = 'single-vim';
    this.onLine = true;
    this.initialize.apply(this , arguments);
};
IndexServer.prototype = {
    initialize: function ($scope) {
        //this.address = arguments[0];
        this.$scope = $scope;
    },

    loadData: function() {
        let _self = this;
        this.$scope.executeAjax(this._loadDataUrl,'GET',{},response => {

            _self._init_address = response.modelAddress
            _self.address = response.modelAddress;

            _self._init_onLine = !!response.onLine;
            _self.onLine = !!response.onLine;

            _self._init_model_env = response.env;
            _self.model_env = response.env;

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
            this.address = "/";
        }
        let _self = this;
        if(!this.address){
            this.$scope.showError('i18n_name_require', '索引服务不能为空');
            this.validate = false;
            return;
        }
        this.$scope.executeAjax(this.address+"/json/data.js",'GET', {remarks: 'query_version_json'}, function(text){
            try {
                if(!!text && text.indexOf('let templateDate') !=-1){
                    let json = text.substr(text.indexOf("{"));
                    this.$scope.model_version_info = JSON.parse(json);
                    for (let x in this.$scope.model_version_info) {
                        this.$scope.model_version_info[x].forEach(basicModel => basicModel.lastRevision = null)
                    }
                    if(isvalidate){
                        this.$scope.wizard.api.next();
                        this.autoNext = false;
                        return;
                    }
                    this.validateSave();
                }
            }catch (e) {
                this.validate = false;
                this.$scope.showError(null,'索引地址无效');
                throw e;
            }
        }.bind(this));
        this.validate = false;
        return false;
    },

    validateSave: function() {
        if(this._init_address === this.address){
            // 这说明 索引服务没有改过 为提升那么一点客户体验 那就不走后台了保存了
            this.validate = true;
            this.$scope.wizard.continue();
            return true;
        }
        //let saveSuccess = false;
        let _self = this;

        let param = {
            modelAddress : this.address,             // 索引服务地址
            validate : 1,                            // 验证结果
            onLine : this.onLine ? 1 : 0,                              // 类型 1是在线 0是离线 此为备用字段
            env : (this.model_env || 'single-vim')                   // 环境 默认是single-vim 可选 k8s
        }
        this.$scope.executeAjax(this._saveDataUrl,'POST',param,res => {
            //saveSuccess = true;
            _self._init_address = _self.address;
            _self.validate = true;
            _self.$scope.wizard.continue();
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
    this.$scope = null;
    this._loadLocalDatasUrl = 'modelManager/indexInstaller/modelInstallInfos';
    this._batchInstallUrl = 'modelManager/operate/install';
    this._batchUninstallUrl = 'modelManager/operate/unUninstall';
    this._localData = null; //本地数据集合
    this._installValidate = false;
    this.initialize.apply(this , arguments);
}
ModelInstaller.prototype = {
    initialize: function ($scope) {
        this._localData = Object.create({});

        this.$scope = $scope;

        this._initColumns();
    },

    _initColumns: function () {

        let checkColumn = {
            default: true,
            sort: false,
            type: "checkbox",
            checkValue: false,
            change: function (checked) {
                this.$scope.installableItems.forEach(function (item) {
                    item.enable = checked;
                });
            }.bind(this),
            width: "40px"
        }
        this.installableColumns = [
            checkColumn,
            {value: '名称', key: "name", sort: false},
            {value: '模块', key: "module", sort: false},
            {value: '版本', key: "lastRevision", sort: false},
            {value: '概诉', key: "overview", sort: false},
            // {value: '操作', key: "name", sort: false}
        ];


        this.installedColumns = [
            {value: '名称', key: "name", sort: false},
            {value: '模块', key: "module", sort: false},
            {value: '版本', key: "lastRevision", sort: false},
            {value: '安装时间', key: "installTime", sort: false},
            {value: '概诉', key: "overview", sort: false}
        ];
        this.conditions = [
            {key: "name", name: "名称", directive: "filter-contains"},
            {key: "module", name: "模块", directive: "filter-contains"}
        ];
        this.filters = [];


        this.installupdateColumns = [
            {value: '名称', key: "name", sort: false},
            {value: '模块', key: "module", sort: false},
            {value: '当前版本', key: "current_version", sort: false},
            {value: '安装时间', key: "installTime", sort: false},
            {value: '可选版本', key: "lastRevision", sort: false},
            {value: '概诉', key: "overview", sort: false}
        ];
    },

    _clearData: function () {
        this._localData = Object.create({});
        this.$scope.installableItems = null;
        this.$scope.installupdateItems = null;
        this.$scope.installedItems = null;
        this.$scope.currentRevisions = null;
        this.$scope.currentUpdateRevisions = null;
        this._installValidate = false;
    },

    loadData: function () {
        this._clearData();
        // 获取json文件中模块数据与本地数据库对比
        let _self = this;
        this.$scope.executeAjax(this._loadLocalDatasUrl,'GET',null,(res) => {
            !!res && res.forEach(item => _self._localData[item.module] = item);
            _self.loadInstallable();_self.loadUpdates();
            // _self.loadInstalled();
            _self.$scope.list();
        });
    },

    saveData: function () {
        // 存储到本地数据库
        if(!this._installValidate){
            this.$scope.showError(null,'请先安装至少一个模块');
            return false;
        }
        return true;
    },


    // 加载可安装数据
    loadInstallable: function () {
        let _self = this;
        let models = []
        for (let modelVersionInfoKey in this.$scope.model_version_info) {
            let item = this.$scope.model_version_info[modelVersionInfoKey];
            (item instanceof Array) && (models = models.concat(item));
        }
        this.$scope.installableItems = models.filter(model => {
            return !_self._localData.hasOwnProperty(model.module);
        }).map(model => {
            model.lastRevision = null;
            model.remoteImageUrl = model.icon.indexOf(_self.$scope.indexServer.address)==-1 ? (_self.$scope.indexServer.address+ "/" + model.icon) : model.icon;
            model.enable = false;
            model.lastRevision = _self._lastVersion(model);
            model._versionEdit = false;//默认是非编辑状态
            return model;
        });
        console.log('------');
    },

    editVersion: function (item) {
        this.endEditAll();
        if(item._versionEdit) return;
        item.last_select_value = item.last_select_value  || item.lastRevision;
        this.$scope.currentRevisions = item.revisions.concat([{
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
        this.$scope.installableItems.forEach( item => item._versionEdit = false);
    },

    //  执行安装
    executeInstall: function () {
        let _self = this;
        let param = this.$scope.installableItems.filter(model => model.enable === true).map(model => {
            let dto = Object.create({});
            model.lastRevision = model.lastRevision || _self._lastVersion(model);
            dto.modelBasic = model;
            dto.modelBasic.icon = model.remoteImageUrl;
            let modelVersion = model.last_version;
            modelVersion.created = new Date(modelVersion.created).getTime();

            dto.modelVersion = modelVersion
            return dto;
        });
        this.$scope.executeAjax(this._batchInstallUrl,'POST',param, (resp) => {
            _self.loadData();
        })

    },

    // 加载可更新数据
    loadUpdates: function () {
        let _self = this;
        let models = []
        for (let modelVersionInfoKey in this.$scope.model_version_info) {
            let item = this.$scope.model_version_info[modelVersionInfoKey];
            (item instanceof Array) && (models = models.concat(item));
        }
        this.$scope.installupdateItems = models.filter(model => {
            let localData = _self._localData[model.module];
            if (!localData) return false;
            let lastVersion = _self._lastVersion(model);
            return localData.lastRevision !== lastVersion;
        }).map(model => {
            model.current_version = _self._localData[model.module].lastRevision;
            model.lastRevision = model.last_version.revision;
            model._versionEdit = false;
            model.installTime = _self._localData[model.module].installTime;
            model.icon = _self._localData[model.module].icon;
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
        this.$scope.currentUpdateRevisions = item._update_options.concat([{
            "revision": "取消",
            "description": "selectcancel",
            "created": "cancel",
            "downloadUrl": "cancel"
        }]);
        item._versionEdit = true;
    },


    endEditAllUpdate: function() {
        this.$scope.installupdateItems.forEach( item => item._versionEdit = false);
    },


    //  执行更新
    executeUpdate: function () {
        let _self = this;
        let opmodel = null;
        let param = this.$scope.installupdateItems.filter(model => model.enable === true).map(model => {
            opmodel = model;
            let dto = Object.create({});
            model.lastRevision = model.lastRevision || _self._lastVersion(model);
            dto.modelBasic = model;
            let modelVersion = model.last_version;
            modelVersion.created = new Date(modelVersion.created).getTime();
            dto.modelVersion = modelVersion
            return dto;
        });
        this.$scope.executeAjax(this._batchInstallUrl,'POST',param, (resp) => {
            opmodel.enable = false;
            _self.loadData();
        })
        opmodel.enable = false;
    },

    // 加载已安装数据
    loadInstalled: function () {
        this.$scope.installedItems = [];
        for (let localDataKey in this._localData) {
            this.$scope.installedItems.push(this._localData[localDataKey])
        }
        this._installValidate = !!this.$scope.installedItems && this.$scope.installedItems.length > 0;
    },

    //卸载模块
    unInstall: function () {
        let _self = this;
        let model_uuid_array = this.$scope.items.filter(model => model.enable === true).map(model =>  model.modelUuid);
        this.$scope.executeAjax(this._batchUninstallUrl,'POST',model_uuid_array, (resp) => {
            _self.loadData();
        })
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
        for (let modelVersionInfoKey in this.$scope.model_version_info) {
            let item = this.$scope.model_version_info[modelVersionInfoKey];
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

};






