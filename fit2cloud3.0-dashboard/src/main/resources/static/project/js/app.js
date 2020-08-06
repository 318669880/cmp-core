/**
 * 启动app，加载菜单
 */
let ProjectApp = angular.module('ProjectApp', ['f2c.common', 'ngDraggable']);

ProjectApp.config(function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'anonymous/all/i18n/',
        suffix: '.json?_t=' + window.appversion
    });
    $translateProvider.useSanitizeValueStrategy('escape', 'sanitizeParameters');
    $translateProvider.preferredLanguage(window.parent.userLocale || "zh_CN");
});

//自定义配置加载翻译文件

// ProjectApp.config(function ($translateProvider) {
//     $translateProvider.useStaticFilesLoader({
//         prefix: '/module1/anonymous/i18n/',
//         suffix: '.json?_t=' + window.appversion
//     },{
//         prefix: '/module2/anonymous/i18n/',
//         suffix: '.json?_t=' + window.appversion
//     });
//     $translateProvider.useSanitizeValueStrategy('escape', 'sanitizeParameters');
//     $translateProvider.preferredLanguage(window.parent.userLocale || "zh_CN");
// });

ProjectApp.controller('IndexCtrl', function ($scope) {
});
