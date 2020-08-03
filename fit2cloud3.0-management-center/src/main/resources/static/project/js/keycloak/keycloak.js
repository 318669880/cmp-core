ProjectApp.controller('KeycloakController', function ($scope, Notification, HttpUtils,Translator) {
    $scope.tagStatus = 0;//0 参数设置；1 同步用户

    $scope.changeStatus = function (status) {
        $scope.tagStatus = status;
    };

    $scope.syncExtraUser = function () {
        $scope.loadingLayer = HttpUtils.get("extra/user/sync", function () {
            Notification.success(Translator.get("i18n_keycloak_sync_task"));
        });
    };

    $scope.addressInit = function (value) {

        let reg = RegExp(/http/);
        let isCon = reg.test(value);
        if (isCon) {
            $scope.addressURL = value;
        } else {
            $scope.addressURL = window.location.origin + value;
        }
    };

    // $scope.reloadSyncExtraUser = function () {
    //     $scope.loadingLayer = HttpUtils.get("extra/user/reload/sync", function () {
    //         Notification.success("重载同步任务已提交，请稍后刷新查看");
    //     });
    // };
});


ProjectApp.controller('KeycloakParamController', function ($scope, Notification, eyeService, HttpUtils, Translator) {
    $scope.isSave = false;

    $scope.clickSave = function () {
        $scope.isSave = !$scope.isSave;
        let passwordElement = angular.element("#password");
        let eyeElement = angular.element("#eye");
        passwordElement[0].type = 'password';
        eyeElement.addClass("fa fa-eye");
        $scope.params = angular.copy($scope.params2)
    };

    $scope.keyCloakInfo = function () {
        $scope.username = "keycloak.username";
        $scope.password = "keycloak.password";
        $scope.address = "keycloak-server-address";

        $scope.loadingLayer = HttpUtils.get("system/parameter/keyCloak/info", function (response) {
            $scope.params = response.data;
            $scope.params2 = angular.copy(response.data);
        });
    };
    $scope.keyCloakInfo();

    $scope.view = function () {
        eyeService.view("#password", "#eye");
    };

    $scope.passwordInputLength = function () {
        let length = "100%";
        if ($scope.isSave) {
            length = "98%";
        }
        return {"width": length}
    };

    $scope.addressInputMargin = function () {
        let length = "0px";
        if ($scope.isSave) {
            length = "42px";
        }
        return {"margin-top": length}
    };

    $scope.jump2KeyCloak = function (url) {
        window.open(url);
    };

    $scope.submit = function (data) {
        $scope.loadingLayer = HttpUtils.post("system/parameter/keyCloak/info", data, function () {
            Notification.success(Translator.get("i18n_mc_update_success"));
            $scope.keyCloakInfo();
            $scope.clickSave()
        });
    };
});

ProjectApp.controller('ExtraUserController', function ($scope, HttpUtils, FilterSearch,Translator) {

    $scope.conditions = [
        {key: "name", name: "ID", directive: "filter-contains"},
        {key: "displayName", name: Translator.get("i18n_user_name_"), directive: "filter-contains"},
        {key: "email", name: Translator.get("i18n_email"), directive: "filter-contains"}
    ];
    $scope.filters = [];
    $scope.columns = [
        {value: "ID", key: "name", sort: false},
        {value: Translator.get("i18n_user_name_"), key: "display_name", sort: false},
        {value: Translator.get("i18n_email"), key: "email", sort: false},// 不想排序的列，用sort: false
        {value: Translator.get("i18n_phone"), key: "phone", sort: false},
        {value: Translator.get("i18n_user_sync_time"), key: "sync_time"}
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
        HttpUtils.paging($scope, "extra/user", condition);
    };
    $scope.list();
});