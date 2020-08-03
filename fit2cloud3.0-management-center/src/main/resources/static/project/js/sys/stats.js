ProjectApp.controller('SysStatsController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Notification, $interval, AuthService, $state) {
    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t" + window.appversion;

    $scope.list = function () {
        $scope.loadingLayer = HttpUtils.get('sys/stats', function (response) {
            $scope.items = response.data;
            angular.forEach($scope.items, function (item) {
                item.content = angular.fromJson(item.stats)
                item.setting = {};
                item.setting.count = item.content.modules.length;
                item.setting.rmoduleList = [];
                item.setting.smoduleList = [];
                angular.forEach(item.content.modules, function (module) {
                    if (module.status === "running") {
                        item.setting.rmoduleList.push(module);
                    } else {
                        item.setting.smoduleList.push(module);
                    }
                });

                item.metricSetting = {
                    resourceId: item.statKey
                };

                item.metricSetting.click = function (resourceId) {
                    $scope.sysMetric(resourceId);
                };
            })
        })
    };

    $scope.list();

    $scope.sysMetric = function (resourceId) {
        $scope.detail = {};
        $scope.detail.id = resourceId;
        $scope.showInformation();

    };

    $scope.metrics = [
        "SYS_CPU_USAGE",
        "SYS_MEMORY_USAGE",
        "SYS_DISK_USAGE"
    ];


    $scope.closeInformation = function () {
        delete $scope.selected;
        $scope.toggleInfoForm(false);
    };

    $scope.showInformation = function () {
        $scope.infoUrl = 'project/html/sys/metric.html' + '?_t=' + window.appversion;
        $scope.toggleInfoForm(true);
        $scope.$broadcast('showDetail');
    };

    //绑定click事件
    $document.on("click", function (event) {
            var inTable = false, inSidenav = false, isDialog = false, isLine = false;
            $.each($(event.target).parents(), function (index, item) {
                if (item.nodeName.toUpperCase() === 'TABLE') {
                    inTable = true;
                }
                if (item.nodeName.toUpperCase() === 'MD-SIDENAV') {
                    inSidenav = true;
                }
                if (item.nodeName.toUpperCase() === 'MD-DIALOG') {
                    isDialog = true;
                }

                if (item.nodeName.toUpperCase() === 'POD-LINE-CHART') {
                    isLine = true;
                }
            });
            $.each($(event.target), function (index, item) {
                if (item.nodeName.toUpperCase() === 'TABLE') {
                    inTable = true;
                }
                if (item.id.toUpperCase() === 'INFO_FORM') {
                    inSidenav = true;
                }
                if (item.nodeName.toUpperCase() === 'MD-DIALOG') {
                    isDialog = true;
                }
                if ($(item).attr('name') === 'deleteTagNode') {
                    isDialog = true;
                }
            });
            if (!inTable && !inSidenav && !isDialog && !isLine) {
                $scope.closeInformation();
            }
        }
    );

});


