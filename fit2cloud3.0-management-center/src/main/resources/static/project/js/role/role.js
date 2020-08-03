ProjectApp.controller('RoleController', function ($scope, $mdDialog, HttpUtils, FilterSearch, Notification, $http, Translator) {
        // 定义搜索条件
        $scope.conditions = [
            {key: "name", name: Translator.get("i18n_role_name"), directive: "filter-contains"},
        ];
        $scope.selectRoles = [];
        $scope.selectRoles.push({id: 'ADMIN', type: 'System', name: Translator.get('系统管理员')});
        $scope.selectRoles.push({id: 'ORGADMIN', type: 'System', name: Translator.get('组织管理员')});
        $scope.selectRoles.push({id: 'USER', type: 'System', name: Translator.get('工作空间用户')});
        $scope.selectRoles.push({id: 'other', type: 'System', name: Translator.get('i18n_role_type_custom')});


        // 用于传入后台的参数
        $scope.filters = [];


        $scope.columns = [
            {value: Translator.get("i18n_role_name"), key: "name", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_role_type"), key: "type", sort: false},
            {value: Translator.get("i18n_role_system"), key: "status", sort: false}
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
            HttpUtils.paging($scope, "role", condition, function () {
                // $scope.selectRoles = angular.copy($scope.items);
                // $scope.selectRoles.push({id: 'other', type: 'System', name: '自定义'});
            });
        };
        $scope.list();

        $scope.onChangeTree = function (node) {
            //选中
            if (node.checked) {
                let index = node.id.lastIndexOf("+");
                if (index !== -1) {
                    //逻辑上的parent
                    let parentNode = $scope.noroot.getNode("id", node.id.substr(0, index));
                    if (!parentNode.checked) {
                        $scope.noroot.toggle(parentNode, true);
                    }
                    $scope.onChangeTree(parentNode);
                }
            } else {
                $scope.cancelSelected($scope.noroot.getParent(node), node.id);
            }
        };

        $scope.cancelSelected = function (node, key) {
            if (node) {
                if (angular.isArray(node.children) && node.children.length > 0) {
                    angular.forEach(node.children, function (child) {
                        if (child.checked && -1 !== key.indexOf(":")) {
                            let index = child.id.indexOf(key);
                            if (index === 0) {
                                child.checked = false;
                            }
                        }
                        $scope.cancelSelected(child, key);
                    });
                }
            }
        };


        $scope.create = function () {
            $scope.noroot = {
                onChange: function (node) {
                    $scope.onChangeTree(node);
                }
            };
            $scope.item = {};
            $scope.item.systemType = 'add';
            $scope.formUrl = 'project/html/role/role-add.html' + '?_t=' + Math.random();
            $scope.toggleForm();
            $scope.show = true;
        };

        $scope.moduleIdList = [];

        $scope.edit = function (item) {
            $scope.noroot = {
                onChange: function (node) {
                    $scope.onChangeTree(node);
                }
            };
            $scope.treeData = {};
            $scope.roleDetailLoadingLayer = HttpUtils.get("role/authorizePermission/" + item.id, function (rep) {
                $scope.treeData = rep.data;
                angular.forEach($scope.treeData, function (tree) {
                    $scope.moduleIdList.push(tree.id);
                });
            });
            $scope.item = angular.copy(item);
            $scope.item.systemType = 'edit';
            $scope.formUrl = 'project/html/role/role-edit.html' + '?_t=' + Math.random();
            $scope.toggleForm();
            $scope.show = true;
        };


        $scope.delete = function (role) {
            Notification.confirm(Translator.get("i18n_menu_delete_confirm"), function () {
                $http.get("role/delete/" + role.id).then(function () {
                    Notification.success(Translator.get("i18n_mc_delete_success"));
                    $scope.list();
                }, function (rep) {
                    Notification.danger(rep.message)
                })
            });
        };

        $scope.closeToggleForm = function () {
            $scope.toggleForm();
            $scope.item = {};
        };

        $scope.changeRole = function (roleId) {
            $scope.treeData = {};
            $scope.roleDetailLoadingLayer = HttpUtils.get("role/permissionTree/" + roleId, function (rep) {
                $scope.treeData = rep.data;
            });
        };

        $scope.wizard = {
            // 按顺序显示,id必须唯一并需要与页面中的id一致，select为分步初始化方法，next为下一步方法(最后一步时作为提交方法)
            steps: [
                {
                    id: "1",
                    name: Translator.get("i18n_role_info"),
                    select: function () {

                    },
                    next: function () {
                        if ($scope.item.name == null) {
                            Notification.info(Translator.get("i18n_role_null_msg"));
                            return false;
                        }
                        if ($scope.item.parentId == null) {
                            Notification.info(Translator.get("i18n_role_system_null_msg"));
                            return false;
                        }
                        return true;
                    }
                }, {
                    id: "2",
                    name: Translator.get("i18n_role_select_permission"),
                    select: function () {

                    },
                    next: function () {
                        $scope.getSelected();
                        if ($scope.item.systemType === 'add') {
                            HttpUtils.post("role/add", $scope.item, function () {
                                Notification.success(Translator.get("i18n_mc_create_success"));
                                $scope.closeToggleForm();
                                $scope.show = false;
                                $scope.list();
                                return true;
                            }, function (rep) {
                                Notification.danger(rep.message);
                                return false;
                            });
                        }
                        if ($scope.item.systemType === 'edit') {
                            $scope.item.moduleIdList = $scope.moduleIdList;
                            HttpUtils.post("role/update", $scope.item, function () {
                                Notification.success(Translator.get("i18n_mc_update_success"));
                                $scope.closeToggleForm();
                                $scope.show = false;
                                $scope.list();
                                return true;
                            }, function (response) {
                                Notification.danger(response.message);
                                return false;
                            });
                        }
                    }
                }
            ],
            // 嵌入页面需要指定关闭方法
            close: function () {
                $scope.closeToggleForm();
                $scope.show = false;
            }
        };

        $scope.closeToggleForm = function () {
            $scope.toggleForm();
            $scope.item = {};
        };

        $scope.getSelected = function () {
            let selected = $scope.noroot.getSelected();
            let permissionMap = {};
            angular.forEach(selected, function (module) {
                if ($scope.isSystemRole()) {
                    if (module.type === 'link') {
                        let moduleId = module.id;
                        let permission = [];
                        permission.push(moduleId);
                        permissionMap[moduleId] = permission;
                    }
                } else {
                    let moduleId = module.id;
                    let permission = [];
                    permission.push(moduleId);
                    $scope.getChildrenSelected(module.children, permission);
                    permissionMap[moduleId] = permission;
                }

            });
            $scope.item.permissionMap = permissionMap;
        };

        $scope.isSystemRole = function () {
            return $scope.item.id === 'ADMIN' || $scope.item.id === 'ORGADMIN' || $scope.item.id === 'USER';
        };

        $scope.getChildrenSelected = function (children, permission) {
            if (angular.isArray(children)) {
                angular.forEach(children, function (item) {
                    permission.push(item.id);
                    $scope.getChildrenSelected(item.children, permission);
                });
            }
        };
    }
)
;