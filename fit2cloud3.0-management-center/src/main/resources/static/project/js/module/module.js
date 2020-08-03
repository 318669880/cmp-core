ProjectApp.controller('ModuleListController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, $state, Translator) {
    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t" + window.appversion;
    $scope.conditions = [];
    // 用于传入后台的参数
    $scope.filters = [];


    $scope.tags = [
        {
            tagKey: "all",
            tagValue: Translator.get("i18n_all"),
            selected: true
        },
        {
            tagKey: "infra",
            tagValue: Translator.get("i18n_module_base"),
            selected: false
        },
        {
            tagKey: "application",
            tagValue: Translator.get("i18n_module_application"),
            selected: false
        },
        {
            tagKey: "link",
            tagValue: Translator.get("i18n_module_external_link"),
            selected: false
        }
    ];

    $scope.changeActive = function (item) {
        HttpUtils.post("module/active/" + item.id + "/" + item.active, {}, function () {
            $scope.list();
        });
    };

    $scope.infraList = [];
    $scope.applicationList = [];
    $scope.linkList = [];

    $scope.convertResults = function (type, name) {
        $scope.infraList = [];
        $scope.applicationList = [];
        $scope.linkList = [];

        angular.forEach($scope.items, function (item) {
            if (item.id === 'dashboard' || item.id === 'management-center' || item.id === 'gateway') {
                if (type === 'all' || type === 'infra') {
                    if (!(name && item.name.toLowerCase().indexOf(name.toLowerCase()) === -1)) {
                        $scope.infraList.push(item);
                    }
                }
            } else if (item.type === 'standard' || item.type === 'extension') {
                if (type === 'all' || type === 'application') {
                    if (!(name && item.name.toLowerCase().indexOf(name.toLowerCase()) === -1)) {
                        $scope.applicationList.push(item);
                    }
                }
            } else if (item.type === 'link') {
                if (type === 'all' || type === 'link') {
                    if (!(name && item.name.toLowerCase().indexOf(name.toLowerCase()) === -1)) {
                        $scope.linkList.push(item);
                    }
                }
            }
        });

    };


    $scope.selectTag = function (key) {
        angular.forEach($scope.tags, function (tag) {
            if (tag.tagKey === key) {
                $scope.convertResults(key, $scope.param);
                tag.selected = true;
            } else {
                tag.selected = false;
            }
        })
    };

    $scope.search = function (param) {
        angular.forEach($scope.tags, function (tag) {
            if (tag.selected) {
                $scope.convertResults(tag.tagKey, param);
            }
        })
    };

    $scope.list = function () {
        $scope.loadingLayer = HttpUtils.get('module/list', function (response) {
            $scope.items = response.data;

            let checkTag = false;
            angular.forEach($scope.tags, function (tag) {
                if (tag.selected) {
                    $scope.convertResults(tag.tagKey, $scope.param);
                    checkTag = true;
                }
            });

            if (!checkTag) {
                $scope.convertResults('all');
            }
        })

    };

    $scope.list();


    $scope.goToLog = function (item) {
        sessionStorage.setItem("ModuleToLogParam", angular.toJson({
                label: item.name,
                value: item.id
            }
        ));
        $state.go("log/system")
    };


    $scope.add = function () {
        $scope.item = {};
        $scope.formUrl = 'project/html/module/module-add.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };

    $scope.edit = function (item) {
        $scope.item = angular.copy(item);
        $scope.formUrl = 'project/html/module/module-edit.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };

    $scope.saveModule = function (item, type) {
        if (type === 'add') {
            HttpUtils.post('module/add', item, function () {
                Notification.success(Translator.get("i18n_mc_create_success"));
                $scope.toggleForm();
                $scope.list();
            });
        } else {
            HttpUtils.post('module/update', item, function () {
                Notification.success(Translator.get("i18n_mc_update_success"));
                $scope.toggleForm();
                $scope.list();
            });
        }

    };

    $scope.deleteModule = function (id) {
        HttpUtils.post('module/delete/' + id, {}, function () {
            Notification.success(Translator.get("i18n_mc_delete_success"));
            $scope.list();
        });
    }

});


