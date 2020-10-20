ProjectApp.controller('WorkspaceController', function ($scope, HttpUtils, FilterSearch, $http, Notification, $state, Translator) {
    $scope.orgParam = angular.fromJson(sessionStorage.getItem("orgParam"));
    sessionStorage.removeItem("orgParam");
    // 定义搜索条件
    $scope.conditions = [
        {key: "name", name: Translator.get("i18n_workspace_name"), directive: "filter-contains"},
    ];

    if ($scope.currentRole === $scope.roleConst.admin) {
        /*$scope.conditions.push({
            key: "organizationId",
            name: Translator.get("i18n_organization"),
            directive: "filter-select-virtual",
            url: "organization",
            search: true,
            convert: {value: "id", label: "name"}
        });*/
        $scope.conditions.push({
            key: "organizationIds",
            name: Translator.get("i18n_organization"),
            directive: "filter-multistage-tree",
            url: "user/orgtreeselect",
            param: {excludeWs: true},
            multiple: true,
            convert: {value: "nodeId", label: "nodeName"},
            build: {
                id: "nodeId",
                name: "nodeName",
                children: "childNodes"
            }

        })
    }
    $scope.filters = [];
    if ($scope.orgParam && $scope.currentRole === $scope.roleConst.admin) {
        $scope.filters = [{
            key: "organizationIds",
            name: Translator.get("i18n_organization"),
            label: $scope.orgParam.label,
            value: $scope.orgParam.value
        }];
    }

    $scope.columns = [
        {value: Translator.get("i18n_workspace_name"), key: "name", sort: false},
        {value: Translator.get("i18n_organization"), key: "organizationName", sort: false},// 不想排序的列，用sort: false
        {value: Translator.get("i18n_workspace_authorized_user"), key: "countAuthorizedUser"},// 不想排序的列，用sort: false
        {value: Translator.get("i18n_workspace_desc"), key: "description"}
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
        HttpUtils.paging($scope, "workspace", condition);
    };
    $scope.list();

    $scope.create = function () {
        //$scope.acquisitionConditions();
        $scope.formUrl = 'project/html/workspace/workspace-tree-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.edit = function (data) {
        $scope.item = angular.copy(data);
        //$scope.acquisitionConditions();
        $scope.sourceDatas = [data.organizationId];
        $scope.organizationId = data.organizationId;
        $scope.formUrl = 'project/html/workspace/workspace-tree-edit.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.acquisitionConditions = function () {
        HttpUtils.get("organization", function (rep) {
            $scope.orgs = rep.data;
        });
    };


    $scope.submit = function (type, data) {
        data.organizationId = $scope.organizationId;
        if(!data.name){
            Notification.danger(Translator.get("i18n_ex_workspace_name_no_empty"));
            return;
        }
        if (type === 'add') {
            HttpUtils.post("workspace/add", data, function () {
                $scope.list();
                Notification.success(Translator.get("i18n_mc_create_success"));
                $scope.closeToggleForm();
            }, function (rep) {
                Notification.danger(rep.message);
            })
        }
        if (type === 'edit') {
            $http.post('workspace/update', data).then(function () {
                $scope.list();
                Notification.success(Translator.get("i18n_mc_update_success"));
                $scope.closeToggleForm();
            }, function (rep) {
                Notification.danger(rep.data.message);
            });
        }
    };

    $scope.delete = function (workspace) {
        Notification.confirm(Translator.get("i18n_workspace_delete_pre") + workspace.name + Translator.get("i18n_workspace_delete_suffix"), function () {

            // HttpUtils.post("workspace/delete?workspaceId="+workspace.id , null ,res => {
            HttpUtils.post("workspace/delete/"+workspace.id , null ,res => {
                Notification.success(Translator.get("i18n_mc_delete_success"));
                $scope.list();
            }, err => {
                debugger;
                Notification.danger(err.message);
            });
        })
    };

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.item = {};
    };

    $scope.workspaceAuthorize = function (item) {
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.selectWorkspaceId = item.id;
        $scope.infoUrl = 'project/html/workspace/workspace-authorize.html' + '?_t=' + Math.random();
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
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
ProjectApp.controller('WorkspaceAuthorizeController', function ($scope, HttpUtils, Translator) {

    $scope.columns = [
        {value: Translator.get("i18n_username"), key: "name", sort: false},
        {value: Translator.get("i18n_email"), key: "name", sort: false},
        {value: Translator.get("i18n_phone"), key: "phone", sort: false},
    ];

    $scope.list = function () {
        HttpUtils.paging($scope, "workspace/user/" + $scope.selectWorkspaceId, {})
    };
    $scope.list();
});