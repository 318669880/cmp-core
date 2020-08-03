ProjectApp.controller('OSController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, AuthService, Translator) {

    $scope.list = function () {
        $scope.loadingLayer = HttpUtils.get('dictionary/category/os/list', function (response) {
            $scope.osList = response.data;
            $scope.osTreeNode = [];
            angular.forEach($scope.osList, function (os) {
                if (os.versionList.length > 0) {
                    angular.forEach(os.versionList, function (version, index) {
                        var node = {};
                        node.id = os.id;
                        node.category = os.category;
                        node.dictionaryKey = os.dictionaryKey;
                        node.dictionaryValue = os.dictionaryValue;
                        node.version = version.dictionaryValue;
                        node.versionId = version.id;
                        node.rowSpan = -1;
                        if (index === 0) {
                            node.rowSpan = os.versionList.length;
                        }
                        $scope.osTreeNode.push(node);
                    })
                } else {
                    var node = {};
                    node.id = os.id;
                    node.category = os.category;
                    node.dictionaryKey = os.dictionaryKey;
                    node.dictionaryValue = os.dictionaryValue;
                    node.rowSpan = 1;
                    $scope.osTreeNode.push(node);
                }

            });
        });

    };

    $scope.list();

    $scope.add = function () {
        $scope.item = {};
        $scope.formUrl = 'project/html/os/os-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.edit = function (item) {
        $scope.item = item;
        $scope.formUrl = 'project/html/os/os-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.submitOsCategory = function (item) {
        if (!item.id) {
            HttpUtils.post("dictionary/category/os/add", item, function () {
                Notification.success(Translator.get("i18n_mc_create_success"));
                $scope.toggleForm();
                $scope.list();
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_create_fail") + "," + response.message);
            });
        } else {
            HttpUtils.post("dictionary/category/os/update", item, function () {
                Notification.success(Translator.get("i18n_mc_update_success"));
                $scope.toggleForm();
                $scope.list();
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_update_fail") + "," + response.message);
            });
        }

    };

    $scope.deleteOsCategory = function (id) {
        Notification.confirm(Translator.get("i18n_menu_delete_confirm"), function () {
            HttpUtils.post("dictionary/category/os/delete/" + id, {}, function () {
                Notification.success(Translator.get("i18n_mc_delete_success"));
                $scope.list();
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_delete_fail") + "," + response.message);
            });

        })
    };

    $scope.addVersion = function (node) {
        $scope.versionItem = angular.copy(node);
        $scope.versionItem.version = null;
        $scope.formUrl = 'project/html/os/os-version-add.html' + '?_t=' + Math.random();
        $scope.toggleForm();
    };

    $scope.submitAddVersion = function (node) {
        HttpUtils.post("dictionary/category/os/" + node.id + "/add/" + node.version, {}, function () {
            Notification.success(Translator.get("i18n_mc_add_success"));
            $scope.toggleForm();
            $scope.list();
        }, function (response) {
            Notification.danger(Translator.get("i18n_mc_add_fail") + "," + response.message);
        });
    };

    $scope.submitDeleteVersion = function (id) {
        Notification.confirm(Translator.get("i18n_menu_delete_confirm"), function () {
            HttpUtils.post("dictionary/category/os/version/delete/" + id, {}, function () {
                Notification.success(Translator.get("i18n_mc_delete_success"));
                $scope.list();
            }, function (response) {
                Notification.danger(Translator.get("i18n_mc_delete_fail") + "," + response.message);
            });
        })

    }
});


