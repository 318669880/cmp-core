ProjectApp.controller('AboutController', function ($scope, HttpUtils, Notification, Translator) {
    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t" + window.appversion;

    $scope.version = $('#_version_').text();
    $scope.loadingLayer = HttpUtils.get("about/build/version", function (response) {
        $scope.build = response.data;
    });
    $scope.copy = $('#_copy_').text();
    $scope.licenseKey = "";

    $scope.import = function () {
        $scope.formUrl = 'project/html/about/license-import.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.support = function () {
        var url = "https://support.fit2cloud.com/";
        window.open(url, '_blank');
    };

    $scope.uploadFile = function (file) {
        if (!file || !file[0]) {
            return;
        }
        $scope.updateLicense = {};
        var reader = new FileReader();
        reader.onload = function (e) {
            $scope.licenseKey = e.target.result;
            $scope.validate({license: $scope.licenseKey}, function (response) {
                $scope.updateLicense = $scope.getLicense(response.data);
                $scope.update();
            });
            $scope.$apply();
        };
        reader.readAsText(file[0]);
    };

    $scope.update = function () {
        $scope.loadingLayer = HttpUtils.post("about/license/update", {license: $scope.licenseKey}, function (response) {
            if (response.data.status === 'Success') {
                Notification.info(Translator.get("i18n_mc_update_success"));
                $scope.license = $scope.getLicense(response.data);
                $scope.toggleForm();
            } else {
                Notification.danger(response.data.message);
            }
        });
    };

    $scope.validate = function (param, success) {
        $scope.loadingLayer = HttpUtils.post("about/license/validate", param, success);
    };

    $scope.getLicenseInfo = function () {
        $scope.validate({}, function (response) {
            $scope.license = $scope.getLicense(response.data);
        });
    };

    $scope.getLicense = function (result) {
        return {
            status: result.status,
            corporation: result.license ? result.license.corporation : "",
            expired: result.license ? result.license.expired : "",
            count: result.license ? result.license.count : "",
            version: result.license ? result.license.version : "",
            edition: result.license ? result.license.edition : ""
        };
    };

    $scope.getLicenseInfo();
});

