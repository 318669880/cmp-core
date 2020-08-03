ProjectApp.directive('passwordCheck', function () {
    return {
        require: 'ngModel',
        link: function (scope, elem, attributes, ctrl) {
            ctrl.$validators.passwordCheck = function (modelValue) {
                let passwordCheck = attributes.passwordCheck;
                let split = passwordCheck.split('.');
                let password = null;
                if (split.length === 1) {
                    password = scope[passwordCheck];
                } else {
                    let v = scope;
                    angular.forEach(split, function (data) {
                        if (angular.isUndefined(v[data])) {
                            return false;
                        } else {
                            v = v[data]
                        }
                    });
                    password = v;
                }
                return password === modelValue;
            };
        }
    };
});

ProjectApp.directive('colorPicker', function ($timeout) {
    return {
        priority: 0,
        require: '?ngModel',
        scope: {
            modelValue: '=ngModel'
        },
        link: function ($scope, element, attributes, ngModel) {

            let defaults = {
                control: 'hue',
                format: 'hex',
                keywords: '',
                inline: false,
                letterCase: 'uppercase',
                position: 'bottom left',
                swatches: [],
                change: function (hex, opacity) {
                    return hex
                },
                theme: 'default'
            };

            function isValidColor(color) {
                return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(color);
            }

            ngModel.$render = function () {
                $timeout(function () {
                    let color = ngModel.$viewValue;
                    setMinicolorsValue(color);
                }, 0, false);
            };

            function setMinicolorsValue(color) {
                if (isValidColor(color)) {
                    element.minicolors('value', color);
                } else {
                    element.minicolors('value', '');
                }
            }

            if (element.hasClass('minicolors-input')) {
                element.minicolors('destroy');
                element.off('blur', onBlur);
            }

            element.minicolors(defaults);

            element.on('blur', onBlur);

            function onBlur(e) {
                $scope.$apply(function() {
                    let color = element.minicolors('value');
                    if (isValidColor(color))
                        ngModel.$setViewValue(color);
                });
            }
        }
    };
});