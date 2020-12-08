(function () {
    let F2CProxy = angular.module('f2c.proxy', []);

    F2CProxy.filter("ScopeFilter", function ($filter) {
        const translator = $filter('translator');
        return function (str) {
            if (str) {
                switch (str) {
                    case "org" :
                        return translator('i18n_organization', "组织");
                    case "global" :
                        return translator('i18n_global', "全局");
                    case "workspace":
                        return translator('i18n_workspace', "工作空间");
                    default:
                        return null;
                }
            }
        }
    });

    F2CProxy.controller('ProxyCtrl', function ($scope, $mdDialog, UserService, $mdBottomSheet, FilterSearch, Notification, HttpUtils, $filter) {
        const translator = $filter('translator');

        // 定义搜索条件
        $scope.conditions = [];

        // 用于传入后台的参数
        $scope.filters = [];

        // 全选按钮，添加到$scope.columns
        $scope.first = {
            default: true,
            sort: false,
            type: "checkbox",
            checkValue: false,
            change: function (checked) {
                $scope.items.forEach(function (item) {
                    item.enable = checked;
                });
            },
            width: "40px"
        };

        $scope.showDetail = function (item) {
            // 点击2次关闭
            if ($scope.selected === item.$$hashKey) {
                $scope.closeInformation();
                return;
            }
            $scope.selected = item.$$hashKey;
            $scope.detail = item;
            $scope.showInformation();
        };

        $scope.closeInformation = function () {
            $scope.selected = "";
            $scope.toggleInfoForm(false);
        };

        $scope.columns = [
            {value: "Proxy Ip", key: "ip"},
            {value: "Port", key: "port"},
            {value: translator('i18n_proxy_username', "Proxy 用户名"), key: "username", sort: false, width: "20%"},
            {value: translator('i18n_proxy_scope', "可见级别"), key: "scope", width: "10%"},
            {value: translator('i18n_create_time', "创建时间"), key: "createdTime"},
        ];

        $scope.create = function () {
            // $scope.formUrl用于side-form
            $scope.formUrl = 'web-public/fit2cloud/html/proxy/proxy-add.html' + '?_t=' + Math.random();
            // toggleForm由side-form指令生成
            $scope.toggleForm();
        };


        $scope.checkRole = function (item) {

            if (item.scope === 'global') {
                if (UserService.isOrgAdmin()) {
                    return false;
                }

                if (UserService.isAdmin()) {
                    return true;
                }
            } else {
                return true;
            }

        };

        $scope.delete = function (item) {
            Notification.confirm(translator('i18n_confirm_delete', "确定删除？"), function () {
                HttpUtils.post("proxy/delete", item.id, function (data) {
                    Notification.success(translator('i18n_delete_success', "删除成功"));
                    $scope.list();
                });
            });
        };
        $scope.edit = function (item) {
            $scope.item = angular.copy(item);
            $scope.formUrl = 'web-public/fit2cloud/html/proxy/proxy-edit.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };


        $scope.list = function (sortObj) {
            var condition = FilterSearch.convert($scope.filters);
            if (sortObj) {
                $scope.sort = sortObj;
            }
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }
            HttpUtils.paging($scope, 'proxy/list', condition)
        };
        $scope.list();


    });


    F2CProxy.controller('ProxyEditCtrl', function ($scope, $http, HttpUtils, Notification, eyeService, $filter) {
        const translator = $filter('translator');

        $scope.view = function (password, eye) {
            eyeService.view("#" + password, "#" + eye);
        };

        $scope.submit = function () {
            $scope.loading = HttpUtils.post("proxy/update", $scope.item, function () {
                Notification.success(translator('i18n_save_success', "保存成功"));
                $scope.toggleForm();
                $scope.list();
            });
        };


    });


    F2CProxy.controller('ProxyAddCtrl', function ($scope, $http, HttpUtils, Notification, eyeService, $filter) {
        const translator = $filter('translator');
        $scope.item = {};
        $scope.view = function (password, eye) {
            eyeService.view("#" + password, "#" + eye);
        };

        $scope.submit = function () {
            $scope.loading = HttpUtils.post("proxy/create", $scope.item, function () {
                Notification.success(translator('i18n_save_success', "保存成功"));
                $scope.toggleForm();
                $scope.list();
            });
        };
    });
})();


