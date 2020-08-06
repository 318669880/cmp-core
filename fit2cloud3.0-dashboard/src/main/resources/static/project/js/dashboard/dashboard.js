ProjectApp.controller('DashboardController', function ($rootScope, $scope, HttpUtils, Notification, operationArr) {
    $scope.background = "/web-public/fit2cloud/html/background/background.svg";
    $scope.itemStatus = {};
    $scope.itemStatus.isHidden = false;
    $scope.itemStatus.hiddenCustomize = true;
    $scope.itemStatus.isCustomize = false;

    $scope.customize = function () {
        $scope.itemStatus.isCustomize = !$scope.itemStatus.isCustomize;
        $scope.itemStatus.isHidden = !$scope.itemStatus.isHidden;
        if (!$scope.itemStatus.isCustomize) {
            angular.forEach($scope.lItems, function (item, index) {
                item.position = 'left';
                item.order = index;
            });
            angular.forEach($scope.cItems, function (item, index) {
                item.position = 'center';
                item.order = index;
            });
            angular.forEach($scope.rItems, function (item, index) {
                item.position = 'right';
                item.order = index;
            });
            HttpUtils.post("dashboard/preference", $scope.lItems.concat($scope.cItems, $scope.rItems), function () {
            });
        }
    };
    $scope.clickTag = function (flag) {
        $scope.itemStatus.hiddenCustomize = flag;
    };

    $scope.list = function () {
        HttpUtils.get("dashboard/card/list", function (response) {
            $rootScope.cardList = response.data;
            $scope.lItems = [];
            $scope.cItems = [];
            $scope.rItems = [];

            angular.forEach($scope.cardList, function (card) {
                if (card.position === 'left') {
                    $scope.lItems.push(card);
                }
                if (card.position === 'center') {
                    $scope.cItems.push(card);
                }
                if (card.position === 'right') {
                    $scope.rItems.push(card);
                }
            });
        });
    };

    $scope.changeHide = function () {
        angular.forEach($scope.lItems, function (item, index) {
            item.position = 'left';
            item.order = index;
        });
        angular.forEach($scope.cItems, function (item, index) {
            item.position = 'center';
            item.order = index;
        });
        angular.forEach($scope.rItems, function (item, index) {
            item.position = 'right';
            item.order = index;
        });


        HttpUtils.post("dashboard/preference", $scope.lItems.concat($scope.cItems, $scope.rItems), function () {

        });
    };

    $scope.list();

    $scope.onDragStart = function (event) {
        let element = event.element;
        if (!angular.isUndefined(element)) {
        }
    };
    $scope.onDragStop = function (event) {
        let element = event.element;
        if (!angular.isUndefined(element)) {
            element.attr('ng-drop', 'true');
        }
    };

    $scope.onDragComplete = function (data, event, arr) {
        operationArr.removeByValue(arr, data);

    };
    $scope.onDropComplete = function (index, data, event, arr, type) {
        if (type === 'drop') {
            let hasExist = false;
            for (let i = 0; i < arr.length; i++) {
                if (arr[i].id === data.id) {
                    hasExist = true;
                }
            }
            if (!hasExist) {
                if (arr.length > 0) {
                    arr.splice(index, 0, data);
                } else {
                    arr.push(data);
                }
                let element = event.element;
                element.attr('ng-drop', 'true');
            }
        }
    };

    $scope.onDropPush = function (index, data, event, arr, type) {
        for (let i = 0; i < arr.length; i++) {
            if (arr[i] === data) {
                return
            }
        }
        if (type === 'last' || type === 'first') {
            arr.push(data);
            let element = event.element;
            element.attr('ng-drop', 'true');
        }
    }
});

ProjectApp.controller('OperateLogController', function ($scope, HttpUtils, FilterSearch, Translator) {

    Translator.wait(function () {
        $scope.columns = [
            {value: Translator.get("i18n_dashboard_owner"), key: "workspaceName", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_dashboard_operator"), key: "resourceUserName", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_operation"), key: "operation", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_dashboard_resource_type"), key: "resourceType", sort: false, checked: false},
            {value: Translator.get("i18n_dashboard_resource_name"), key: "resourceName", sort: false},
            {value: Translator.get("i18n_dashboard_module"), key: "module", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_dashboard_ip"), key: "sourceIp", sort: false},// 不想排序的列，用sort: false
            {value: Translator.get("i18n_dashboard_time"), key: "time", sort: false}
        ];

        // 定义搜索条件
        $scope.conditions = [
            {key: "username", name: Translator.get("i18n_dashboard_operator"), directive: "filter-contains"},
            {
                key: "module",
                name: Translator.get("i18n_dashboard_module"),
                directive: "filter-select-virtual",
                url: "module/system/list",
                convert: {value: "id", label: "name"}
            },
            {
                key: "resourceName",
                name: Translator.get("i18n_dashboard_resource_name"),
                directive: "filter-select-virtual",
                url: "dashboard/activity/resource_name",
                search: true,
                convert: {value: "id", label: "name"}
            },
            {
                key: "operation",
                name: Translator.get("i18n_operation"),
                directive: "filter-select-virtual",
                url: "dashboard/activity/operation",
                search: true,
                convert: {value: "id", label: "name"}
            }
        ];

    });

    HttpUtils.get('module/system/list', function (response) {
        $scope.moduleList = response.data;
        $scope.logList();
    });

    $scope.getModule = function (moduleId) {
        for (let index = 0; index < $scope.moduleList.length; index++) {
            if ($scope.moduleList[index].id === moduleId) {
                return $scope.moduleList[index].name;
            }
        }
    };

    $scope.filters = [];
    $scope.logList = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "dashboard/activity", condition);
    };
});

