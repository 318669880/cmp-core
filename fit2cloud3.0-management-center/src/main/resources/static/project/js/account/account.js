ProjectApp.controller('AccountListController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, eyeService, $state, Translator) {

    $scope.pluginSelects = [];

    $scope.conditions = [
        {key: "name", name: Translator.get("i18n_account_cloud_name"), directive: "filter-contains"},
        {
            key: "pluginName",
            name: Translator.get("i18n_account_cloud_plugin"),
            directive: "filter-select",
            search: true,
            selects: $scope.pluginSelects
        },
        {
            key: "status",
            name: Translator.get("i18n_account_cloud_status"),
            directive: "filter-select", // 使用哪个指令
            selects: [
                {value: "VALID", label: Translator.get("i18n_account_cloud_valid")},
                {value: "INVALID", label: Translator.get("i18n_account_cloud_invalid")}
            ]
        }, {
            key: "syncStatus",
            name: Translator.get("i18n_account_cloud_sync_status"),
            directive: "filter-select",
            selects: [
                {value: "success", label: Translator.get("i18n_account_cloud_success")},
                {value: "error", label: Translator.get("i18n_account_cloud_fail")},
                {value: "sync", label: Translator.get("i18n_account_cloud_synching")},
                {value: "pending", label: Translator.get("i18n_account_cloud_pending")}
            ]
        }

    ];

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
        $scope.detail = item;
    };

    $scope.columns = [
        {value: Translator.get("i18n_account_cloud_name"), key: "cloud_account.name"},
        {value: Translator.get("i18n_account_cloud_plugin"), key: "pluginId", sort: false},
        {value: Translator.get("i18n_account_cloud_status"), key: "status", sort: false},
        {value: Translator.get("i18n_account_cloud_sync_status"), key: "email", sort: false},
        {value: Translator.get("i18n_account_cloud_last_sync_time"), key: "cloud_account.update_time"}
    ];

    if (AuthService.hasPermissions("CLOUD_ACCOUNT:READ+EDIT,CLOUD_ACCOUNT:READ+VALIDATE,CLOUD_ACCOUNT:READ+SYNC,CLOUD_ACCOUNT:READ+DELETE")) {
        $scope.columns.push({value: "", default: true, sort: false});
    }

    if (AuthService.hasPermissions("CLOUD_ACCOUNT:READ+BATCH_SYNC,CLOUD_ACCOUNT:READ+BATCH_DELETE")) {
        $scope.columns.unshift($scope.first);
    }

    $scope.list = function (sortObj) {
        var condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        condition.pluginType = $scope.pluginType;

        HttpUtils.paging($scope, 'cloud/account/list', condition)

    };

    $scope.init = function (type) {
        $scope.pluginType = type;
        $scope.list();

        HttpUtils.get("plugin/all", function (response) {
            $scope.pluginList = [];
            angular.forEach(response.data, function (item) {
                if (item.pluginType === type) {
                    $scope.pluginList.push(item);
                    let pluginItem = {};
                    pluginItem.value = item.name;
                    pluginItem.label = item.description;
                    pluginItem.icon = item.icon;
                    $scope.pluginSelects.push(pluginItem);
                }
            });
        });
    };


    $scope.changePlugin = function (pluginName, type) {
        HttpUtils.get("plugin/" + pluginName, function (response) {
            $scope.tmp = {};
            var fromJson = angular.fromJson(response.data);
            $scope.tmpList = fromJson.data;
            if (type === 'edit') {
                var credentials = angular.fromJson($scope.item.credential);
                angular.forEach($scope.tmpList, function (tmp) {
                    if (credentials[tmp.name] === undefined) {
                        tmp.input = tmp.defaultValue;
                    } else {
                        tmp.input = credentials[tmp.name];
                    }
                });
            } else {
                angular.forEach($scope.tmpList, function (tmp) {
                    if (tmp.defaultValue !== undefined) {
                        tmp.input = tmp.defaultValue;
                    }
                });
            }
        });
    };


    $scope.add = function () {
        $scope.tmpList = {};
        $scope.item = {};
        $scope.formUrl = 'project/html/account/account-add.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };

    $scope.edit = function (item) {
        $scope.tmpList = {};
        $scope.item = {};
        $scope.item = item;
        $scope.changePlugin($scope.item.pluginName, 'edit');
        $scope.formUrl = 'project/html/account/account-edit.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };

    $scope.saveAccount = function (item, type) {
        if (!$scope.tmpList.length) {
            Notification.danger(Translator.get("i18n_account_cloud_plugin_param"));
            return;
        }
        var data = {}, key = {};
        angular.forEach($scope.tmpList, function (tmp) {
            key[tmp.name] = tmp.input;
        });
        data["credential"] = angular.toJson(key);
        data["name"] = item.name;
        data["pluginName"] = item.pluginName;

        if (type === 'add') {
            $scope.loadingLayer1 = HttpUtils.post("cloud/account/add", data, function (response) {
                Notification.success(Translator.get("i18n_mc_create_success"));
                $scope.toggleForm();
                $scope.sync(response.data);
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_create_fail") + "," + response.message);
            });
        } else {
            data["id"] = item.id;
            $scope.loadingLayer1 = HttpUtils.post("cloud/account/update", data, function (response) {
                Notification.success(Translator.get("i18n_mc_update_success"));
                $scope.toggleForm();
                $scope.sync(response.data);
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_update_fail") + "," + response.message);
            });
        }
    };

    $scope.validate = function (item) {
        $scope.loadingLayer = HttpUtils.post("cloud/account/validate/" + item.id, {}, function (response) {
            if (response.data) {
                Notification.success(Translator.get("i18n_account_cloud_valid_msg"));
                $scope.list();
            } else {
                Notification.danger(Translator.get("i18n_account_cloud_invalid_msg"));
                $scope.list();
            }

        }, function (response) {
            Notification.danger(Translator.get("i18n_account_cloud_invalid_msg") + "," + response.message);
        });
    };

    $scope.getItemIds = function (ids) {
        angular.forEach($scope.items, function (item) {
            if (item.enable) {
                ids.push(item.id);
            }
        });
    };
    $scope.checkedPengding = function (item) {
        // 返回值恒等于false 因为该功能与新增涨后立即同步功能有冲突 暂时废弃
        return false && item && (item.syncStatus === 'pending' || item.syncStatus === 'sync') || $scope.items.some((item) => item.enable && (item.syncStatus === 'pending' || item.syncStatus === 'sync'));
    };

    $scope.sync = function (item) {
        if (item) {
            if ($scope.checkedPengding(item)) {//这里虽然后台可以保证幂等性 但是对数据库压力较大 所以 加个连点的约束
                Notification.info(Translator.get("i18n_check_pending_item"));
                return;
            }
            $scope.loadingLayer = HttpUtils.post("cloud/account/sync/" + item.id, {}, function () {
                $scope.list();
                $scope.checkStatus();
            }, function (response) {
                Notification.danger(Translator.get("i18n_account_cloud_sync_task_fail") + "," + response.message);
            });
        } else {
            let ids = [];
            $scope.getItemIds(ids);
            if (ids.length === 0) {
                Notification.info(Translator.get("i18n_no_selected_item"));
            } else if ($scope.checkedPengding()) {//这里虽然后台可以保证幂等性 但是对数据库压力较大 所以 加个连点的约束
                Notification.info(Translator.get("i18n_check_pending_item"));
            } else {
                $scope.loadingLayer = HttpUtils.post("cloud/account/batch/sync", ids, function () {
                    Notification.success(Translator.get("i18n_account_cloud_sync_task_success"));
                    $scope.list();
                    $scope.checkStatus();
                }, function (response) {
                    Notification.danger(Translator.get("i18n_account_cloud_sync_task_fail") + "," + response.message);
                });
            }
        }
    };

    $scope.delete = function (item) {
        if (item) {
            Notification.confirm(Translator.get("i18n_account_cloud_delete_msg"), function () {
                $scope.loadingLayer = HttpUtils.post("cloud/account/delete/" + item.id, {}, function () {
                    Notification.success(Translator.get("i18n_mc_delete_success"));
                    $scope.list();
                }, function (response) {
                    Notification.danger(Translator.get("i18n_mc_delete_fail") + "," + response.message);
                });
            })
        } else {
            let ids = [];
            $scope.getItemIds(ids);
            if (ids.length === 0) {
                Notification.info(Translator.get("i18n_no_selected_item"));
                return;
            }
            Notification.confirm(Translator.get("i18n_account_cloud_delete_msg"), function () {
                $scope.loadingLayer = HttpUtils.post("cloud/account/batch/delete", ids, function () {
                    Notification.success(Translator.get("i18n_mc_delete_success"));
                    $scope.list();
                }, function (response) {
                    Notification.danger(Translator.get("i18n_mc_delete_fail") + "," + response.message);
                });
            })
        }
    };

    $scope.checkStatus = function () {
        $scope.timer = $interval(function () {
            var condition = FilterSearch.convert($scope.filters);
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }

            HttpUtils.post("cloud/account/list/" + $scope.pagination.page + "/" + $scope.pagination.limit, condition, function (response) {
                var items = response.data.listObject;
                for (var i = 0; i < items.length; i++) {
                    angular.forEach($scope.items, function (item) {
                        if (item.id === items[i].id) {
                            item.syncStatus = items[i].syncStatus;
                            item.status = items[i].status;
                        }
                    });
                }
                var i = 0;
                angular.forEach($scope.items, function (item) {
                    if (item.syncStatus === 'sync') {
                        i++;
                    }
                });
                if (i > 0) {
                    $scope.syncStatusCancel = true;
                } else {
                    $scope.syncStatusCancel = false;
                }
            });

        }, 10000);
    };
    $scope.checkStatus();

    $scope.$watch('syncStatusCancel', function (newValue) {

        if (!newValue) {
            if ($scope.timer) {
                $interval.cancel($scope.timer);
            }
            $scope.syncStatusCancel = false;
        }
    });

    $scope.$on("$destroy", function () {
        if ($scope.timer) {
            $interval.cancel($scope.timer);
        }
    });

    $scope.view = function (index) {
        eyeService.view('#password' + index, '#eye' + index);
    };

    $scope.goSystemLog = function (item) {
        sessionStorage.setItem("AccountToLogParam", angular.toJson({
                label: item.name,
                value: item.name
            }
        ));
        $state.go("log/system")
    }
});


