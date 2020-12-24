ProjectApp.directive("f2cCard", function ($window) {
    return {
        replace: true,
        templateUrl: "project/html/dashboard/card.html" + '?_t=' + window.appversion,
        scope: {
            data: "=",
            itemStatus: "=",
            changeHide: "&"
        },
        link: function (scope) {
            scope.mouseover = function (id) {
                document.getElementById(id).setAttribute("class", "md-whiteframe-4dp");
            };

            scope.mouseleave = function (id) {
                document.getElementById(id).setAttribute("class", "md-whiteframe-2dp");
            };

            scope.footMouseover = function (id) {
                document.getElementById(id + 'footer').setAttribute("class", "footer-border footer-hover");
            };

            scope.footMouseleave = function (id) {
                document.getElementById(id + 'footer').setAttribute("class", "footer-border");
            };

            scope.turnOn = function (url) {
                $window.parent.api.open(url);
            };

            scope.hideCard = function (data) {
                data.display = false;
                scope.changeHide();
            };
        }
    };
});


ProjectApp.directive("cardType", function ($compile, DateSelectService) {
    return {
        replace: true,
        scope: {
            data: "="
        },
        link: function ($scope, element) {

            $scope.setting = angular.fromJson($scope.data.setting);

            if (!$scope.setting) {
                $scope.setting = {};
            }
            $scope.setting.dashboard = true;

            if ($scope.setting.param && $scope.setting.param.period) {
                let date = DateSelectService.calculateDate($scope.setting.param.period);
                $scope.setting.param.start = date.start;
                $scope.setting.param.end = date.end;
            }

            let html = '<' + $scope.data.directive + ' setting="setting" dashboard="setting.dashboard" loading="loadingLayer" ></' + $scope.data.directive + '>';

            if ($scope.data.templateUrl) {
                html = '<' + $scope.data.directive + ' setting="setting" dashboard="setting.dashboard" template-url="' + $scope.data.templateUrl + '"  loading="loadingLayer" ></' + $scope.data.directive + '>';
            }
            element.html(html).show();
            $compile(element.contents())($scope);
        }
    }
});


ProjectApp.directive("cardContentText", function (HttpUtils) {
    return {
        replace: true,
        templateUrl: function (element, attr) {
            return (attr.templateUrl || "project/html/dashboard/content/card-content-text.html") + '?_t=' + window.appversion;
        },
        scope: {
            setting: "="
        },
        link: function ($scope) {
            if ($scope.setting.param) {
                HttpUtils.post($scope.setting.url, $scope.setting.param, function (response) {
                    $scope.data = response.data;
                });
            } else {
                HttpUtils.get($scope.setting.url, function (response) {
                    $scope.data = response.data;
                });
            }
        }
    }
});

