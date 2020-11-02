ProjectApp.controller('MessageController', function ($scope, $http, Notification, eyeService, HttpUtils, Translator) {
    $scope.isSave = false;
    $scope.tab = 'mail';

    $scope.clickTab = function (tab) {
        $scope.tab = tab;
        $scope.isSave = false;
        $scope.uiInfo();
    };

    $scope.clickSave = function () {
        $scope.isSave = !$scope.isSave;
        $scope.params = angular.copy($scope.params2);
    };

    $scope.edit = function () {
        $scope.isSave = !$scope.isSave;
    };

    $scope.uiInfo = function () {
        $scope.mail = {
            port: "smtp.port",
            account: "smtp.account",
            password: "smtp.password",
            server: "smtp.server",
            ssl: "smtp.ssl",
            tls: "smtp.tls",
            anon: "smtp.anon",
        };
        $scope.wechat = {
            cropId: "wechat.cropId",
            agentId: "wechat.agentId",
            secret: "wechat.secret",
            testUser: "wechat.testUser",
        };
        $scope.dingtalk = {
            appKey: "dingtalk.appKey",
            agentId: "dingtalk.agentId",
            appSecret: "dingtalk.appSecret",
            testUser: "dingtalk.testUser",
        };

        $scope.loadingLayer = HttpUtils.get("system/parameter/message/info/" + $scope.tab, function (rep) {
            $scope.params = rep.data;
            $scope.params2 = angular.copy(rep.data);
        });
    };
    $scope.uiInfo();


    $scope.submit = function (data) {
        $scope.loadingLayer = HttpUtils.post("system/parameter/message/info/" + $scope.tab, data, function () {
            Notification.success(Translator.get("i18n_mc_update_success"));
            $scope.uiInfo();
            $scope.clickSave();
        });
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
            length = "25px";
        }
        return {"margin-top": length}
    };

    $scope.view = function () {
        eyeService.view("#password", "#eye");
    };

    $scope.transform = function (value) {
        return value === 'true';
    };

    $scope.connectionEnable = true;
    $scope.testConnection = function () {
        $scope.connectionEnable = false;
        let data = {};
        angular.forEach($scope.params, function (param) {
            data[param.paramKey] = param.paramValue;
        });

        if ($scope.tab === 'wechat'){
            if (!data['wechat.testUser']){
                Notification.danger(Translator.get("i18n_pls_input_user"));
                $scope.connectionEnable = true;
                return;
            }
        }
        if ($scope.tab === 'dingtalk'){
            if (!data['dingtalk.testUser']){
                Notification.danger(Translator.get("i18n_pls_input_user"));
                $scope.connectionEnable = true;
                return;
            }
        }

        $scope.loadingLayer = HttpUtils.post("system/parameter/message/testConnection/" + $scope.tab, data, function () {
            Notification.success(Translator.get("i18n_test_success"));
            $scope.connectionEnable = true;
        }, function (rep) {
            Notification.danger(Translator.get("i18n_test_fail") + ", " + rep.message);
            $scope.connectionEnable = true;
        })
    };

    $scope.view = function (password, eye) {
        eyeService.view("#" + password, "#" + eye);
    };
});