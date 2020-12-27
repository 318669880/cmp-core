ProjectApp.controller('OrganizationController', function ($scope, HttpUtils, FilterSearch, $http, Notification, operationArr, $state, Translator, UserService) {

    // 定义搜索条件
    $scope.conditions = [
        {key: "name", name: Translator.get("i18n_organization_name"), directive: "filter-contains"}
    ];

    // 用于传入后台的参数
    $scope.filters = [];
    $scope.ids = [];
    // 全选按钮，添加到$scope.columns
    $scope.first = {
        default: true,
        sort: false,
        type: "checkbox",
        checkValue: false,
        change: function (checked) {
            $scope.items.forEach(function (item) {
                if (!item.countWorkspace > 0 && !$scope.hasKid(item)) {
                    item.enable = checked;
                    $scope.singleClick(checked, item, true);
                }
            });
        },
        width: "40px"
    };

    $scope.clickChecked = function (checked, item, isSelectAll) {
        let nodes = [];
        $scope.getChildNodes([item], nodes);
        let opItems = $scope.items.filter(cItem => (!cItem.countWorkspace>0) && !$scope.hasKid(item) && nodes.some(tNode => tNode.id == cItem.id));
        opItems.forEach(opItem => {
            if (item.id != opItem.id ){
                opItem.enable = checked;
            }
            $scope.singleClick(checked, opItem, isSelectAll);
        });
        //$scope.singleClick(checked, item, isSelectAll);
        // $scope.selectAll();
    };

    $scope.singleClick = function (checked, item, isSelectAll) {
        if (checked === true) {
            $scope.ids.push(item.id);
        } else {
            if (isSelectAll) {
                $scope.ids = [];
            } else {
                operationArr.removeByValue($scope.ids, item.id);
                if ($scope.ids.length === 0) {
                    $scope.first.checkValue = false;
                }
            }
        }
    };

    // $scope.selectAll = function () {
    //     var count = 0;
    //     angular.forEach($scope.items, function (item) {
    //         if (!item.countWorkspace > 0) {
    //             count++;
    //             if (count === $scope.ids.length) {
    //                 $scope.first.checkValue = true;
    //             }
    //         }
    //     });
    // };


    $scope.columns = [
        $scope.first,
        {value: Translator.get("i18n_organization_name"), key: "name", sort: false},
        {value: Translator.get("i18n_workspace_list"), key: "countWorkspace"},// 不想排序的列，用sort: false
        {value: Translator.get("组织管理员"), key: "countOrgAdmin"},// 不想排序的列，用sort: false
        {value: Translator.get("i18n_organization_desc"), key: "description"},
        {value: Translator.get("i18n_create_time"), key: "create_time"},
    ];

    $scope.highLight = function(item){
        return $scope.filters && $scope.filters.some(filter => filter.key == "name" && item.name.indexOf(filter.label) != -1) ;
    };

    $scope.list = function (sortObj) {
        var condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "organization", condition, function () {
            angular.forEach($scope.items, function (item) {
                item.enable = false;
            });
            $scope.sourceItems = angular.copy($scope.items);
            $scope.enableIds = $scope.sourceItems.map(item => item.id)
            $scope.items = $scope.formatTree();

        });
    };
    $scope.hasKid = function(node){
        return node.childNodes && node.childNodes.length > 0;
    };

    $scope.getChildNodes = function(nodes, results){
        if (!results) results = [];
        nodes.forEach(node => {
            results.push(node);
            if (node.childNodes && node.childNodes.length > 0){
                $scope.getChildNodes(node.childNodes, results);
            }
        })
    }

    $scope.userInfo = UserService.getUserInfo();
    $scope.filterSelf = function (node) {
        return $scope.item.id != node.id && $scope.filterPermission(node);
    }

    $scope.filterPermission = function(node){
        if ($scope.userInfo.roleIdList.indexOf("ORGADMIN") == -1) return true;
        return $scope.enableIds.some(item => item == node.id);
        return true;
    }


    $scope.toggleNode = function(node, status){
        if (node.status == 'close'){
            node.status = 'open';
        }else{
            node.status = 'close';
        }
        if (node.childNodes && node.childNodes.length > 0){
            $scope.toggleChilds(node.childNodes, node.status);
        }
    }

    $scope.toggleChilds = function(nodes, parent_status){
        nodes.forEach(node => {
            if (parent_status == 'close'){
                node.show = false;
            }
            if (parent_status == 'open'){
                node.show = true;
            }
            if (node.childNodes && node.childNodes.length > 0){
                $scope.toggleChilds(node.childNodes, node.show ? node.status : "close");
            }
        })
    }


    $scope.formatWithPermission = function(){
        if ($scope.userInfo.roleIdList.indexOf("ORGADMIN") == -1) return;
        $scope.items.forEach(item => !$scope.items.some(cItem => item.pid == cItem.id) && (item.pid = null));
    }

    $scope.formatTree = function(){
        $scope.formatWithPermission();
        let formatItems = [];
        $scope.items.forEach(item => {
            item.show = true;
            if (!item.pid) {
                formatItems.push(item);
                item.is_root = true;
            }
            $scope.items.forEach(tempItem => {
                if (tempItem.pid == item.id){
                    if (item.childNodes == null){
                        item.childNodes = [];
                    }
                    item.childNodes.push(tempItem);
                    tempItem.is_child = true;
                }
            })
        });
        /**
         * 解决分页第二页因为没有根节点无法展示的bug
         */
        let missionItems = $scope.items.filter(item => !item.is_root && !item.is_child);
        //formatItems = formatItems.concat(missionItems);
        formatItems = missionItems.concat(formatItems);
        let results = [];
        formatItems && formatItems.length > 0 && $scope.expandTree(formatItems, results);
        return results;
    };

    $scope.expandTree = function(treeDatas, results){
        if (!results) results = [];
        treeDatas.forEach(node => {
            node._prefix = $scope.treeNodePrefix(node.level);
            node.name = node.name;
            results.push(node);
            if (node.childNodes && node.childNodes.length > 0){
                node.status = 'open';
                $scope.expandTree(node.childNodes, results);
            }
        });
    }

    $scope.treeNodePrefix = function (sum){
        let result = [];
        for (let i = 0; i< sum ;i++){
            result.push(i);
        }
        return result;
    }

    $scope.list();

    $scope.create = function () {
        $scope.pid = null;
        $scope.enableIds = $scope.sourceItems.map(item => item.id);
        $scope.selectedOrgIds = [];
        $scope.formUrl = 'project/html/organization/organization-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.edit = function (data) {
        $scope.pid = null;
        if ($scope.userInfo.roleIdList.indexOf("ORGADMIN") != -1){
            let cData = null;
            $scope.sourceItems.some(sItem => { cData = sItem;return sItem.id == data.id}) && (data = cData)
        }
        $scope.item = angular.copy(data);
        $scope.selectedOrgIds = [data.pid];
        if ($scope.userInfo.roleIdList.indexOf("ORGADMIN") != -1){
            $scope.enableIds = $scope.sourceItems.map(item => item.id)
            $scope.enableIds.push(data.pid);
        }
        $scope.formUrl = 'project/html/organization/organization-edit.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };


    $scope.submit = function (type, data) {
        data.pid = $scope.pid;
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
        if ($scope.ids.length === 0) {
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
                value: [item.id]
            }
        ));
        $state.go("workspace")
    };

    $scope.linkOrgAdmin = function (item) {
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.orgId = item.id;
        $scope.infoUrl = 'project/html/organization/organization-link-orgAdmin.html' + '?_t=' + Math.random();
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.item = {};
        $scope.selected = "";
        $scope.toggleInfoForm(false);
    };


    $scope.builder =  {
        id: "nodeId",
        name: "nodeName",
        children: "childNodes"
    }
    $scope.selectedOrgIds = [];
    $scope.ts_param = {excludeWs: true};
    $scope.pid = null;
    $scope.ts_changed = (values) => {
        $scope.pid = (!!values && values.length > 0) ? values[0] : null;
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