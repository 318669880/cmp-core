ProjectApp.controller('UIController', function ($scope, $http, Notification, $timeout, HttpUtils, Translator) {
    $scope.languages = window.parent.languageOptions;


    $scope.isSave = false;

    $scope.clickSave = function () {
        $scope.isSave = !$scope.isSave;
        $scope.params = angular.copy($scope.params2);
    };

    $scope.edit = function () {
        $scope.isSave = !$scope.isSave;
    };

    $scope.uiInfo = function () {
        $scope.ui = {
            support_name: "ui.support.name",
            support_url: "ui.support.url",
            logo: "ui.logo",
            system_name: "ui.system.name",
            theme_accent: "ui.theme.accent",
            theme_primary: "ui.theme.primary",
            favicon: "ui.favicon",
            login_title: "ui.login.title",
            login_img: "ui.login.img",
            title: "ui.title",
            language: "ui.language"
        };

        $scope.loadingLayer = HttpUtils.get('system/parameter/ui/info', function (response) {
            $scope.params = response.data;
            $scope.params2 = angular.copy($scope.params);
        });
    };
    $scope.uiInfo();

    $scope.submit = function (data) {
        let form = new FormData();
        angular.forEach(data, function (param) {
            if ((!angular.isUndefined(param.file)) && param.file !== null) {
                let file = param.file;
                let name = file.name + "," + param.paramKey;
                let newfile = new File([file], name, {type: file.type});
                form.append('file', newfile);
                param.file = null;
            }
        });
        let toJson = angular.toJson(data);
        form.append('parameter', toJson);

        $http({
            method: 'POST',
            url: 'system/parameter/ui/info',
            data: form,
            headers: {'Content-Type': undefined}
        }).then(function (data) {
            Notification.success(Translator.get("i18n_ui_edit_refresh"));
            $scope.uiInfo();
            $scope.clickSave();
            $timeout(function () {
                window.parent.location.reload()
            }, 2000);
        }, function (rep) {
            Notification.danger(rep.data.message)
        });
    };

    $scope.upload = function (file, key) {
        if (file.size > 1024 * 1024) {
            Notification.warn(Translator.get("i18n_ui_upload_file_limit"));
            return;
        }
        let arr = key.split(".");
        let tmp;
        if (arr.length === 3) {
            tmp = arr[1] + "_" + arr[2]
        } else {
            tmp = arr[1];
        }
        angular.forEach($scope.params, function (data) {
            if (data.paramKey === $scope.ui[tmp]) {
                data.fileName = file.name;
                data.file = file;
            }
        })
    };
    $scope.cancelUpload = function (key) {
        let arr = key.split(".");
        let tmp;
        if (arr.length === 3) {
            tmp = arr[1] + "_" + arr[2]
        } else {
            tmp = arr[1];
        }
        angular.forEach($scope.params, function (data) {
            if (data.paramKey === $scope.ui[tmp]) {
                data.fileName = null;
                data.file = null;
            }
        })
    }
});