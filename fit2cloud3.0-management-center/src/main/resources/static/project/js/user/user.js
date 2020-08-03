ProjectApp.controller('UserController', function ($scope, HttpUtils, FilterSearch, $http, Notification, operationArr, eyeService, $state, $stateParams, ProhibitPrompts, UserService, AuthService, Loading, Translator) {

        // 定义搜索条件
        $scope.conditions = [
            {key: "id", name: "ID", directive: "filter-contains"},
            {key: "name", name: Translator.get("i18n_user_name_"), directive: "filter-contains"},
            {key: "email", name: Translator.get("i18n_email"), directive: "filter-contains"},
            //增加一个异步字典转换的例子，将请求内容转换为value,label格式
            {
                key: "roleId",
                name: Translator.get("i18n_role"),
                directive: "filter-select-virtual",
                url: "role",
                convert: {value: "id", label: "name"}
            }
        ];

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

        $scope.columns = [
            {value: "ID", key: "id", sort: false},
            {value: Translator.get("i18n_user_name_"), key: "name", sort: false},
            {value: Translator.get("i18n_email"), key: "email", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_role"), key: "roleName", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_user_source"), key: "source"},
            {value: Translator.get("i18n_status"), key: "active"},
            {value: Translator.get("i18n_phone"), key: "phone", sort: false},
            {value: Translator.get("i18n_create_time"), key: "user.create_time"}
        ];
        if (AuthService.hasPermissions("USER:READ+EDIT,USER:READ+DELETE,USER:READ+RESET_PASSWORD,USER:READ+LOG")) {
            $scope.columns.push({value: "", default: true, sort: false});
        }

        if (AuthService.hasPermissions("USER:READ+ADD_ROLE")) {
            $scope.columns.unshift($scope.first);
        }

        // 用于传入后台的参数
        $scope.filters = [];

        $scope.list = function (sortObj) {
            const condition = FilterSearch.convert($scope.filters);
            if (sortObj) {
                $scope.sort = sortObj;
            }
            // 保留排序条件，用于分页
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }
            HttpUtils.paging($scope, "user", condition, function () {
            });
        };

        $scope.list();

        $scope.getItemIds = function (ids) {
            angular.forEach($scope.items, function (item) {
                if (item.enable) {
                    ids.push(item.id);
                }
            });
        };
        $scope.addUserRole = function () {
            $scope.ids = [];
            $scope.getItemIds($scope.ids);
            if ($scope.ids.length === 0) {
                Notification.info(Translator.get("i18n_no_selected_item"));
            } else {
                $scope.item = {};
                $scope.item.userIdList = $scope.ids;
                $scope.isAddLineAble = true;
                $scope.item.roleInfoList = [];
                $scope.addLine();
                $scope.formUrl = 'project/html/user/user-add-role.html' + '?_t=' + Math.random();
                $scope.toggleForm();
            }
        };

        $scope.submitUserRole = function (item) {
            HttpUtils.post("user/role/add", item, function () {
                Notification.success(Translator.get("i18n_mc_add_success"));
                $scope.closeToggleForm();
                $scope.list();
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_add_fail") + "，" + response.message);
            })
        };

        $scope.create = function () {
            $scope.item = {};
            $scope.isAddLineAble = true;
            $scope.item.roleInfoList = [];
            $scope.addLine();
            $scope.formUrl = 'project/html/user/user-add.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };

        $scope.addLine = function () {
            let roleInfo = {};
            roleInfo.length = {"width": '100%'};
            roleInfo.selects = [];
            angular.forEach($scope.item.roleInfoList, function (info) {
                roleInfo.selects.push(info.roleId);
            });
            $scope.item.roleInfoList.push(roleInfo);
            if ($scope.item.roleInfoList && $scope.roles) {
                if ($scope.item.roleInfoList.length === $scope.roles.length) {
                    $scope.isAddLineAble = false;
                }
            }
        };
        $scope.subtractLine = function (info) {
            operationArr.removeByValue($scope.item.roleInfoList, info);
            if ($scope.item.roleInfoList.length !== $scope.roles.length) {
                $scope.isAddLineAble = true;
            }
        };

        $scope.filterRole = function (role, roleInfo) {
            let value = true;
            if (roleInfo.selects.length === 0) {
                value = true;
            }
            angular.forEach(roleInfo.selects, function (roleId) {
                if (role.id === roleId) {
                    value = false;
                }
            });
            return value;
        };

        $scope.showResources = function (item) {
            if ($scope.selected === item.$$hashKey) {
                $scope.closeInformation();
                $scope.selected = null;
                return;
            }
            $scope.selected = item.$$hashKey;
            $scope.showResourceInfo = {user: item};
            $scope.showInformation();
        };

        $scope.showInformation = function () {
            $scope.infoUrl = 'project/html/user/resource-list.html' + '?_t=' + Math.random();
            $scope.toggleInfoForm(true);
        };

        $scope.closeInformation = function () {
            $scope.toggleInfoForm(false);
        };


        $scope.changeActive = function (user) {
            let message = null;
            if (!user.active) {
                message = Translator.get("i18n_user_enable_success");
            } else {
                message = Translator.get("i18n_user_disable_success");
            }
            $scope.item = {};
            $scope.item.id = user.id;
            $scope.item.roleId = user.roleId;
            $scope.item.active = !user.active;
            $scope.item.roleIds = user.roleIds;
            HttpUtils.post("user/active", $scope.item, function () {
                Notification.success(message);
                $scope.item = {};
            }, function (rep) {
                $scope.list();
                Notification.danger(rep.message);
            })
        };

        $scope.isRePassword = function (item) {
            let flag = true;
            if (item.source === "extra") {
                return false;
            } else {
                if ($scope.currentRole === $scope.roleConst.orgAdmin) {
                    angular.forEach(item.roles, function (currentRole) {
                        if (currentRole.parentId === $scope.roleConst.orgAdmin || currentRole.parentId === $scope.roleConst.admin) {
                            flag = false;
                        }
                    })
                }
                return flag;
            }
        };

        $scope.changeType = function (id) {
            ProhibitPrompts.changeType(id);
        };

        $scope.edit = function (data) {
            $scope.item = angular.copy(data);
            $scope.select = {};
            $scope.isAddLineAble = true;
            Loading.add(
                $http.get("user/role/info/" + data.id).then(function (rep) {
                    $scope.item.roleInfoList = [];
                    angular.forEach(rep.data.data, function (roleInfo) {
                        let parentId = $scope.getParentId(roleInfo.roleId);
                        roleInfo.selects = [];
                        if ($scope.currentRole === $scope.roleConst.orgAdmin) {
                            if (parentId === $scope.roleConst.orgAdmin) {
                                roleInfo.roleType = $scope.roleConst.orgAdmin;
                                Loading.add(HttpUtils.get("user/ids/" + data.id, function (rep) {
                                    $scope.select.organizationIds = rep.data;
                                }));
                                $scope.item.roleInfoList.push(roleInfo)
                            }
                            if (parentId === $scope.roleConst.user) {
                                roleInfo.roleType = $scope.roleConst.user;
                                Loading.add(HttpUtils.get("user/ids/" + data.id, function (rep) {
                                    $scope.select.workspaceIds = rep.data;
                                }));
                                $scope.item.roleInfoList.push(roleInfo)
                            }

                        }
                        if ($scope.currentRole === $scope.roleConst.admin) {
                            if (parentId === $scope.roleConst.orgAdmin) {
                                roleInfo.roleType = $scope.roleConst.orgAdmin;
                                Loading.add(HttpUtils.get("user/ids/" + data.id, function (rep) {
                                    $scope.select.organizationIds = rep.data;
                                }));
                            }
                            if (parentId === $scope.roleConst.user) {
                                roleInfo.roleType = $scope.roleConst.user;
                                Loading.add(HttpUtils.get("user/ids/" + data.id, function (rep) {
                                    $scope.select.workspaceIds = rep.data;
                                }));
                            }
                            if (parentId === $scope.roleConst.admin) {
                                roleInfo.roleType = $scope.roleConst.admin;
                            }
                            if (parentId === 'other') {
                                roleInfo.roleType = 'other';
                            }
                            $scope.item.roleInfoList.push(roleInfo);
                        }
                    });
                    if ($scope.item.roleInfoList.length === $scope.roles.length) {
                        $scope.isAddLineAble = false;
                    }
                }));
            $scope.editLoadingLayer = Loading.load();
            $scope.formUrl = 'project/html/user/user-edit.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };

        $scope.changeRole = function (roleInfo, roleId) {
            roleInfo.roleType = $scope.getParentId(roleId);
            switch (roleInfo.roleType) {
                case $scope.roleConst.admin:
                    roleInfo.length = {"width": '100%'};
                    break;
                case $scope.roleConst.orgAdmin:
                    roleInfo.length = {"width": '50%'};
                    break;
                case $scope.roleConst.user:
                    roleInfo.length = {"width": '33%'};
                    break;
            }
            if ($scope.item.roleInfoList.length === $scope.roles.length) {
                $scope.isAddLineAble = false;
            }
        };

        $scope.getParentId = function (roleId) {
            let parentId = null;
            angular.forEach($scope.roles, function (role) {
                if (role.id === roleId) {
                    if (role.type !== 'System') {
                        parentId = role.parentId;
                    } else {
                        parentId = roleId;
                    }
                }
            });
            return parentId;
        };

        $scope.resetPassword = function (item) {
            $scope.resetItem = item;
            $scope.formUrl = 'project/html/user/user-reset-password.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };
        $scope.acquisitionConditions = function () {
            HttpUtils.get("role", function (rep) {
                $scope.roles = rep.data;
            });
            if ($scope.currentRole === $scope.roleConst.orgAdmin) {
                HttpUtils.get("organization/currentOrganization", function (rep) {
                    $scope.orgs = rep.data;
                });
            } else {
                HttpUtils.get("organization", function (rep) {
                    $scope.orgs = rep.data;
                });
            }

            HttpUtils.get("workspace", function (rep) {
                $scope.workspaces = rep.data;
            });
        };

        $scope.acquisitionConditions();


        $scope.submitFlag = false;
        $scope.submit = function (type, data) {
            $scope.submitFlag = true;
            if (type === 'add') {
                data.source = 'local';
                HttpUtils.post("user/add", data, function () {
                    Notification.success(Translator.get("i18n_mc_create_success"));
                    $scope.closeToggleForm();
                    $scope.list();
                    $scope.acquisitionConditions();
                    $scope.submitFlag = false;
                }, function (rep) {
                    $scope.submitFlag = false;
                    Notification.danger(rep.message);
                })
            }
            if (type === 'edit') {
                HttpUtils.post('user/update', data, function () {
                    Notification.success(Translator.get("i18n_mc_update_success"));
                    $scope.closeToggleForm();
                    $scope.list();
                    $scope.submitFlag = false;
                }, function (rep) {
                    $scope.submitFlag = false;
                    Notification.danger(rep.message);
                });
            }
            if (type === 'resetPassword') {
                HttpUtils.post("user/reset/password", data, function () {
                    Notification.success(Translator.get("i18n_mc_update_success"));
                    $scope.closeToggleForm();
                    $scope.list();
                    $scope.submitFlag = false;
                }, function (rep) {
                    $scope.submitFlag = false;
                    Notification.danger(rep.message);
                })
            }
        };

        $scope.delete = function (user) {
            Notification.confirm(Translator.get("i18n_menu_delete_confirm"), function () {
                $http.get("user/delete/" + user.id).then(function () {
                    Notification.success(Translator.get("i18n_mc_delete_success"));
                    $scope.list();
                }, function (rep) {
                    Notification.danger(rep.data.message)
                })
            });
        };


        $scope.closeToggleForm = function () {
            $scope.item = {};
            $scope.toggleForm();
            $scope.resetItem = {}
        };

        $scope.view = function (password, eye) {
            eyeService.view("#" + password, "#" + eye);
        };

        $scope.goExtraUsers = function () {
            $scope.item = {};
            $scope.isAddLineAble = true;
            $scope.item.roleInfoList = [];
            $scope.formUrl = 'project/html/user/import-users.html' + '?_t=' + Math.random();
            $scope.toggleForm();
            $scope.show = true;
        };

        $scope.export = function (sortObj) {
            let condition = FilterSearch.convert($scope.filters);
            if (sortObj) {
                $scope.sort = sortObj;
            }
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }
            let columns = $scope.columns.filter(function (c) {
                return c.checked !== false && c.key;
            });

            $scope.loadingLayer = HttpUtils.download('user/export', {
                columns: columns,
                params: condition
            }, 'users.xlsx', 'application/octet-stream');
        };
    }
);


