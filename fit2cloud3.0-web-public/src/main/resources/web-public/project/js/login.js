let SSOApp = angular.module('ssoApp', ['ngAnimate', 'ngMaterial', 'ngMessages', 'pascalprecht.translate']);

SSOApp.config(function ($mdThemingProvider) {
    if (window.IndexConstants) {
        $mdThemingProvider.definePalette('primary', Palette.primary(window.IndexConstants['ui.theme.primary']));
        $mdThemingProvider.theme('default').primaryPalette('primary');

        $mdThemingProvider.definePalette('accent', Palette.accent(window.IndexConstants['ui.theme.accent']));
        $mdThemingProvider.theme('default').accentPalette('accent');
    }

});

SSOApp.config(function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'anonymous/i18n/',
        suffix: '.json?_t=' + window.appversion
    });
    $translateProvider.useSanitizeValueStrategy('escape', 'sanitizeParameters');
    $translateProvider.preferredLanguage(window.userLocale);

});

SSOApp.service('Translator', function ($filter, $translate) {
    this.get = function (key) {
        return $filter('translator')(key);
    };

    this.gets = function (key) {
        return $filter('translators')(key);
    };

    this.wait = function (func) {
        $translate("i18n_i18n").then(func);
    };

    this.setLang = function (lang) {
        $translate.use(lang);
    }
});

// 单Key翻译
SSOApp.filter('translator', function ($translate) {
    // 翻译过滤器，兼容没有使用国际化的情况
    function translator(input, defaultStr) {
        try {
            let str = $translate.instant(input);
            if (str === input && defaultStr) {
                return defaultStr;
            }
            return str === input ? "" : str;
        } catch (e) {
            return defaultStr ? defaultStr : "";
        }
    }

    if ($translate.statefulFilter()) {
        translator.$stateful = true;
    }

    return translator;
});

SSOApp.controller('loginCtrl', function ($scope, $timeout, $mdDialog, Translator) {
    $scope.user = {};

    $scope.background = "/web-public/fit2cloud/html/background/background.html?_t=" + window.appversion;

    $scope.submit = function () {
        if (!$scope.user.name || $scope.user.name === "" || !$scope.user.password || $scope.user.password === "") {
            return;
        }
        $(".login-error-msg").html("");
        $scope.loading = true;
        if (window.parent && window.parent !== window && window.parent.document) {
            // 如果被嵌入keycloak页面
            $(window.parent.document).find("#username").val($scope.user.name);
            $(window.parent.document).find("#password").val($scope.user.password);
            $(window.parent.document).find("#kc-login").click();
        } else {
            $timeout(function () {
                angular.element('#submit').trigger('click');
            });
        }
    };
    $scope.initLoading = function () {
        let loading = $(".loading-div");
        loading.css("width", $("md-progress-linear").width() + "px");
        loading.css("height", (loading.parent().height() - 5) + "px");
    };


    if (window.parent && window.parent !== window && window.parent.document) {
        // 如果被嵌入keycloak页面
        if (window.parent.parent !== window.parent) {
            $("md-content").css("display", "none");
            Translator.wait(function () {
                $mdDialog.show($mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title(Translator.get("i18n_warn"))
                    .textContent(Translator.get("i18n_login_expired"))
                    .ok(Translator.get("i18n_ok"))
                ).then(function () {
                    window.parent.parent.location.href = "/logout";
                });
            });
        }
        $("#login-form").submit(function (event) {
            event.preventDefault();
        });
        window.parent.document.title = window.document.title;
        $(window.parent.document).find('link[rel="shortcut icon"]').attr('href', $('link[rel="shortcut icon"]').attr('href'));
        if ($(".login-error-msg span").text() == "USER_ALREADY_LOGIN") {
            window.parent.parent.location.href = "/";
        }
        let kcFeedBackElement = $(window.parent.document).find(".kc-feedback-text");
        if (kcFeedBackElement && kcFeedBackElement.length > 0) {
            $(".login-error-msg span").text(kcFeedBackElement.text());
        }
        let kcErrorElement = $(window.parent.document).find("#kc-error-message p");
        if (kcErrorElement && kcErrorElement.length > 0) {
            $("md-content").css("display", "none");
            document.write("Keycloak Error Message: " + kcErrorElement.text());
        }
        let kcLoginBtn = $(window.parent.document).find("#kc-login");
        if (!kcLoginBtn || kcLoginBtn.length === 0) {
            $(window.parent.document).find('body div').css("display", "block");
            $(window.parent.document).find('.f2c-frame').css("display", "none");
        }
    }
});

SSOApp.directive('autoFocus', function ($timeout) {
    return {
        restrict: 'AC',
        link: function (_scope, _element) {
            $timeout(function () {
                window.focus();
                _element[0].focus();
                angular.element(_element[0]).trigger('click');
            }, 100);
        }
    };
});