ProjectApp.controller('OrganizationTreeController', function ($scope, $filter, HttpUtils, FilterSearch, $http, Notification, operationArr, $state, Translator) {
    $scope.cols = [
        [
            {type: 'numbers'},
            {type: 'checkbox'},
            {field: 'nodeName', title: '名称', minWidth: 165},
            {
                title: '菜单图标', align: 'center', hide: true,
                templet: '<p><i class="layui-icon {{d.menuIcon}}"></i></p>'
            },
            {field: 'relativeNum', title: '相关人员', width: 50,
                templet: d => {
                    let relativeNum = d.relativeNum;
                    window.aClick = node => {
                        if (node.relativeNum == 0)return;
                        let item = {
                            id: node.nodeId,
                            pid: node.parentId,
                            name: node.nodeName,
                            description: node.description
                        }
                        if (node.nodeType=="org"){
                            $scope.linkOrgAdmin(item);
                            return;
                        }
                        $scope.workspaceAuthorize(item);
                    };
                    let temp = "<a style='cursor:pointer' class='md-primary' href = '' onClick='aClick("+JSON.stringify(d)+")' >"+relativeNum+"</a>";
                    return temp;
                }
            },
            {title: '类型', templet: '<p>{{d.nodeType=="org" ? "组织机构" : "工作空间"}}</p>', align: 'center', width: 60},
            {
                title: '创建时间', templet: function (d) {
                    return $filter('date')(d.createTime,'yyyy-MM-dd');
                }
            },
            {field: 'description', title: '描述', minWidth: 105},
            {
                align: 'center',
                title: '操作',
                width: 150,
                templet: (d) => {
                    window.treeEdit = (node) => {
                        let item = {
                            id: node.nodeId,
                            pid: node.parentId,
                            name: node.nodeName,
                            description: node.description
                        }
                        $scope.sourceDatas = [node.parentId];
                        $scope.organizationId = node.parentId;
                        $scope.edit(item);
                    }
                    window.treeDelete = (node) => {
                        $scope.targetTree.setChecked([node.nodeId]);

                        $scope.delete();
                    }
                    return "<a class=\"layui-btn layui-btn-primary layui-btn-xs\" onClick = 'treeEdit("+JSON.stringify(d)+")'>编辑</a>\n" +
                           "<a class=\"layui-btn layui-btn-danger layui-btn-xs\" onClick = 'treeDelete("+JSON.stringify(d)+")'>删除</a>"
                }
            }
        ]
    ];
    $scope.targetTree = null;
    $scope.tree_url = "user/orgtree";
    $scope.id_name = "nodeId";
    $scope.pid_name = "parentId";
    $scope.treeTableParam = {excludeWs: false};
    $scope.treeSucess = (targetTree) => {
        $scope.targetTree = targetTree;
        //$scope.targetTree.expandAll();
    }

    // 定义搜索条件
    $scope.conditions = [
        {key: "name", name: Translator.get("i18n_organization_name"), directive: "filter-contains"}
    ];

    // 用于传入后台的参数
    $scope.filters = [];
    $scope.ids = [];
    // 全选按钮，添加到$scope.columns


    $scope.list = () => {
        let condition = FilterSearch.convert($scope.filters);
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        $scope.targetTree && $scope.targetTree.refresh && $scope.targetTree.refresh();
    }



    $scope.columns = [
        {value: Translator.get("i18n_organization_name"), key: "name", sort: false},
        {value: Translator.get("i18n_workspace_list"), key: "countWorkspace"},// 不想排序的列，用sort: false
        {value: Translator.get("组织管理员"), key: "countOrgAdmin"},// 不想排序的列，用sort: false
        {value: Translator.get("i18n_organization_desc"), key: "description"},
        {value: Translator.get("i18n_create_time"), key: "create_time"},
    ];



    $scope.create = function () {
        $scope.formUrl = 'project/html/organization/organization-tree-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.edit = function (data) {
        $scope.item = angular.copy(data);
        $scope.formUrl = 'project/html/organization/organization-tree-edit.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };




    $scope.submit = function (type, data) {
        data.pid = $scope.organizationId;
        if (type === 'add') {
            HttpUtils.post("organization/add", data, function () {
                $scope.list();
                Notification.success(Translator.get("i18n_mc_create_success"));
                $scope.closeToggleForm();
            }, function (rep) {
                Notification.danger(rep.message);
            })
        }
        if (type === 'edit') {
            $http.post('organization/update', data).then(function () {
                $scope.list();
                Notification.success(Translator.get("i18n_mc_update_success"));
                $scope.closeToggleForm();
            }, function (rep) {
                Notification.danger(rep.data.message);
            });
        }
    };
    $scope.delete = function () {
        let checkdNodes = $scope.targetTree.checkStatus();
        $scope.ids = checkdNodes && checkdNodes.length > 0 && checkdNodes.filter(node => !node.LAY_INDETERMINATE).map(node => node.nodeId);
        if (!$scope.ids  || $scope.ids.length === 0) {
            Notification.info(Translator.get("i18n_no_selected_item"))
        } else {
            Notification.confirm(Translator.get("i18n_menu_delete_confirm"), function () {
                $http.post("organization/delete", $scope.ids).then(function () {
                    Notification.success(Translator.get("i18n_mc_delete_success"));
                    $scope.list();
                }, function (rep) {
                    Notification.danger(rep.data.message)
                })
            })
        }
    };

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.item = {};
    };

    $scope.linkWorkspace = function (item) {
        sessionStorage.setItem("orgParam", angular.toJson({
                label: item.name,
                value: item.id
            }
        ));
        $state.go("workspace")
    };
    $scope.linkOrgAdmin = function (item) {
        if ($scope.selected === item.id) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.id;
        $scope.orgId = item.id;
        $scope.infoUrl = 'project/html/organization/organization-link-orgAdmin.html' + '?_t=' + Math.random();
        $scope.toggleInfoForm(true);
    };
    $scope.workspaceAuthorize = function (item) {
        if ($scope.selected === item.id) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.id;
        $scope.selectWorkspaceId = item.id;
        $scope.infoUrl = 'project/html/workspace/workspace-authorize.html' + '?_t=' + Math.random();
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.item = {};
        $scope.selected = "";
        $scope.toggleInfoForm(false);
    };
    $scope.ts_url = "user/orgtreeselect";
    $scope.ts_param = {excludeWs: true};
    $scope.method = "post";
    $scope.organizationId = null;
    $scope.ts_changed = (values) => {
        $scope.organizationId = (!!values && values.length > 0) ? values[0] : null;
    };

    $scope.start = (e) => {
        angular.element('#_treeSelectTs_').parent().addClass("md-input-focused");
    };
    $scope.end = (e) => {
        angular.element('#_treeSelectTs_').parent().removeClass("md-input-focused");
        angular.element('#_treeSelectTs_').parent().addClass(!!$scope.organizationId ? "md-input-has-value" : "md-input-invalid");
    };

});

ProjectApp.controller('OrganizationLinkWorkspaceController', function ($scope, HttpUtils, Translator) {

    $scope.columns = [
        {value: Translator.get("i18n_workspace_name"), key: "name", sort: false},
        {value: Translator.get("i18n_create_time"), key: "name", sort: false},
    ];

    $scope.list = function () {
        HttpUtils.paging($scope, "organization/link/workspace/" + $scope.orgId, {})
    };
    $scope.list();
});

ProjectApp.controller('OrganizationLinkOrgAdminController', function ($scope, HttpUtils, Translator) {

    $scope.columns = [
        {value: Translator.get("i18n_user_name_"), key: "name", sort: false},
        {value: Translator.get("i18n_email"), key: "name", sort: false},
        {value: Translator.get("i18n_phone"), key: "phone", sort: false},
    ];

    $scope.list = function () {
        HttpUtils.paging($scope, "organization/user/" + $scope.orgId, {})
    };
    $scope.list();
});