ProjectApp.controller('ResourceController', function ($scope, HttpUtils, $http) {

    $scope.roleTree = function () {
        $scope.showInfoName = $scope.showResourceInfo.user.name;
        $scope.treeData = [];
        $scope.noroot = {};
        $http.get("user/switch/source/" + $scope.showResourceInfo.user.id).then(function (rep) {
            $scope.rawData = rep.data.data;
            angular.forEach($scope.rawData, function (data) {
                data.collapsed = false;
                if (data.parentId == null) {
                    if (data.switchable) {
                        data.name = data.name + "[" + data.desc + "]"
                    }
                    $scope.treeData.push(data);
                }
            });
            angular.forEach($scope.treeData, function (treeData) {
                treeData.children = [];
                angular.forEach($scope.rawData, function (data) {
                    if (data.parentId === treeData.id) {
                        if (data.switchable) {
                            data.name = data.name + "[" + data.desc + "]"
                        }
                        treeData.children.push(data)
                    }
                });
                if (treeData.children.length === 0) {
                    treeData.children = null;
                }
            })

        }, function (rep) {

        });
    };
    $scope.roleTree();
});

ProjectApp.controller('ImportExtraUserController', function ($scope, HttpUtils, FilterSearch, $http, Notification, operationArr, Translator) {

    $scope.addLine();

    $scope.wizard = {
        // 按顺序显示,id必须唯一并需要与页面中的id一致，select为分步初始化方法，next为下一步方法(最后一步时作为提交方法)
        steps: [
            {
                id: "1",
                name: Translator.get("i18n_select_user"),
                select: function () {
                },
                next: function () {
                    let ids = [];
                    $scope.getItemIds(ids);
                    if (ids.length === 0) {
                        Notification.info(Translator.get("i18n_select_user_please"));
                        return false
                    } else {
                        $scope.item.ids = ids;
                        return true;
                    }
                }
            },
            {
                id: "2",
                name: Translator.get("i18n_select_role"),
                select: function () {
                },
                next: function () {
                    if ($scope.item.roleInfoList.length === 1 && angular.isUndefined($scope.item.roleInfoList[0].roleId)) {
                        Notification.warn(Translator.get("i18n_select_role_please"));
                        return false;
                    } else {
                        $http.post("extra/user/import", $scope.item.roleInfoList, {headers: {ids: $scope.item.ids}}).then(function () {
                            Notification.success(Translator.get("i18n_user_import_success"));
                            $scope.list();
                            $scope.closeToggleForm();
                        }, function (rep) {
                            Notification.danger(rep.data.message);
                        });
                    }
                }
            }
        ],
        // 嵌入页面需要指定关闭方法
        close: function () {
            $scope.show = false;
            $scope.closeToggleForm();
        }
    };

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

    $scope.getItemIds = function (ids) {
        angular.forEach($scope.items, function (item) {
            if (item.enable) {
                ids.push(item.id);
            }
        });
    };

    $scope.clickAllCheck = function (check) {
        $scope.items.forEach(function (item) {
            item.enable = !check;
        });
    };

    $scope.conditions = [
        {key: "name", name: "ID", directive: "filter-contains"},
        {key: "displayName", name: Translator.get("i18n_user_name_"), directive: "filter-contains"},
        {key: "email", name: Translator.get("i18n_email"), directive: "filter-contains"}
    ];

    $scope.filters = [];
    $scope.extraUsers = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "extra/user", condition, function () {
            angular.forEach($scope.items, function (item) {
                item.enable = false;
            })
        });
    };

    $scope.extraUsers();

    $scope.importExtraUsers = function () {
        $scope.formUrl = 'project/html/user/import-extra-users.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };
});
