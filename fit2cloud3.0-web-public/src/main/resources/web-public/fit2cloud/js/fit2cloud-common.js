/**
 * FIT2CLOUD 统一工具模块
 */

/**
 * frame打开时的进度条
 */
(function () {
    if (window.parent !== window) {
        let loadingProcess = $(window.parent.document).find(".frame-loading-process");
        if (loadingProcess && loadingProcess.length > 0) {
            loadingProcess.css("display", "block");
        }
        $(document).ready(function () {
            setTimeout(function () {
                let loadingProcess = $(window.parent.document).find(".frame-loading-process");
                if (loadingProcess && loadingProcess.length > 0) {
                    loadingProcess.css("display", "none");
                }
            }, Math.ceil(Math.random() * 500 + 1000));
        });
        window.parent.api.inbox(false);
        // change parent window url
        let module = sessionStorage.getItem("module");
        if (module) {
            try {
                if (module.split("?").length > 1) {
                    module = module.split("?")[0];
                    if (module !== "/") {
                        if (typeof (window.parent.history.pushState) === "function") {
                            window.parent.history.pushState(null, "", module);
                        }
                    }
                }
            } catch (error) {
                console.error(error);
            }
        }
    }
})();

/**
 * 加载自定义模块
 */
(function () {
    angular.module('f2c.module', [
        'ngAnimate',
        'ngMaterial',
        'ngMessages',
        'pascalprecht.translate',
        'f2c.module.menu',
        'f2c.module.permissions',
        'f2c.module.table',
        'f2c.module.filter',
        'f2c.module.tree',
        'f2c.module.wizard',
        'f2c.module.form',
        'f2c.module.metric',
        'f2c.module.chart',
        'f2c.module.others',
        'f2c.module.process'
    ]);

    let F2CCommon = angular.module('f2c.common', ['f2c.filter', 'f2c.service', 'f2c.module', 'cgBusy']);

    F2CCommon.config(function ($mdAriaProvider, $httpProvider) {
        $mdAriaProvider.disableWarnings();
        $httpProvider.interceptors.push(['$q', '$injector', function ($q, $injector) {
            return {
                response: function (response) {
                    let deferred = $q.defer();
                    let Translator = $injector.get('Translator');
                    if (response.headers("Authentication-Status") === "invalid" || (typeof (response.data) === "string" && response.data.indexOf("action=\"") > -1 && response.data.indexOf("<form") > -1 && response.data.indexOf("/form>") > -1)) {
                        deferred.reject('invalid session');
                        if (!window.parent.sessionInvalid) {
                            window.parent.sessionInvalid = true;
                            Translator.wait(function () {
                                let $mdDialog = $injector.get("$mdDialog");
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
                    } else {
                        deferred.resolve(response);
                    }
                    return deferred.promise;
                }
                ,
                request: function (request) {
                    //resolve ie cache issue
                    if (request.method === "GET" && request.headers.Accept.indexOf("json") !== -1 && request.url.indexOf(".") === -1) {
                        let d = new Date();
                        if (request.url.indexOf("?") === -1) {
                            request.url = request.url + '?_nocache=' + d.getTime();
                        } else {
                            request.url = request.url + '&_nocache=' + d.getTime();
                        }
                    }
                    return request;
                },
                responseError: function (err) {
                    let Notification = $injector.get("Notification");
                    if (-1 === err.status) {
                        // 远程服务器无响应
                        Notification.danger("No response from remote server", null, {delay: 5000});
                    }
                    if (502 === err.status) {
                        // Bad Gateway
                        Notification.danger("Bad Gateway", null, {delay: 5000});
                    }
                    return $q.reject(err);
                }
            };
        }]);
    });

    F2CCommon.config(function ($mdThemingProvider) {

        if (window.parent.IndexConstants) {
            $mdThemingProvider.definePalette('primary', Palette.primary(window.parent.IndexConstants['ui.theme.primary']));
            $mdThemingProvider.theme('default').primaryPalette('primary');

            $mdThemingProvider.definePalette('accent', Palette.accent(window.parent.IndexConstants['ui.theme.accent']));
            $mdThemingProvider.theme('default').accentPalette('accent');
        }

        $mdThemingProvider.definePalette('white', {
            '50': '#ffffff',
            '100': '#f5f5f5',
            '200': '#eeeeee',
            '300': '#e0e0e0',
            '400': '#bdbdbd',
            '500': '#9e9e9e',
            '600': '#757575',
            '700': '#616161',
            '800': '#424242',
            '900': '#212121',
            'A100': '#fafafa',
            'A200': '#000000',
            'A400': '#303030',
            'A700': '#616161',
            'contrastDefaultColor': 'dark',
            'contrastLightColors': '600 700 800 900 A200 A400 A700'
        });

        $mdThemingProvider.theme('default').backgroundPalette('white');
    });

    F2CCommon.config(function ($mdDateLocaleProvider) {
        let Translator = window.parent[window.parent.userLocale];
        if (Translator) {
            $mdDateLocaleProvider.months = Translator.months;
            $mdDateLocaleProvider.shortMonths = Translator.shortMonths;
            $mdDateLocaleProvider.days = Translator.days;
            $mdDateLocaleProvider.shortDays = Translator.shortDays;
            $mdDateLocaleProvider.msgCalendar = Translator.msgCalendar;
            $mdDateLocaleProvider.msgOpenCalendar = Translator.msgOpenCalendar;
        }

        $mdDateLocaleProvider.firstDayOfWeek = 0;

        $mdDateLocaleProvider.parseDate = function (dateString) {
            let m = moment(dateString, 'YYYY-MM-DD', true);
            return m.isValid() ? m.toDate() : new Date(NaN);
        };

        $mdDateLocaleProvider.formatDate = function (date) {
            if (!date) return '';
            let m = moment(date);
            return m.isValid() ? m.format('YYYY-MM-DD') : '';
        };

        $mdDateLocaleProvider.monthHeaderFormatter = function (date) {
            return date.getFullYear() + ' ' + $mdDateLocaleProvider.months[date.getMonth()];
        };
    });

    F2CCommon.config(function ($translateProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: 'anonymous/i18n/',
            suffix: '.json?_t=' + window.appversion
        });
        $translateProvider.useSanitizeValueStrategy('escape', 'sanitizeParameters');
        $translateProvider.preferredLanguage(window.parent.userLocale || "zh_CN");
    });

    F2CCommon.value('cgBusyDefaults', {
        message: '',
        templateUrl: 'web-public/fit2cloud/html/loading/loading.html' + '?_t=' + window.appversion,
        backdrop: true
    });

    F2CCommon.run(function ($transitions, $rootScope, MenuRouter, $window, $log) {
        // Translator.setLang($window.parent.userLocale);
        //页面初次打开或刷新后，根据state自动展开左侧菜单
        $rootScope.state = {
            name: ""
        };
        let menus = MenuRouter.getMenus();

        let isMenu = function (name) {
            for (let i = 0; i < menus.length; i++) {
                if (menus[i].name === name) {
                    return true;
                }
            }
            return false;
        };
        $transitions.onSuccess({}, function (data) {
            let global = data.router.globals;
            if (global && isMenu(global.current.name)) {
                $rootScope.state.name = global.current.name;
                sessionStorage.setItem('url', global.current.url);
            }
        });

        // Echart Resize
        angular.element($window).bind('resize', function () {
            let echarts_instances = angular.element("div[_echarts_instance_]");
            if (echarts_instances !== null && echarts_instances.length > 0) {
                for (let i = 0; i < echarts_instances.length; i++) {
                    try {
                        echarts.getInstanceByDom(echarts_instances[i]).resize();
                    } catch (e) {
                        $log.error(e);
                    }
                }
            }
        });
    });
})();

/**
 * 公用过滤器
 */
(function () {
    let F2CFilter = angular.module('f2c.filter', []);

    F2CFilter.service('Translator', function ($filter, $translate) {
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
    F2CFilter.filter('translator', function ($translate) {
        // 翻译过滤器，兼容没有使用国际化的情况
        function translator(input, defaultStr) {
            try {
                let str = $translate.instant(input);
                if (str === input && defaultStr) {
                    return defaultStr;
                }
                if (str === input && input.startsWith("i18n_")) {
                    return str === input ? "" : str;
                }
                return str;
            } catch (e) {
                return defaultStr ? defaultStr : input;
            }
        }

        if ($translate.statefulFilter()) {
            translator.$stateful = true;
        }

        return translator;
    });

    // 多Key翻译，例如: '订单号：{{key_1}}，交易商品：{{key_2}}，初步估价：{{key_3}}元'
    F2CFilter.filter('translators', function ($filter, $translate) {
        function match(str) {
            let r = str.match(/{{\s*[\w.\u4e00-\u9fa5]+\s*}}/g);
            if (r) {
                return r.map(function (x) {
                    return x.match(/[\w.\u4e00-\u9fa5]+/)[0];
                });
            }
        }

        function replace(string, find, replace) {
            if ((/[A-Za-z0-9_]+/g).test(string)) {
                return string.replace(new RegExp('\{\{(?:\\s+)?(' + find + ')(?:\\s+)?\}\}'), replace);
            } else {
                return string;
            }
        }

        function translators(input, defaultStr) {
            try {
                let array = match(input);
                if (array) {
                    let result = input;
                    angular.forEach(array, function (item) {
                        result = replace(result, item, $filter('translate')(item));
                    });
                    return result;
                }
                let str = $filter('translate')(input);
                if (str === input && defaultStr) {
                    return defaultStr;
                }
                return str;
            } catch (e) {
                return defaultStr ? defaultStr : input;
            }
        }

        if ($translate.statefulFilter()) {
            translators.$stateful = true;
        }

        return translators;
    });

    F2CFilter.filter("selects", function () {
        return function (input, array) {
            if (array) {
                for (let i = 0; i < array.length; i++) {
                    if (input === array[i].key) {
                        return array[i].name
                    }
                }
            }
            return input;
        }
    });

    F2CFilter.filter('filterItems', function () {
        return function (optionList, textField, example) {
            var output = [];
            angular.forEach(optionList, function (item) {
                if(example == "" || example == undefined){
                    output.push(item);
                }else {
                    if(item[textField].indexOf(example) != -1){
                        output.push(item);
                    }
                }
            });
            return output;
        };
    });

    // 多个属性搜索，或的关系，person in peoples | propsFilter: {name: $select.search, age: $select.search}
    F2CFilter.filter('propsFilter', function () {
        return function (items, props) {
            let out = [];

            if (angular.isArray(items)) {
                let keys = Object.keys(props);

                items.forEach(function (item) {
                    let itemMatches = false;

                    for (let i = 0; i < keys.length; i++) {
                        let prop = keys[i];
                        if (!props[prop]) {
                            itemMatches = true;
                            break;
                        }
                        let text = props[prop].toLowerCase();
                        if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                            itemMatches = true;
                            break;
                        }
                    }

                    if (itemMatches) {
                        out.push(item);
                    }
                });
            } else {
                out = items;
            }

            return out;
        };
    });
})();

/**
 * 公用服务
 */
(function () {
    let F2CService = angular.module('f2c.service', []);

    F2CService.service('HttpUtils', function ($http, Notification, $log) {
        this.get = function (url, success, error, config) {
            return $http.get(url, config).then(function (response) {
                if (!response.data) {
                    //处理不是ResultHolder的结果
                    return success(response);
                } else if (response.data.success) {
                    return success(response.data);
                } else {
                    if (error) {
                        error(response.data);
                    } else {
                        Notification.danger(response.data.message);
                        $log.error(response);
                    }
                }
            }, function (response) {
                if (error) {
                    error(response.data);
                } else {
                    Notification.danger(response.data.message);
                    $log.error(response);
                }
            });
        };

        this.post = function (url, data, success, error, config) {
            return $http.post(url, data, config).then(function (response) {
                if (!response.data) {
                    //处理不是ResultHolder的结果
                    return success(response);
                } else if (response.data.success) {
                    return success(response.data);
                } else {
                    if (error) {
                        error(response.data);
                    } else {
                        Notification.danger(response.data.message);
                        $log.error(response);
                    }
                }
            }, function (response) {
                if (error) {
                    error(response.data);
                } else {
                    Notification.danger(response.data.message);
                    $log.error(response);
                }
            });
        };

        this.delete = function (url, success, error, config) {
            return $http.delete(url, config).then(function (response) {
                if (!response.data) {
                    //处理不是ResultHolder的结果
                    success(response);
                } else if (response.data.success) {
                    success(response.data);
                } else {
                    if (error) {
                        error(response.data);
                    } else {
                        Notification.danger(response.data.message);
                        $log.error(response);
                    }
                }
            }, function (response) {
                if (error) {
                    error(response.data);
                } else {
                    Notification.danger(response.data.message);
                    $log.error(response);
                }
            });
        };

        this.paging = function ($scope, url, data, callBack, error) {
            let self = this;
            let method = callBack;
            let result = function (response) {
                $scope.items = response.data["listObject"];
                $scope.pagination.formData = response.data["param"];
                $scope.pagination.itemCount = response.data["itemCount"];
                $scope.pagination.pageCount = response.data['pageCount'];
                if ($scope.pagination.pageCount > 0) {
                    $scope.pagination.show = true;
                    $scope.pagination.pageCount = response.data['pageCount'];
                } else {
                    $scope.pagination.show = false;
                }
                if (angular.isFunction(method)) {
                    method(response);
                }
            };
            $scope.pagination = angular.extend({
                page: 1,
                limit: 10,
                limitOptions: [10, 20, 50, 100]
            }, $scope.pagination);

            $scope.pagination.onPaginate = function () {
                $scope.items = [];
                if (data !== undefined && data !== null) {
                    $scope.loadingLayer = self.post(url + "/" + $scope.pagination.page + "/" + $scope.pagination.limit, data, result, error);
                } else {
                    $scope.loadingLayer = self.get(url + "/" + $scope.pagination.page + "/" + $scope.pagination.limit, result, error);
                }

            };

            $scope.pagination.onPaginate();
        };

        this.download = function (url, data, filename, mime) {
            return $http({
                url: url,
                method: 'post',
                responseType: 'arraybuffer',
                data: data
            }).then(function (response) {
                let blob = new Blob([response.data], {type: mime});
                saveAs(blob, filename);
            }, function (response) {
                Notification.danger('file download error.');
                $log.error(response);
            });
        }
    });

    F2CService.service('Loading', function ($q) {
        let promises = [];
        this.add = function (promise) {
            promises.push(promise);
        };

        this.load = function () {
            let promises_q = $q.all(promises);
            promises = [];
            return promises_q;
        }
    });

    F2CService.directive("notice", function () {
        return {
            replace: true,
            template: '<div id="_notification" class="notify">\n' +
                '    <div class="notify-position notify-left" ng-if="messages.left.length > 0">\n' +
                '        <message ng-repeat="msg in messages.left" msg="msg" data="messages.left"></message>\n' +
                '    </div>\n' +
                '    <div class="notify-position notify-center" ng-if="messages.center.length > 0">\n' +
                '        <message ng-repeat="msg in messages.center" msg="msg" data="messages.center"></message>\n' +
                '    </div>\n' +
                '    <div class="notify-position notify-right" ng-if="messages.right.length > 0">\n' +
                '        <message ng-repeat="msg in messages.right" msg="msg" data="messages.right"></message>\n' +
                '    </div>\n' +
                '</div>\n',
            scope: {
                messages: "="
            },
            link: function ($scope, element) {
                element.on('$destroy', function () {
                    $scope.messages.left = [];
                    $scope.messages.center = [];
                    $scope.messages.right = [];
                });
            }
        };
    });

    F2CService.directive("message", function ($timeout) {
        return {
            replace: true,
            template: '<div layout="row" class="notify-item" ng-class="msg.type">\n' +
                '    <div class="notify-msg">{{msg.msg}}</div>\n' +
                '    <div flex></div>\n' +
                '    <div class="notify-close" md-ink-ripple ng-click="close()">\n' +
                '        <md-icon>close</md-icon>\n' +
                '    </div>\n' +
                '</div>',
            scope: {
                data: "=",
                msg: "="
            },
            link: function ($scope) {
                $scope.exec = function (callback) {
                    if (angular.isFunction(callback)) {
                        callback();
                    }
                };

                $scope.remove = function () {
                    if ($scope.data.length > 0) {
                        for (let i = 0; i < $scope.data.length; i++) {
                            if ($scope.data[i].$$hashKey === $scope.msg.$$hashKey) {
                                $scope.data.splice(i, 1);
                                break;
                            }
                        }
                        $scope.exec($scope.msg.close);
                    }
                };

                //自动关闭
                $timeout(function () {
                    $scope.remove();
                }, $scope.msg.delay);

                // 手动关闭
                $scope.close = function () {
                    if ($scope.data.length > 0) {
                        $scope.remove();
                    }
                };
            }
        };
    });

    F2CService.service('Notification', function ($mdToast, $mdDialog, $compile, $rootScope, $document, Translator) {
        let template = "<notice messages='messages'></notice>";
        let scope = $rootScope.$new();
        scope.messages = {
            left: [],
            center: [],
            right: []
        };

        function add(msg, type, callback, option) {
            let item = angular.extend({
                msg: msg,
                close: callback,
                position: "notify-right",
                type: type,
                delay: 2000
            }, option);

            switch (item.position) {
                case "notify-left":
                    scope.messages.left.unshift(item);
                    break;
                case "notify-center":
                    scope.messages.center.unshift(item);
                    break;
                case "notify-right":
                    scope.messages.right.unshift(item);
                    break;
                default:
                    scope.messages.right.unshift(item);
            }

            let notify = $("#_notification");
            if (notify.length === 0) {
                angular.element("md-content[ui-view]").append($compile(template)(scope));
            }
        }

        this.show = function (msg, callback, option) {
            add(msg, "notify-default", callback, option);
        };

        this.info = function (msg, callback, option) {
            add(msg, "notify-primary", callback, option);
        };

        this.success = function (msg, callback, option) {
            add(msg, "notify-success", callback, option);
        };

        this.warn = function (msg, callback, option) {
            add(msg, "notify-warn", callback, angular.extend({
                delay: 10000
            }, option));
        };

        this.danger = function (msg, callback, option) {
            add(msg, "notify-danger", callback, angular.extend({
                delay: 30000
            }, option));
        };

        this.alert = function (msg) {
            $mdDialog.show($mdDialog.alert()
                .multiple(true)
                .clickOutsideToClose(true)
                .title(Translator.get("i18n_warn"))
                .textContent(msg)
                .ok(Translator.get("i18n_ok"))
            );
        };

        this.confirm = function (msg, success, cancel) {
            let confirm = $mdDialog.confirm()
                .multiple(true)
                .title(Translator.get("i18n_confirm"))
                .textContent(msg)
                .ok(Translator.get("i18n_ok"))
                .cancel(Translator.get("i18n_cancel"));

            $mdDialog.show(confirm).then(function () {
                if (angular.isFunction(success)) success();
            }, function () {
                if (angular.isFunction(cancel)) cancel();
            });
        };

        this.prompt = function (obj, success, cancel) {
            let locals = {
                title: obj.title,
                text: obj.text,
                placeholder: obj.placeholder,
                init: obj.init,
                required: obj.required,
                type: obj.type || 'text',

                selectValue: obj.selectValue,
                selectKey: obj.selectKey,
                selectItems: obj.selectItems,
                selectText: obj.selectText,
                selectRequired: obj.selectRequired || false,

                multiSelectValue: obj.multiSelectValue,
                multiSelectKey: obj.multiSelectKey,
                multiSelectItems: obj.multiSelectItems,
                multiSelectText: obj.multiSelectText,
                multiSelectRequired: obj.multiSelectRequired || false
            };
            $mdDialog.show({
                multiple: true,
                templateUrl: "/web-public/fit2cloud/html/notice/prompt.html" + '?_t=' + window.appversion,
                parent: angular.element($document[0].body),
                controller: function ($scope, $mdDialog, title, text, placeholder, init, required, type, selectRequired, selectValue, selectKey, selectItems, selectText, multiSelectRequired, multiSelectValue, multiSelectKey, multiSelectItems, multiSelectText) {
                    $scope.title = title;
                    $scope.text = text;
                    $scope.placeholder = placeholder;
                    $scope.required = required;
                    $scope.showInput = text? true : false;
                    $scope.value = init || null;
                    $scope.type = type;

                    $scope.selected = null;
                    $scope.selectValue =  selectValue;
                    $scope.selectKey =  selectKey;
                    $scope.selectItems =  selectItems;
                    $scope.selectText =  selectText;
                    $scope.showSelect = angular.isArray(selectItems) ? true : false;
                    $scope.selectRequired = selectRequired;

                    $scope.multiSelected = null;
                    $scope.multiSelectValue =  multiSelectValue;
                    $scope.multiSelectKey =  multiSelectKey;
                    $scope.multiSelectItems =  multiSelectItems;
                    $scope.multiSelectText =  multiSelectText;
                    $scope.showMultiSelect = angular.isArray(multiSelectItems) ? true : false;
                    $scope.multiSelectRequired = multiSelectRequired;


                    $scope.close = function () {
                        $mdDialog.cancel();
                    };

                    $scope.ok = function () {
                        if($scope.selectItems || $scope.multiSelectItems){
                            let result = {
                                value: $scope.value,
                                selected: $scope.selected,
                                multiSelected: $scope.multiSelected
                            };
                            $mdDialog.hide(result);
                        }else {
                            $mdDialog.hide($scope.value);
                        }
                    }
                },
                locals: locals,
                clickOutsideToClose: false
            }).then(function (result) {
                if (angular.isFunction(success)) success(result);
            }, function () {
                if (angular.isFunction(cancel)) cancel();
            });
        };

    });

    F2CService.factory('DateSelectService', function () {
        let factory = {};
        factory.DATE_SELECT_TYPE = {
            LAST_1_DAYS: "LAST_1_DAYS",
            LAST_3_DAYS: "LAST_3_DAYS",
            LAST_7_DAYS: "LAST_7_DAYS",
            LAST_14_DAYS: "LAST_14_DAYS",
            LAST_30_DAYS: "LAST_30_DAYS",
            LAST_1_MONTHS: "LAST_1_MONTHS",
            LAST_3_MONTHS: "LAST_3_MONTHS",
            LAST_6_MONTHS: "LAST_6_MONTHS",
            LAST_12_MONTHS: "LAST_12_MONTHS",
            LAST_1_YEARS: "LAST_1_YEARS",
            LAST_3_YEARS: "LAST_3_YEARS",
            RANGE: "RANGE",
            NO_LIMIT: "NO_LIMIT"
        };

        factory.calculateDate = function (type) {
            let end = moment().endOf("days");
            let start;

            switch (type) {
                case this.DATE_SELECT_TYPE.LAST_1_DAYS:
                    start = moment().startOf("days");
                    break;
                case this.DATE_SELECT_TYPE.LAST_3_DAYS:
                    start = moment().subtract(2, "days").startOf("days");
                    break;
                case this.DATE_SELECT_TYPE.LAST_7_DAYS:
                    start = moment().subtract(6, "days").startOf("days");
                    break;
                case this.DATE_SELECT_TYPE.LAST_14_DAYS:
                    start = moment().subtract(13, "days").startOf("days");
                    break;
                case this.DATE_SELECT_TYPE.LAST_30_DAYS:
                    start = moment().subtract(29, "days").startOf("days");
                    break;
                case this.DATE_SELECT_TYPE.LAST_1_MONTHS:
                    start = moment().startOf("months");
                    break;
                case this.DATE_SELECT_TYPE.LAST_3_MONTHS:
                    start = moment().subtract(2, "months").startOf("months");
                    break;
                case this.DATE_SELECT_TYPE.LAST_6_MONTHS:
                    start = moment().subtract(5, "months").startOf("months");
                    break;
                case this.DATE_SELECT_TYPE.LAST_12_MONTHS:
                    start = moment().subtract(11, "months").startOf("months");
                    break;
                case this.DATE_SELECT_TYPE.LAST_1_YEARS:
                    start = moment().startOf("years");
                    break;
                case this.DATE_SELECT_TYPE.LAST_3_YEARS:
                    start = moment().subtract(2, "years").startOf("years");
                    break;
                case this.DATE_SELECT_TYPE.RANGE:
                    return false;
                case this.DATE_SELECT_TYPE.NO_LIMIT:
                    return false;
            }

            return {
                end: end.valueOf(),
                start: start.valueOf()
            }
        };

        return factory;
    });

    F2CService.service('UserService', function ($window, HttpUtils) {
        let user = $window.parent.userInfo || {};

        if (!user.id) {
            HttpUtils.get("user/info", function (response) {
                user = angular.extend(user, response.data);
            });
        }

        this.getUserInfo = function () {
            return user;
        };

        this.isAdmin = function () {
            return user.parentRoleId === 'ADMIN';
        };

        this.isOrgAdmin = function () {
            return user.parentRoleId === 'ORGADMIN';
        };

        this.isUser = function () {
            return user.parentRoleId === 'USER';
        };

        this.getRoleIdList = function () {
            return user.roleIdList;
        };

        this.getWorkSpaceId = function () {
            return user.workspaceId;
        };

        this.getOrganizationId = function () {
            return user.organizationId;
        };
    });

    F2CService.directive("organizationInfo", function (HttpUtils, $window) {
        return {
            restrict: 'E',
            template: '<span>{{organzitionName}}</span>',
            scope: {
                id: "="
            },
            link: function ($scope) {

                if (!$window.parent.organzitionMap) {
                    $window.parent.organzitionMap = {};
                }

                if ($window.parent.organzitionMap[$scope.id]) {
                    $scope.organzitionName = $window.parent.organzitionMap[$scope.id];
                } else {
                    HttpUtils.get('condition/' + $scope.id + '/organization', function (response) {
                            $scope.organzitionName = response.data;
                            $window.parent.organzitionMap[$scope.id] = $scope.organzitionName;
                        }
                    )
                    ;
                }
            }
        }
    });

    F2CService.directive("cloudAccountInfo", function (HttpUtils, $window) {
        return {
            restrict: 'E',
            template: '<span ng-if="loading"><i class="fa fa-spinner fa-spin"></i>&nbsp;{{"i18n_loading" | translator}}...</span>' +
                '<span ng-if="account && !loading"><img ng-src="{{account.icon}}" width="16px" height="16px" style="vertical-align:middle" />&nbsp;{{account.accountName}}<span ng-if="account.accountStatus === \'INVALID\'" style="color: red">&nbsp;&nbsp;({{"i18n_invalid" | translator}})</span></span><span ng-if="!account && !loading">{{"i18n_chart_unknown" | translator}}</span>',
            scope: {
                id: "="
            },
            link: function ($scope) {
                $scope.loading = true;
                if (!$window.parent.cloudAccountMap) {
                    $window.parent.cloudAccountMap = {};
                }

                if ($window.parent.cloudAccountMap[$scope.id]) {
                    $scope.account = $window.parent.cloudAccountMap[$scope.id];
                    $scope.loading = false;
                } else {
                    HttpUtils.get('condition/' + $scope.id + '/cloud/account', function (response) {
                        if (response.data && response.data.icon) {
                            $scope.account = response.data;
                            $window.parent.cloudAccountMap[$scope.id] = $scope.account;
                        }
                        $scope.loading = false;
                    });
                }
            }
        }
    });

    F2CService.directive("dbassClusterInfo", function (HttpUtils, $window) {
        return {
            restrict: 'E',
            template: '<span ng-if="loading"><i class="fa fa-spinner fa-spin"></i>&nbsp;{{"i18n_loading" | translator}}...</span>' +
                '<span ng-if="cluster && !loading"><img ng-src="{{cluster.icon}}" width="16px" height="16px" style="vertical-align:middle" />&nbsp;{{cluster.name}}<span ng-if="cluster.status === \'INVALID\'" style="color: red">&nbsp;&nbsp;({{"i18n_invalid" | translator}})</span></span><span ng-if="!cluster && !loading">{{"i18n_chart_unknown" | translator}}</span>',
            scope: {
                id: "="
            },
            link: function ($scope) {
                $scope.loading = true;
                if (!$window.parent.dbassClusterMap) {
                    $window.parent.dbassClusterMap = {};
                }

                if ($window.parent.dbassClusterMap[$scope.id]) {
                    $scope.cluster = $window.parent.dbassClusterMap[$scope.id];
                    $scope.loading = false;
                } else {
                    HttpUtils.get('dbaas/cluster/' + $scope.id, function (response) {
                        if (response.data && response.data.icon) {
                            $scope.cluster = response.data;
                            $window.parent.dbassClusterMap[$scope.id] = $scope.cluster;
                        }
                        $scope.loading = false;
                    });
                }
            }
        }
    });

    F2CService.directive("workspaceInfo", function (HttpUtils, $window) {
        return {
            restrict: 'E',
            template: '<span style="cursor: pointer;" ng-if="workspace">' +
                '<md-tooltip md-delay="200" class="f2c-tooltip-user">{{"i18n_organization" | translator}}：{{workspace.organizationName}}<br>{{"i18n_workspace" | translator}}：{{workspace.workspaceName}}</md-tooltip>' +
                '{{workspace.workspaceName}}</span>' +
                '<span ng-if="!workspace && defaultValue">{{defaultValue}}</span>',
            scope: {
                id: "=",
                defaultValue: "@"
            },
            link: function ($scope) {

                //特殊处理
                if ($scope.id === 'root') {
                    $scope.workspace = null;
                } else {
                    if (!$window.parent.workspaceMap) {
                        $window.parent.workspaceMap = {};
                    }

                    if ($window.parent.workspaceMap[$scope.id]) {
                        $scope.workspace = $window.parent.workspaceMap[$scope.id];
                    } else {
                        HttpUtils.get('condition/' + $scope.id + '/workspace', function (response) {
                            $scope.workspace = response.data;
                            $window.parent.workspaceMap[$scope.id] = $scope.workspace;
                        });
                    }
                }
            }
        }
    });

    F2CService.directive('userInfo', function (HttpUtils, $window) {
        return {
            restrict: 'E',
            template: '<span>' +
                '<span ng-repeat="user in userList track by $index" style="cursor: pointer;">' +
                '<md-tooltip md-delay="200" class="f2c-tooltip-user">{{"i18n_user" | translator}}ID：{{user.id}}<br> {{"i18n_user_name_" | translator}}：{{user.name}}<br/>{{"i18n_email" | translator}}：{{user.email}}</md-tooltip>' +
                '<span><span ng-if="user.exist">{{user.name}}</span><span ng-if="!user.exist">{{user.id}}</span></span>' +
                '<span ng-if="$index < userList.length-1"><br></span></span>' +
                '</span>',
            scope: {
                userId: "=",
                userIds: "="
            },
            link: function ($scope) {
                if (!$window.parent.userMap) {
                    $window.parent.userMap = {};
                }
                let userIdList = [];
                $scope.userList = [];
                if ($scope.userId) {
                    if ($window.parent.userMap[$scope.userId]) {
                        $scope.userList.push($window.parent.userMap[$scope.userId]);
                    } else {
                        userIdList.push($scope.userId);
                    }

                }
                if (angular.isArray($scope.userIds)) {
                    angular.forEach($scope.userIds, function (id) {
                        if (!$scope.userId || $scope.userId !== id) {
                            if ($window.parent.userMap[id]) {
                                $scope.userList.push($window.parent.userMap[id]);
                            } else {
                                userIdList.push(id);
                            }
                        }
                    });
                }

                if (userIdList.length > 0) {
                    HttpUtils.post('condition/user/tooltip', userIdList, function (response) {
                        angular.forEach(response.data, function (userTooltip) {
                            $scope.userList.push(userTooltip);
                            $window.parent.userMap[userTooltip.id] = userTooltip;
                        });
                    });
                }

            }
        }
    });

    F2CService.directive("f2cTooltip", function () {
        return {
            restrict: 'E',
            template: '<span><md-tooltip  md-delay="200" class="f2c-tooltip-user">{{name}}</md-tooltip>{{displayName}}</span>',
            scope: {
                name: "@",
                limit: "@"
            },
            link: function ($scope) {
                if (!$scope.limit || $scope.limit <= 0) {
                    //默认截取10个字符长度
                    $scope.limit = 10;
                }

                if ($scope.name && $scope.name.length > $scope.limit) {
                    $scope.displayName = $scope.name.substring(0, $scope.limit) + '...';
                } else {
                    $scope.displayName = $scope.name;
                }
            }
        }
    });

    F2CService.service('eyeService', function () {
        this.view = function (password, eye, type) {
            let passwordElement = angular.element(password);
            let eyeElement = angular.element(eye);
            eyeElement.removeClass();
            if (passwordElement[0].type === 'password') {
                if (type) {
                    passwordElement[0].type = type;
                } else {
                    passwordElement[0].type = 'text';
                }
                eyeElement.addClass("fa fa-eye-slash f2c-eye");
            } else {
                passwordElement[0].type = 'password';
                eyeElement.addClass("fa fa-eye f2c-eye");
            }
        };
    });
})();

/**
 * 菜单,Menu
 */
(function () {
    let F2CModule = angular.module('f2c.module.menu', ['ui.router']);

    F2CModule.service('MenuRouter', function ($stateRegistry, $urlService, $window) {
        let MODULE_SAVE, MENUS_SAVE = [];
        let DELAY_MENUS = [];

        let addStates = function (states) {
            $urlService.deferIntercept();
            angular.forEach(states, function (menu) {
                if (!$stateRegistry.get(menu)) {
                    $stateRegistry.register(menu);
                }
            });
            $urlService.listen();
            $urlService.sync();
        };

        let addState = function (menu) {
            let state = addParams(menu);
            if (state.default) {
                $urlService.rules.when("", state.url);
            }
            $stateRegistry.register(state);
            MENUS_SAVE.push(state);
        };

        let addParams = function (state) {
            if (!state.params) {
                state.params = {
                    param: {}
                }
            }
            return addVersion(state);
        };

        let addVersion = function (state) {
            if (state.templateUrl.indexOf("?_t=") < 0) {
                state.templateUrl += '?_t=' + window.appversion;
            }
            return state;
        };

        /**
         * 动态注册路由
         */
        this.register = function (module) {
            $window.parent.currentModule = module.id;
            if (!module.menus) return;
            $urlService.deferIntercept();
            angular.forEach(module.menus, function (menu) {
                if (menu.children) {
                    angular.forEach(menu.children, function (subMenu) {
                        addState(subMenu);
                    })
                } else {
                    addState(menu);
                }
            });
            $urlService.listen();
            $urlService.sync();
            MODULE_SAVE = module;
            addStates(DELAY_MENUS);
        };

        this.addStates = function (menus) {
            if (MODULE_SAVE) {
                // 如果在module.json加载后添加，则直接加
                addStates(menus);
            } else {
                // 如果在module.json加载前添加，则必须先注册到DELAY_MENUS，等待module.json加载完成
                angular.forEach(menus, function (menu) {
                    DELAY_MENUS.push(menu);
                });
            }
        };

        this.getModule = function () {
            return MODULE_SAVE;
        };

        this.getMenus = function () {
            return MENUS_SAVE;
        }
    });

    F2CModule.directive("module", function (HttpUtils, MenuRouter, $rootScope, $log, Translator) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/menu/module.html' + '?_t=' + window.appversion,
            scope: {
                states: "=",
                dev: "=" // 开发模式
            },
            link: function ($scope) {
                let API_GET_MODULES = "module/menus";
                let USER_CURRENT_ROLE = "user/current/parent/role";
                let USER_CURRENT_ROLE_ID = "user/current/parent/roleId";

                $scope.init = function () {
                    if ($scope.dev) {
                        $scope.module = $scope.dev;
                        MenuRouter.register($scope.module);
                    } else {
                        $scope.registerModule();
                    }
                    HttpUtils.get(USER_CURRENT_ROLE, function (response) {
                        $rootScope.currentRole = response.data;
                    });
                    HttpUtils.get(USER_CURRENT_ROLE_ID, function (response) {
                        $rootScope.currentRoleId = response.data;
                    });
                    $rootScope.roleConst = {
                        admin: "ADMIN",
                        orgAdmin: "ORGADMIN",
                        user: "USER",
                        system: "SYSTEM",
                        anonymous: "ANONYMOUS"
                    }

                };

                $scope.registerModule = function () {
                    HttpUtils.get(API_GET_MODULES, function (response) {
                        try {
                            $scope.module = response.data;
                            MenuRouter.register($scope.module);
                            if ($scope.states) {
                                MenuRouter.addStates($scope.states);
                            }
                        } catch (e) {
                            $log.error(Translator.get("i18n_registry_module_failed") + ": " + name, response.data, e);
                        }
                    });
                };

                $scope.init();
            }
        };
    });

    F2CModule.directive("menuToggle", function ($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/menu/menu-toggle.html' + '?_t=' + window.appversion,
            scope: {
                menu: "="
            },
            link: function ($scope, element) {
                if ($scope.menu.icon.indexOf("/") > -1) {
                    $scope.iconType = "img";
                } else {
                    $scope.iconType = "icon";
                }

                $scope.toggleElement = element.find(".menu-toggle-list");

                $scope.toggled = false;

                $scope.toggle = function () {
                    $scope.toggled = !$scope.toggled;
                };

                $scope.open = function (menu) {
                    $rootScope.state.name = menu.name;
                };

                $scope.state = $rootScope.state;

                $scope.$watch("state.name", function (value) {
                    if (value === undefined) {
                        return;
                    }
                    $scope.toggled = false;
                    angular.forEach($scope.menu.children, function (child) {
                        if (child.name === $rootScope.state.name) {
                            $scope.toggled = true;
                        }
                    })
                });
            }
        };
    });
})();

/**
 * 权限,Permissions
 */
(function () {
    let F2CModule = angular.module('f2c.module.permissions', []);

    F2CModule.service("AuthService", function ($window) {

        let moduleId = $window.parent.currentModule;

        return {
            hasPermission: function (permission, module) {
                if (permission === undefined) {
                    return false;
                }

                let permissionMap = module ? $window.parent.modulePermissions[module] : $window.parent.modulePermissions[moduleId];

                if (permissionMap == null) {
                    return false;
                }

                let authPermissions = permissionMap[permission.split(":")[0]];

                if (authPermissions === undefined) {
                    return false;
                }
                for (let i = 0; i < authPermissions.length; i++) {
                    if (authPermissions[i] === permission) {
                        return true;
                    }
                }
                return false;
            },

            hasPermissions: function (permission, module) {
                if (permission === undefined) {
                    return false;
                }
                let permissionMap = module ? $window.parent.modulePermissions[module] : $window.parent.modulePermissions[moduleId];

                if (permissionMap == null) {
                    return false;
                }

                let permissions = permission.split(",");
                for (let j = 0; j < permissions.length; j++) {
                    let authPermissions = permissionMap[permissions[j].split(":")[0]];
                    if (authPermissions === undefined) {
                        continue;
                    }
                    for (let i = 0; i < authPermissions.length; i++) {
                        if (authPermissions[i] === permissions[j]) {
                            return true;
                        }
                    }
                }
                return false;
            },

            lackPermission: function (permission, module) {
                if (permission === undefined) {
                    return true;
                }
                let permissionMap = module ? $window.parent.modulePermissions[module] : $window.parent.modulePermissions[moduleId];
                if (permissionMap == null) {
                    return true;
                }

                let authPermissions = permissionMap[permission.split(":")[0]];
                if (authPermissions === undefined) {
                    return true;
                }
                for (let i = 0; i < authPermissions.length; i++) {
                    if (authPermissions[i] === permission) {
                        return false;
                    }
                }
                return true;
            },
            lackPermissions: function (permission, module) {
                if (permission === undefined) {
                    return true;
                }
                let permissionMap = module ? $window.parent.modulePermissions[module] : $window.parent.modulePermissions[moduleId];
                if (permissionMap == null) {
                    return true;
                }
                let permissions = permission.split(",");
                for (let j = 0; j < permissions.length; j++) {
                    let authPermissions = permissionMap[permissions[j].split(":")[0]];
                    if (authPermissions === undefined) {
                        continue;
                    }
                    for (let i = 0; i < authPermissions.length; i++) {
                        if (authPermissions[i] === permissions[j]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    });

    F2CModule.directive("hasPermission", function ($compile, AuthService) {
        return {
            restrict: 'A',
            terminal: true,
            priority: 50000,
            compile: function compile(element) {
                element.removeAttr("has-permission");
                return {
                    pre: function preLink($scope, element, attr) {
                        if (!AuthService.hasPermission(attr["hasPermission"], attr['module'])) {
                            element.attr('ng-if', false);
                        }
                        $compile(element)($scope);
                    }
                };
            }
        };
    });

    F2CModule.directive("hasPermissions", function ($compile, AuthService) {
        return {
            restrict: 'A',
            terminal: true,
            priority: 50000,
            compile: function compile(element) {
                element.removeAttr("has-permissions");
                return {
                    pre: function preLink($scope, element, attr) {
                        if (!AuthService.hasPermissions(attr["hasPermissions"], attr['module'])) {
                            element.attr('ng-if', false);
                        }
                        $compile(element)($scope);
                    }
                };
            }
        };
    });

    F2CModule.directive("lackPermission", function ($compile, AuthService) {
        return {
            restrict: 'A',
            terminal: true,
            priority: 50000,
            compile: function compile(element) {
                element.removeAttr("lack-permission");
                return {
                    pre: function preLink($scope, element, attr) {
                        if (!AuthService.lackPermission(attr["lackPermission"], attr['module'])) {
                            element.attr('ng-if', false);
                        }
                        $compile(element)($scope);
                    }
                };
            }
        };
    });

    F2CModule.directive("lackPermissions", function ($compile, AuthService) {
        return {
            restrict: 'A',
            terminal: true,
            priority: 50000,
            compile: function compile(element) {
                element.removeAttr("lack-permissions");
                return {
                    pre: function preLink($scope, element, attr) {
                        if (!AuthService.lackPermissions(attr["lackPermissions"], attr['module'])) {
                            element.attr('ng-if', false);
                        }
                        $compile(element)($scope);
                    }
                };
            }
        };
    });
})();

/**
 * 表格,Table
 */
(function () {
    let F2CModule = angular.module('f2c.module.table', []);

    F2CModule.directive("dynamicTable", function () {
        return {
            restrict: 'A',
            replace: true,
            transclude: true,
            templateUrl: "web-public/fit2cloud/html/table/dynamic-table.html" + '?_t=' + window.appversion,
            scope: {
                execute: "&",
                columns: "="
            },
            link: function ($scope, element, attr, ctrl, transclude) {
                $scope.getColumn = function (id) {
                    for (let i = 0; i < $scope.columns.length; i++) {
                        let c = $scope.columns[i];
                        if (c.$$hashKey === id) {
                            return c;
                        }
                    }
                };

                $scope.init = function () {
                    element.find("#_table_").append(transclude());
                    $scope.sortType = "desc";
                    $scope.sortCol = "";
                };

                $scope.sort = function (col) {
                    if (col.sort === false) return;
                    if ($scope.sortCol !== col.key) {
                        $scope.sortCol = col.key;
                    } else {
                        if ($scope.sortType === "asc") {
                            $scope.sortType = "desc";
                        } else {
                            $scope.sortType = "asc";
                        }
                    }

                    $scope.execute({sort: col.key, type: $scope.sortType, sql: col.key + " " + $scope.sortType});
                };

                $scope.refresh = function () {
                    if (!$scope.columns || $scope.columns.length === 0) return false;
                    for (let index = 0; index < $scope.columns.length; index++) {
                        let column = $scope.columns[index];
                        let td = element.find("#_table_").find("tbody tr").find("td:eq(" + index + ")");
                        if (!angular.isDefined(column.checked) || column.checked) {
                            td.show();
                        } else {
                            td.hide();
                        }
                        if ($scope.resize === true && column.default && !td.hasClass("column-fixed")) {
                            td.addClass("column-fixed");
                        }
                    }
                    return true;
                };

                $scope.init();
            }
        };
    });

    F2CModule.directive("selectColumns", function (UserService) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/table/select-columns.html" + '?_t=' + window.appversion,
            scope: {
                columns: "=",
                storage: "@" // false时禁用存储
            },
            link: function ($scope) {
                let user = UserService.getUserInfo();

                // 初始化，用watch处理异步传递columns
                let clear = $scope.$watch('columns', function (value) {
                    if (value && value.length > 0) {
                        if ($scope.storage !== false) {
                            get();
                        }
                        check();
                        clear();
                    }
                });

                if ($scope.storage !== false) {
                    // 保存变更内容
                    $scope.$watch('columns', function (newValue, oldValue) {
                        if (newValue !== oldValue && oldValue !== undefined) {
                            save();
                        }
                    }, true);
                }


                let check = function () {
                    for (let i = 0; i < $scope.columns.length; i++) {
                        let column = $scope.columns[i];
                        if (!angular.isDefined(column.checked) && !column.default) {
                            column.checked = true;
                        }
                    }
                };

                let get = function () {
                    let keys = getKeys();
                    let id = getId(keys);
                    let storage = localStorage.getItem(id);
                    let array = storage ? JSON.parse(storage) : [];
                    array.forEach(function (a) {
                        $scope.columns.forEach(function (c) {
                            if (c.key === a.key && a.checked !== undefined) {
                                c.checked = a.checked;
                            }
                        });
                    })
                };

                let save = function () {
                    let keys = getKeys();
                    let id = getId(keys);
                    localStorage.setItem(id, JSON.stringify(keys));
                };

                let getKeys = function () {
                    let keys = [];
                    $scope.columns.forEach(function (c) {
                        if (c.key) {
                            keys.push({key: c.key, checked: c.checked});
                        }
                    });
                    return keys;
                };

                let getId = function (keys) {
                    let id = [];
                    keys.forEach(function (k) {
                        id.push(k.key);
                    });
                    return md5(user.id + "-" + id.join(","));
                }
            }
        };
    });

    F2CModule.directive("tableMenus", function (Translator) {
        return {
            replace: true,
            transclude: true,
            templateUrl: "web-public/fit2cloud/html/table/table-menus.html" + '?_t=' + window.appversion,
            scope: {
                op: "=",
                disabled: "=",
                width: "="
            },
            link: function ($scope) {
                $scope.name = $scope.op || Translator.get("i18n_operation");
                $scope.w = $scope.width || 3;
            }
        };
    });

    F2CModule.directive('tablePagination', function () {
        return {
            restrict: 'E',
            templateUrl: "web-public/fit2cloud/html/table/table-pagination.html" + '?_t=' + window.appversion,
            scope: {
                pagination: "="
            },
            link: function ($scope) {
                function isPositive(number) {
                    return parseInt(number, 10) > 0;
                }

                $scope.first = function () {
                    $scope.pagination.page = 1;
                    $scope.onPaginationChange();
                };

                $scope.hasNext = function () {
                    return $scope.pagination.page * $scope.pagination.limit < $scope.pagination.itemCount;
                };

                $scope.hasPrevious = function () {
                    return $scope.pagination.page > 1;
                };

                $scope.last = function () {
                    $scope.pagination.page = $scope.pages();
                    $scope.onPaginationChange();
                };

                $scope.max = function () {
                    return $scope.hasNext() ? $scope.pagination.page * $scope.pagination.limit : $scope.pagination.itemCount;
                };

                $scope.min = function () {
                    return isPositive($scope.pagination.itemCount) ? $scope.pagination.page * $scope.pagination.limit - $scope.pagination.limit + 1 : 0;
                };

                $scope.next = function () {
                    $scope.pagination.page++;
                    $scope.onPaginationChange();
                };

                $scope.onKeyPress = function (event) {
                    if (event && event.type === "keypress" && event.keyCode !== 13) {
                        return;
                    }
                    $scope.onPaginationChange();
                };

                $scope.check = function () {
                    if ($scope.pagination.page < 1 || $scope.pagination.page === undefined) {
                        $scope.pagination.page = 1;
                    }
                    if ($scope.pagination.page > $scope.pages()) {
                        $scope.pagination.page = $scope.pages();
                    }
                };

                $scope.onPaginationChange = function () {
                    if (angular.isFunction($scope.pagination.onPaginate)) {
                        $scope.check();
                        $scope.pagination.onPaginate();
                    }
                };

                $scope.pages = function () {
                    return isPositive($scope.pagination.itemCount) ? Math.ceil($scope.pagination.itemCount / (isPositive($scope.pagination.limit) ? $scope.pagination.limit : 1)) : 1;
                };

                $scope.previous = function () {
                    $scope.pagination.page--;
                    $scope.onPaginationChange();
                };

                $scope.$watch('pagination.limit', function (newValue, oldValue) {
                    if (isNaN(newValue) || isNaN(oldValue) || newValue === oldValue) {
                        return;
                    }

                    $scope.pagination.page = Math.floor((($scope.pagination.page * oldValue - oldValue) + newValue) / (isPositive(newValue) ? newValue : 1));
                    $scope.onPaginationChange();
                });

                $scope.$watch('pagination.itemCount', function (newValue, oldValue) {
                    if (isNaN(newValue) || newValue === oldValue) {
                        return;
                    }

                    if ($scope.pagination.page > $scope.pages()) {
                        $scope.last();
                    }
                });
            }
        };
    });



    F2CModule.directive("treeSelect", function ($compile, HttpUtils) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-select.html" + '?_t=' + window.appversion,
            require: '?^form',
            scope: {
                tsUrl: "@",
                where: "=",
                method : "@",
                changed: "&",
                single: "@",
                selected: "=",
                noCascade: "@",
                name: "@",
                builder: "=",
                required: "@",
                checkCondition:"&"
            },
            link: function ($scope, element, attr, ctrl) {

                $scope.selected = $scope.selected || [];
                $scope.option = {
                    no_cascade: ($scope.noCascade && $scope.noCascade == 'true')
                }
                $scope.values = [];
                $scope.results = [];
                $scope.form = ctrl;
                $scope.$watch("where.rootId", function (value) {
                    if (value !== undefined) {
                        $scope.init();
                    }
                });
                $scope.validate = function(values){
                    let parentDom = element.find("[name='"+$scope.name+"']").parent().parent().parent().parent();
                    if (!parentDom || parentDom.length==0) return;
                    $scope.domClass(parentDom, "md-input-focused", false);
                    if ((!values || values.length==0) ){
                        $scope.domClass(parentDom, "md-input-has-value", false);
                        if ($scope.required && $scope.required == 'true')
                        $scope.domClass(parentDom, "md-input-invalid", true);
                    }else {
                        $scope.domClass(parentDom, "md-input-invalid", false);
                        $scope.domClass(parentDom, "md-input-has-value", true);
                    }
                }

                element.ready(function(e){
                    let index = 0;
                    let interval = window.setTimeout(function(){
                        $scope.disableRemoveNode();
                        index ++;
                        if (index == 5)
                            window.clearTimeout(interval);
                    },500)
                })

                $scope.onValuesAdd = function(chip, index){

                }
                $scope.onValuesRemove = function(chip, index){
                    $scope.results.forEach(tNode => {
                        if (tNode.name == chip){
                            $scope.nodeRemoveResults(tNode);
                            $scope.setNodeStatus(tNode.id, false);
                        }
                    })
                    if (!$scope.values || $scope.values.length == 0){
                        $scope.validate($scope.values);
                    }
                    $scope.values2Selected();
                    if ($scope.changed && $scope.changed()){
                        $scope.changed()($scope.selected);
                    }

                }

                $scope.setNodeStatus = function(id, status){
                    let node = $scope.getNodeById($scope.treeData, id);
                    node.checked = status ? status : undefined;
                }

                $scope.getNodeById = function(nodes,id){
                    let result = null;
                    let hasFound = false;
                    let find = function(arrs) {
                        if (arrs && !hasFound){
                            arrs.forEach(item => {
                                if (item.id == id){
                                    result = item;
                                    hasFound = true;
                                    return true;
                                }else if (item.children && item.children.length > 0){
                                    find(item.children);
                                }
                            })
                        }
                    }
                    find(nodes);
                    return result;
                }

                $scope.selected2Results = function() {
                    let resultNodes = [];
                    $scope.allNodes($scope.treeData, resultNodes);
                    $scope.results =  resultNodes.filter(node => $scope.selected.some(tNode => tNode == node.id));
                }

                $scope.allNodes = function(nodes, resultNodes){
                    nodes.forEach(node => {
                        resultNodes.push(node);
                        if (node.children && node.children.length > 0) {
                            $scope.allNodes(node.children, resultNodes);
                        }
                    })
                }

                $scope.values2Selected = function() {
                    $scope.selected = $scope.results.map(tNode => tNode.id);
                }

                $scope.domClass = function(dom, className, add){
                    if (dom.hasClass(className) && !add){
                        dom.removeClass(className);
                    }
                    if (!dom.hasClass(className) && add){
                        dom.addClass(className);
                    }
                }


                $scope.afterClose = function() {
                    $scope.validate($scope.values);
                    $scope.values2Selected();
                }



                $scope.edit = function(){
                    $scope.form[$scope.name].$setTouched();
                    let parentDom = element.find("[name='"+$scope.name+"']").parent().parent().parent().parent();
                    $scope.domClass(parentDom, "md-input-invalid", false);
                    $scope.domClass(parentDom, "md-input-has-value", false);
                    $scope.domClass(parentDom, "md-input-focused", true);
                }

                $scope.setValues = function(){
                    $scope.values = $scope.results.map( node => node.name);
                }

                $scope.disableChip = function(value){
                    let cDom = element.find("span:contains("+value+")").parent().next();
                    cDom.css("display", "none");
                }

                $scope.nodeInResults = tNode => {
                    return $scope.results.some(cNode => cNode.id == tNode.id);
                }

                $scope.nodeRemoveResults = tNode => {
                    let i = -1;
                    let inResults = $scope.results.some((cNode ,index) => {
                        i = index;
                        return cNode.id == tNode.id;
                    });
                    if (inResults){
                        $scope.results.splice(i,1);
                    }
                }

                $scope.noroot = {
                    onChange: function (node) {
                        $scope.results = $scope.results || [];
                        if (node.checked && !$scope.nodeInResults(node)){
                            if ($scope.single && $scope.single == 'true' && $scope.results.length > 0){
                                $scope.results.forEach(rNode => $scope.unCheckNode(rNode));
                            }
                            $scope.results.push(node);
                        }
                        if (!node.checked && $scope.nodeInResults(node)){
                            $scope.nodeRemoveResults(node);
                        }
                        $scope.setValues();
                        $scope.values2Selected();
                        if ($scope.changed && $scope.changed()){
                            $scope.changed()($scope.selected);
                        }
                    }
                };

                $scope.unCheckNode = function(node){
                    if (!!node.checked){
                        /*node.checked = undefined;*/
                        $scope.noroot.setSelected("id", node.id, undefined);
                    }
                    $scope.nodeRemoveResults(node);
                }

                $scope.removeResultNode = function(node) {
                    $scope.nodeRemoveResults(node);
                };
                $scope.init = function () {
                    $scope.selected = Array.from(new Set($scope.selected));
                    $scope.values = [];
                    $scope.results = [];
                    $scope.loadTreeData();
                }

                $scope.loadTreeData = function(){
                    HttpUtils.post($scope.tsUrl, $scope.where, function (response) {
                        let nodes = response.data;
                        $scope.buildTreeData(nodes);
                        $scope.treeData = nodes;
                        $scope.selected2Results();
                        $scope.setValues();

                        if ($scope.results && $scope.results.length > 0){
                            $scope.validate($scope.values);
                        }
                    }, function (data) {
                        $scope.error = data;
                    })
                }

                $scope.disableRemoveNode = function(){
                    $scope.results.filter(node => node.hiddenBox).forEach(node => {
                        $scope.disableChip(node.name);
                    })
                }
                $scope.buildTreeData = (nodes) => {
                    if (!nodes || nodes.length == 0) return;
                    let index = nodes.length;
                    while (index --){
                        let node = nodes[index];
                        angular.forEach($scope.builder, (value, key) => node[key] = node[value]);
                        if ($scope.selected.some(tNode => tNode == node.id)){
                            node.checked = true
                        }
                        node.collapsed = false;
                        let allChildHidden = true;
                        if (!!$scope.checkCondition && $scope.checkCondition()){
                            let enableCheckBox = $scope.checkCondition()(node);
                            node.disabled = !enableCheckBox;
                            node.hiddenBox = node.disabled;
                            if (node.hiddenBox){
                                let kids = node.children;
                                if (kids && kids.length > 0){
                                    allChildHidden = $scope.allKidsHidden(kids);
                                }
                                if (allChildHidden){
                                    //如果所有子节点都不可选择 那么就隐藏
                                    nodes.splice(index, 1);
                                    continue;
                                }
                            }
                        }
                        $scope.buildTreeData(node.children);
                    }
                }

                $scope.allKidsHidden = function(kids){
                    let result = true;
                    for (let i = 0; i < kids.length; i++) {
                        let kid = angular.copy(kids[i]);
                        angular.forEach($scope.builder, (value, key) => kid[key] = kid[value]);
                        let enable = $scope.checkCondition()(kid);
                        if (enable) return false;
                        let childs = kid.children;
                        if (childs && childs.length > 0){
                            if (!$scope.allKidsHidden(childs)) return false;
                        }
                    }
                    return result;
                }
                $scope.init();
            }
        };
    });

})();


/**
 * 综合搜索框,Filter,Search
 */
(function () {
    let F2CModule = angular.module('f2c.module.filter', []);

    F2CModule.service('FilterSearch', function () {
        this.push = function (results, condition) {
            for (let i = 0; i < results.length; i++) {
                if (results[i].key === condition.key) {
                    results.splice(i, 1);
                    break;
                }
            }
            results.push(condition);
        };

        this.convert = function (results) {
            let condition = {};
            results.forEach(function (f) {
                if (f.complex) {
                    condition[f.key] = f;
                } else {
                    if (f.values) {
                        condition[f.key] = f.values;
                    } else {
                        condition[f.key] = f.value;
                    }
                }
            });
            return condition;
        };
    });

    F2CModule.directive("filterTools", function ($timeout, $mdPanel) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter.html?_t=" + window.appversion,
            scope: {
                execute: "&",
                results: "=",
                conditions: "=",
                autoExecute: "=",
                quickSearch: "@"
            },
            link: function ($scope) {
                $scope.query = function () {
                    $scope.execute();
                };

                $scope.showConditions = function (event) {
                    let position = $mdPanel.newPanelPosition().relativeTo(event.target)
                        .addPanelPosition($mdPanel.xPosition.ALIGN_START, $mdPanel.yPosition.BELOW);

                    $mdPanel.open({
                        attachTo: angular.element(document.body),
                        templateUrl: "web-public/fit2cloud/html/filter/filter-conditions.html?_t=" + window.appversion,
                        position: position,
                        openFrom: event,
                        clickOutsideToClose: true,
                        escapeToClose: true,
                        focusOnOpen: true,
                        locals: {
                            quickSearch: $scope.quickSearch,
                            conditions: $scope.conditions,
                            addCondition: $scope.addCondition,
                            results: $scope.results,
                            autoExecute: $scope.autoExecute,
                            execute: $scope.execute
                        },
                        controller: function ($scope, $timeout, mdPanelRef, FilterSearch, quickSearch, conditions, addCondition, results, autoExecute, execute, Translator) {
                            $scope.quick = "";
                            $scope.quickSearch = quickSearch;
                            $scope.conditions = conditions;
                            $scope.addCondition = addCondition;

                            $scope.addQuickResult = function (value, event) {
                                if (!value || $scope.quickSearch !== 'true') {
                                    return;
                                }
                                if (event && event.type === "keypress" && event.keyCode !== 13 && event.type !== "blur") {
                                    return;
                                }

                                let condition = {
                                    key: "quickSearch",
                                    name: Translator.get("i18n_fuzzy_query"),
                                    operator: Translator.get("i18n_filter_condition_contains"),
                                    label: value,
                                    value: "%" + value + "%"
                                };

                                FilterSearch.push(results, condition);
                                if (autoExecute !== false) {
                                    execute();
                                }
                                $scope.quick = "";
                                mdPanelRef.close();
                            };

                            $scope.add = function (condition) {
                                $scope.addCondition(condition);
                                mdPanelRef.close();
                            };
                        }
                    }).then(function (result) {
                        $timeout(function () {
                            angular.element("#quick_search").focus();
                        });
                        $scope.panelRef = result;
                    });
                };

                $scope.$on('$destroy', function () {
                    if ($scope.panelRef) $scope.panelRef.destroy();
                });

                $scope.addCondition = function (condition) {
                    $scope.condition = angular.copy(condition);
                };

                $scope.removeCondition = function () {
                    $scope.condition = null;
                };

                $scope.removeResult = function (index) {
                    // 保留默认条件
                    if (!$scope.results[index].default) {
                        $scope.results.splice(index, 1);
                        if ($scope.autoExecute !== false) {
                            $scope.execute();
                        }
                    }
                };

                $scope.removeAllResult = function () {
                    let results = [];
                    $scope.results.forEach(function (r) {
                        // 保留默认条件
                        if (r.default) {
                            results.push(r);
                        }
                    });
                    $scope.results.splice(0, $scope.results.length);
                    results.forEach(function (result) {
                        $scope.results.push(result);
                    });
                    $scope.condition = null;
                    $scope.status = null;
                    $timeout(function () {
                        $scope.query();
                    });
                };

                $scope.isShowRemoveAll = function () {
                    for (let i = 0; i < $scope.results.length; i++) {
                        if (!$scope.results[i].default) {
                            return true;
                        }
                    }
                    return false;
                };

                $scope.addConditionComplete = function () {
                    if ($scope.autoExecute !== false) {
                        $scope.execute();
                    }
                };
            }
        };
    });

    F2CModule.directive("filterType", function ($compile) {
        return {
            replace: true,
            scope: false,
            link: function ($scope, element) {
                let html = '<' + $scope.condition.directive + ' results="results" condition="condition" remove="removeCondition()"' +
                    ' complete="addConditionComplete()"></' + $scope.condition.directive + '>';
                element.html(html).show();
                $compile(element.contents())($scope);
            }
        };
    });

    F2CModule.directive("filterPopover", function ($templateCache, $mdPanel, $compile, $document) {
        return {
            restrict: 'A',
            link: function ($scope, element, attr) {
                let template = $templateCache.get(attr["popoverTemplate"]);
                let auto = attr["autoOpen"];
                $compile(template)($scope);

                $scope.open = function () {
                    let position = $mdPanel.newPanelPosition().relativeTo(element)
                        .addPanelPosition($mdPanel.xPosition.ALIGN_START, $mdPanel.yPosition.BELOW);

                    let config = {
                        attachTo: angular.element($document[0].body),
                        controller: function ($scope, mdPanelRef) {
                            $scope.close = function () {
                                mdPanelRef.close();
                            }
                        },
                        scope: $scope,
                        template: template,
                        position: position,
                        clickOutsideToClose: true,
                        escapeToClose: true,
                        focusOnOpen: false,
                        onDomRemoved: $scope.onClose
                    };

                    $mdPanel.open(config).then(function (result) {
                        $scope.popoverRef = result;
                    });
                };

                $scope.onClose = function(){
                    if ($scope.popoverRef.config.scope.afterClose){
                        $scope.popoverRef.config.scope.afterClose();
                    }
                }

                $scope.$on('$destroy', function () {
                    if ($scope.popoverRef) $scope.popoverRef.close();
                });

                if (auto === 'false') return;
                $scope.open();
            }
        };
    });

    F2CModule.directive("filterNumber", function ($timeout, $filter, FilterSearch) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-number.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope, element) {
                $scope.check = function (start, end) {
                    $scope.error = "";
                    if (!start && !end) {
                        $scope.error = $filter('translator')('i18n_filter_condition_number_error_1', '至少输入一个有效数值！');
                        return false;
                    }
                    if (start && end && start >= end) {
                        $scope.error = $filter('translator')('i18n_filter_condition_number_error_2', '起始数值不能大于或等于结束数值！');
                        return false;
                    }
                    return true;
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function (start, end) {
                    if (!$scope.check(start, end)) {
                        return;
                    }
                    let condition;
                    if (start || start >= 0) {
                        condition = angular.copy($scope.condition);
                        condition.oldKey = condition.key;
                        condition.key = condition.key + "Start";
                        condition.operator = $filter('translator')('i18n_filter_condition_from', '从');
                        condition.value = start;
                        condition.label = start;
                        FilterSearch.push($scope.results, condition);
                    }

                    if (end) {
                        condition = angular.copy($scope.condition);
                        condition.oldKey = condition.key;
                        condition.key = condition.key + "End";
                        condition.operator = $filter('translator')('i18n_filter_condition_to', '到');
                        condition.value = end;
                        condition.label = end;
                        FilterSearch.push($scope.results, condition);
                    }
                    $scope.remove();
                    $scope.complete();
                };

                $scope.enter = function () {
                    $scope.readyRemove = true;
                };

                $scope.leave = function () {
                    $scope.readyRemove = false;
                };

                $timeout(function () {
                    element.find("#_input_").click();
                });
            }
        };
    });

    F2CModule.directive("filterInput", function ($timeout, FilterSearch) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-input.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope, element) {
                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.addResult = function (condition, event) {
                    if ($scope.readyRemove) return;

                    if (!condition || !condition.value || $.trim(condition.value) === "") {
                        return;
                    }
                    if (event && event.type === "keypress" && event.keyCode !== 13) {
                        return;
                    }
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };

                $scope.enter = function () {
                    $scope.readyRemove = true;
                };

                $scope.leave = function () {
                    $scope.readyRemove = false;
                };

                $timeout(function () {
                    element.find("#_input_").focus();
                });
            }
        };
    });

    F2CModule.directive("filterContains", function ($timeout, FilterSearch, $filter) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-input.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope, element) {
                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.addResult = function (condition, event) {
                    if ($scope.readyRemove) return;

                    if (!condition || !condition.value || $.trim(condition.value) === "") {
                        return;
                    }
                    if (event && event.type === "keypress" && event.keyCode !== 13) {
                        return;
                    }

                    $scope.condition.operator = $scope.condition.operator || $filter('translator')('i18n_filter_condition_contains', '包含');
                    $scope.condition.label = condition.value;
                    $scope.condition.value = "%" + condition.value + "%";

                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };

                $scope.enter = function () {
                    $scope.readyRemove = true;
                };

                $scope.leave = function () {
                    $scope.readyRemove = false;
                };

                $timeout(function () {
                    element.find("#_input_").focus();
                });
            }
        };
    });

    F2CModule.directive("filterSelectOperators", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-operators.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                $scope.condition.complex = true;

                $scope.operators = [
                    {key: '=', value: '='},
                    {key: '!=', value: '!='},
                    {key: 'IN', value: 'IN'},
                    {key: 'NOT IN', value: 'NOT IN'},
                ];

                $scope.condition.operator = $scope.condition.operator || "IN";

                // 如果设置了URL，则异步加载
                if ($scope.condition.url) {
                    HttpUtils.get($scope.condition.url, function (response) {
                        if ($scope.condition.convert) {
                            $scope.condition.selects = [];
                            response.data.forEach(function (e) {
                                let obj = {
                                    value: e[$scope.condition.convert.value],
                                    label: e[$scope.condition.convert.label]
                                };
                                $scope.condition.selects.push(obj);
                            });
                        } else {
                            $scope.condition.selects = response.data;
                        }
                    }, function (data) {
                        $scope.error = data;
                    });
                }

                // 单选
                $scope.selectResult = function (condition, item) {
                    $scope.getOperator();
                    condition.value = item.value;
                    condition.label = item.label;
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };

                $scope.check = function (item) {
                    item.checked = !item.checked;
                };

                // 多选
                $scope.addResults = function () {
                    $scope.getOperator();
                    let values = [];
                    let labels = [];
                    $scope.condition.selects.forEach(function (item) {
                        if (item.checked) {
                            values.push(item.value);
                            labels.push(item.label);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };

                $scope.getOperator = function () {
                    angular.forEach($scope.operators, function (o) {
                        if ($scope.condition.operator === o.key) {
                            $scope.condition.operatorLabel = o.value;
                        }
                    });
                }
            }
        };
    });

    F2CModule.directive("filterSelect", function (FilterSearch) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.selectResult = function (condition, item) {
                    condition.value = item.value;
                    condition.label = item.label;
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };
            }
        };
    });

    F2CModule.directive("filterSelectVirtual", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-virtual.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                // 如果设置了URL，则异步加载
                if ($scope.condition.url) {
                    HttpUtils.get($scope.condition.url, function (response) {
                        if ($scope.condition.convert) {
                            $scope.condition.selects = [];
                            response.data.forEach(function (e) {
                                let obj = {
                                    value: e[$scope.condition.convert.value],
                                    label: e[$scope.condition.convert.label]
                                };
                                $scope.condition.selects.push(obj);
                            });
                        } else {
                            $scope.condition.selects = response.data;
                        }
                        $scope.getStyle();
                    }, function (data) {
                        $scope.error = data;
                    });
                }

                $scope.getStyle = function () {
                    if ($scope.condition.selects) {
                        if ($scope.condition.selects.length < 10) {
                            $scope.height = ($scope.condition.selects.length * 36) + "px";
                        } else {
                            $scope.height = "360px";
                        }
                    }
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.selectResult = function (condition, item) {
                    condition.value = item.value;
                    condition.label = item.label;
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };

                $scope.getStyle();
            }
        };
    });

    F2CModule.directive("filterSelectAjax", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-ajax.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                HttpUtils.get($scope.condition.url, function (response) {
                    if ($scope.condition.convert) {
                        $scope.condition.selects = [];
                        response.data.forEach(function (e) {
                            let obj = {
                                value: e[$scope.condition.convert.value],
                                label: e[$scope.condition.convert.label]
                            };
                            $scope.condition.selects.push(obj);
                        });
                    } else {
                        $scope.condition.selects = response.data;
                    }
                }, function (data) {
                    $scope.error = data;
                });
                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.selectResult = function (condition, item) {
                    condition.value = item.value;
                    condition.label = item.label;
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };
            }
        };
    });

    F2CModule.directive("filterSelectIcon", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-icon.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                HttpUtils.get($scope.condition.url, function (response) {
                    if ($scope.condition.convert) {
                        $scope.condition.selects = [];
                        response.data.forEach(function (e) {
                            let obj = {
                                value: e[$scope.condition.convert.value],
                                label: e[$scope.condition.convert.label],
                                icon: e[$scope.condition.convert.icon]
                            };
                            $scope.condition.selects.push(obj);
                        });
                    } else {
                        $scope.condition.selects = response.data;
                    }
                }, function (data) {
                    $scope.error = data;
                });
                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.selectResult = function (condition, item) {
                    condition.value = item.value;
                    condition.label = item.label;
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                };
            }
        };
    });

    F2CModule.directive("filterSelectMultiple", function (FilterSearch) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-multiple.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {

                $scope.check = function (item) {
                    item.checked = !item.checked;
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selects.forEach(function (item) {
                        if (item.checked) {
                            values.push(item.value);
                            labels.push(item.label);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };
            }
        };
    });

    F2CModule.directive("filterSelectMultipleAjax", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-multiple.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                let url = $scope.condition.url;
                if($scope.condition.params && $scope.condition.paramKey){
                    let params = "";
                    for(i=0;i<$scope.condition.paramKey.length;i++){
                        let key = $scope.condition.paramKey[i];
                        $scope.condition.params.forEach(function (f) {
                            if (f.key==key) {
                                if((i+1)==$scope.condition.paramKey.length){
                                    params+=key+"="+f.values;
                                }else{
                                    params+=key+"="+f.values+"&";
                                }
                            }
                        });
                    }
                    url=url+"?params="+params;
                }
                HttpUtils.get(url, function (response) {
                    if ($scope.condition.convert) {
                        $scope.condition.selects = [];
                        response.data.forEach(function (e) {
                            let obj = {
                                value: e[$scope.condition.convert.value],
                                label: e[$scope.condition.convert.label]
                            };
                            $scope.condition.selects.push(obj);
                        });
                    } else {
                        $scope.condition.selects = response.data;
                    }
                }, function (data) {
                    $scope.error = data;
                });

                $scope.check = function (item) {
                    item.checked = !item.checked;
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selects.forEach(function (item) {
                        if (item.checked) {
                            values.push(item.value);
                            labels.push(item.label);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };
            }
        };
    });



    F2CModule.directive("filterSelectTree", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-select-tree.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                let convertValue = function (selects) {
                    if (!selects) {
                        return;
                    }
                    return selects.map(function (e) {
                        return {
                            value: e[$scope.condition.convert.value],
                            label: e[$scope.condition.convert.label]
                        };
                    });
                };

                let uncheck = function (selects) {
                    if (!selects) {
                        return;
                    }
                    selects.forEach(function (select) {
                        if (select.children) {
                            uncheck(select.children);
                        }
                        select.checked = false;
                    });
                };

                HttpUtils.get($scope.condition.url, function (response) {
                    if ($scope.condition.convert) {
                        $scope.condition.selects = [];
                        response.data.forEach(function (e) {
                            let obj = {
                                value: e[$scope.condition.convert.value],
                                label: e[$scope.condition.convert.label]
                            };
                            if (e.children) {
                                obj.children = convertValue(e.children);
                            }
                            $scope.condition.selects.push(obj);
                        });
                    } else {
                        $scope.condition.selects = response.data;
                    }
                }, function (data) {
                    $scope.error = data;
                });

                $scope.check = function (item) {
                    if (!$scope.condition.multiple) {
                        uncheck($scope.condition.selects);
                    }
                    item.checked = !item.checked;
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selects.forEach(function (item) {
                        if (item.children) {
                            item.children.forEach(function (c) {
                                if (c.checked) {
                                    values.push(c.value);
                                    labels.push(c.label);
                                }
                            })
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };
            }
        };
    });

    F2CModule.directive("filterDate", function ($timeout, FilterSearch, $filter) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-date.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                $scope.start = "";
                $scope.end = "";
                $scope.startOf = function (timestamp) {
                    return moment(timestamp).startOf("days").valueOf();
                };

                $scope.endOf = function (timestamp) {
                    return moment(timestamp).endOf("days").valueOf();
                };

                $scope.format = function (timestamp) {
                    return moment(timestamp).format("YYYY-MM-DD");
                };

                $scope.check = function (start, end) {
                    $scope.error = "";
                    if (!start && !end) {
                        $scope.error = $filter('translator')('i18n_filter_condition_date_error_1', '至少选择一个日期！');
                        return false;
                    }
                    if (start >= end) {
                        $scope.error = $filter('translator')('i18n_filter_condition_date_error_2', '开始日期不能大于或等于结束日期！');
                        return false;
                    }
                    return true;
                };

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function (start, end) {
                    if (!$scope.check(start, end)) {
                        return;
                    }
                    let unit = 1;
                    if ($scope.condition.directiveUnit && $scope.condition.directiveUnit === "second") {
                        unit = 1000;
                    }

                    let condition;
                    if (start) {
                        start = $scope.startOf(start);
                        condition = angular.copy($scope.condition);
                        condition.oldKey = condition.key;
                        condition.key = condition.key + "Start";
                        condition.operator = $filter('translator')('i18n_filter_condition_from', '从');
                        condition.value = start / unit;
                        condition.label = $scope.format(start);
                        FilterSearch.push($scope.results, condition);
                    }

                    if (end) {
                        end = $scope.endOf(end);
                        condition = angular.copy($scope.condition);
                        condition.oldKey = condition.key;
                        condition.key = condition.key + "End";
                        condition.operator = $filter('translator')('i18n_filter_condition_to', '到');
                        condition.value = end / unit;
                        condition.label = $scope.format(end);
                        FilterSearch.push($scope.results, condition);
                    }
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };
            }
        };
    });

    F2CModule.directive("filterMultistageTree", function (FilterSearch, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-multistage-tree.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                $scope.condition.selecteds = [];
                $scope.noroot = {
                    onChange: function (node) {
                        $scope.onChangeTree(node);
                        //$scope.add();
                    }
                };
                $scope.onChangeTree = function (node) {
                    if (!node.checked){
                        $scope.cancelSelected($scope.noroot.getParent(node), node.id);
                        if ($scope.condition.selecteds.length > 0){
                            $scope.condition.selecteds = $scope.condition.selecteds.filter(selected => node.id != selected.id);
                        }

                    }else{
                        $scope.condition.selecteds.push(node);
                    }
                };

                $scope.cancelSelected = function (node, key) {
                    if (node) {
                        if (angular.isArray(node.children) && node.children.length > 0) {
                            angular.forEach(node.children, child => $scope.cancelSelected(child, key));
                        }
                    }
                };



                HttpUtils.post($scope.condition.url, $scope.condition.param, function (response) {
                    let nodes = response.data;
                    $scope.buildTreeData(nodes);
                    $scope.treeData = nodes;
                }, function (data) {
                    $scope.error = data;
                })

                /*HttpUtils.get($scope.condition.url, function (response) {
                    let nodes = response.data;
                    $scope.buildTreeData(nodes);
                    $scope.treeData = nodes;
                }, function (data) {
                    $scope.error = data;
                });*/

                $scope.buildTreeData = (nodes) => {
                    angular.forEach(nodes ,(node) => {
                        angular.forEach($scope.condition.build, (value, key) => node[key] = node[value]);
                        //$scope.condition.selects.push(node);
                        $scope.buildTreeData(node.children);

                    })
                }

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selecteds.forEach(function (c) {
                        if (c.checked) {
                            values.push(c.id);
                            labels.push(c.name);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };
            }
        };
    });


    F2CModule.directive("filterOrgTree", function (FilterSearch, HttpUtils, UserService) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-org-tree.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                $scope.condition.selecteds = [];
                $scope.condition.convert = {value: "nodeId", label: "nodeName"};
                if ($scope.condition.multiple == null || typeof $scope.condition.multiple == "undefined"){
                    $scope.condition.multiple = true;
                }
                $scope.option = {
                    no_cascade: true
                }
                $scope.noroot = {
                    onChange: function (node) {
                        $scope.onChangeTree(node);
                    }
                };
                $scope.onChangeTree = function (node) {
                    if (!node.checked){
                        if ($scope.condition.selecteds.length > 0){
                            $scope.condition.selecteds = $scope.condition.selecteds.filter(selected => node.id != selected.id);
                        }
                    }else{
                        if (!$scope.condition.multiple){
                            $scope.condition.selecteds.forEach(cnode => cnode.id!==node.id && $scope.noroot.setSelected("id", cnode.id, undefined));
                            $scope.condition.selecteds = [];
                        }
                        $scope.condition.selecteds.push(node);
                    }
                };


                $scope.loadData = function () {
                    let url = "user/orgtreeselect";
                    let param = {excludeWs: true};
                    if (UserService.isOrgAdmin()) param.rootId = UserService.getOrganizationId();
                    HttpUtils.post(url, param, function (response) {
                        let nodes = response.data;
                        $scope.buildTreeData(nodes);
                        $scope.treeData = nodes;
                    }, function (data) {
                        $scope.error = data;
                    })
                }

                $scope.nodeMapping = {
                    name: "nodeName",
                    children: "childNodes",
                    id: "nodeId"
                }
                $scope.buildTreeData = (nodes) => {
                    angular.forEach(nodes ,(node) => {
                        angular.forEach($scope.nodeMapping, (value, key) => node[key] = node[value]);
                        $scope.buildTreeData(node.children);
                    })
                }


                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selecteds.forEach(function (c) {
                        if (c.checked) {
                            values.push(c.id);
                            labels.push(c.name);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };

                $scope.loadData();
            }
        };
    })

    F2CModule.directive("filterWksTree", function (FilterSearch, HttpUtils, UserService) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/filter/filter-wks-tree.html" + '?_t=' + window.appversion,
            scope: {
                results: "=",
                condition: "=",
                remove: "&",
                complete: "&"
            },
            link: function ($scope) {
                $scope.condition.selecteds = [];
                $scope.condition.convert = {value: "nodeId", label: "nodeName"};
                if ($scope.condition.multiple == null || typeof $scope.condition.multiple == "undefined"){
                    $scope.condition.multiple = true;
                }
                $scope.option = {
                    no_cascade: true
                }
                $scope.noroot = {
                    onChange: function (node) {
                        $scope.onChangeTree(node);
                    }
                };
                $scope.onChangeTree = function (node) {
                    if (!node.checked){
                        if ($scope.condition.selecteds.length > 0){
                            $scope.condition.selecteds = $scope.condition.selecteds.filter(selected => node.id != selected.id);
                        }
                    }else{
                        if (!$scope.condition.multiple){
                            $scope.condition.selecteds.forEach(cnode => cnode.id!==node.id && $scope.noroot.setSelected("id", cnode.id, undefined));
                            $scope.condition.selecteds = [];
                        }
                        $scope.condition.selecteds.push(node);
                    }
                };

                $scope.loadData = function () {
                    let url = "user/orgtreeselect";
                    let param = {excludeWs: false};
                    if (UserService.isOrgAdmin()) param.rootId = UserService.getOrganizationId();
                    HttpUtils.post(url, param, function (response) {
                        let nodes = response.data;
                        $scope.buildTreeData(nodes);
                        $scope.treeData = nodes;
                    }, function (data) {
                        $scope.error = data;
                    })
                }

                $scope.enableCheckCondition = function (node){
                    return node.nodeType == 'wks';
                }

                $scope.allKidsHidden = function(kids){
                    let result = true;
                    for (let i = 0; i < kids.length; i++) {
                        let kid = angular.copy(kids[i]);
                        angular.forEach($scope.nodeMapping, (value, key) => kid[key] = kid[value]);
                        let enable = $scope.enableCheckCondition(kid);
                        if (enable) return false;
                        let childs = kid.children;
                        if (childs && childs.length > 0){
                            if (!$scope.allKidsHidden(childs)) return false;
                        }
                    }
                    return result;
                }

                $scope.nodeMapping = {
                    name: "nodeName",
                    children: "childNodes",
                    id: "nodeId"
                }
                $scope.buildTreeData = (nodes) => {
                    if (!nodes || nodes.length == 0) return;
                    let index = nodes.length;
                    while (index --){
                        let node = nodes[index];
                        angular.forEach($scope.nodeMapping, (value, key) => node[key] = node[value]);
                        let allChildHidden = true;
                        let enableCheckBox = $scope.enableCheckCondition(node);
                        node.disabled = !enableCheckBox;
                        node.hiddenBox = node.disabled;
                        if (node.hiddenBox){
                            let kids = node.children;
                            if (kids && kids.length > 0){
                                allChildHidden = $scope.allKidsHidden(kids);
                            }
                            if (allChildHidden){
                                //如果所有子节点都不可选择 那么就隐藏
                                nodes.splice(index, 1);
                            }
                        }

                        $scope.buildTreeData(node.children);
                    }

                }

                // 核心方法，必须有，名字随便，用于添加条件到results
                $scope.add = function () {
                    let values = [];
                    let labels = [];
                    $scope.condition.selecteds.forEach(function (c) {
                        if (c.checked) {
                            values.push(c.id);
                            labels.push(c.name);
                        }
                    });
                    if (values.length === 0) {
                        $scope.remove();
                        return;
                    }
                    $scope.condition.values = values;
                    $scope.condition.label = labels.join(", ");
                    FilterSearch.push($scope.results, $scope.condition);
                    $scope.remove();
                    $scope.complete();
                    $scope.close();
                };

                $scope.loadData();
            }
        };
    })

})();

/**
 * 树,Tree
 */
(function () {
    let F2CModule = angular.module('f2c.module.tree', []);

    F2CModule.directive("tree", function ($timeout) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                api: "=",
                option: "=",
                type: "@",
                width: "@",
                height: "@"
            },
            link: function ($scope) {
                $scope.nodes = [];

                $scope.api.refresh = function () {
                    $timeout(function () {
                        let copy = angular.isDefined($scope.data) ? angular.copy($scope.data) : [];
                        $scope.nodes = angular.isArray(copy) ? copy : [copy];
                    });
                };

                $scope.api.refresh();

                if ($scope.api.manual !== true) {
                    $scope.$watch("data", function () {
                        $scope.api.refresh();
                    }, true);
                }
            }
        };
    });

    F2CModule.directive("treeNode", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "=",
                type: "@"
            },
            link: function ($scope) {
                $scope.option = angular.extend({
                    icon: {
                        collapsed: "keyboard_arrow_right",
                        expand: "keyboard_arrow_down"
                    }
                }, $scope.option);

                $scope.collapsed = angular.isDefined($scope.node.collapsed) ? $scope.node.collapsed : true;

                $scope.toggle = function () {
                    $scope.collapsed = !$scope.collapsed;
                    $scope.node.collapsed = $scope.collapsed;
                };

                $scope.hasChildren = function () {
                    if (angular.isFunction($scope.option.hasChildren)) {
                        return $scope.option.hasChildren($scope.node);
                    }
                    return angular.isArray($scope.node.children) && $scope.node.children.length > 0;
                }
            }
        };
    });

    F2CModule.directive("treeType", function ($compile) {
        return {
            replace: true,
            scope: false,
            link: function ($scope, element) {
                switch ($scope.type) {
                    case "radio":
                        $scope.directive = "tree-node-radio";
                        break;
                    case "checkbox":
                        $scope.directive = "tree-node-checkbox";
                        break;
                    case "file":
                        $scope.directive = "tree-node-file";
                        break;
                    case "text":
                        $scope.directive = "tree-node-text";
                        break;
                    case "link":
                        $scope.directive = "tree-node-link";
                        break;
                    default:
                        $scope.directive = "tree-node-checkbox";
                        break;
                }
                let html = '<' + $scope.directive + ' data="data" node="node" api="api" option="option"></' + $scope.directive + '>';
                element.html(html).show();
                $compile(element.contents())($scope);
            }
        };
    });

    F2CModule.directive("treeNodeCheckbox", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node-checkbox.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "="
            },
            link: function ($scope) {
                $scope.option = angular.extend({
                    label: "name"
                }, $scope.option);

                $scope.hasChildren = function (node) {
                    return angular.isArray(node.children) && node.children.length > 0;
                };

                $scope.isChecked = function (node) {
                    if ($scope.hasChildren(node) && !$scope.option.no_cascade) {
                        for (let i = 0; i < node.children.length; i++) {
                            if (!$scope.isChecked(node.children[i])) {
                                return false;
                            }
                        }
                        node.checked = true;
                    }
                    return node.checked;
                };

                $scope.isIndeterminate = function (node) {
                    if (node.checked && $scope.option.no_cascade) return false;
                    if ($scope.hasChildren(node)) {
                        let checkChild = false;
                        let checkAll = true;
                        for (let i = 0; i < node.children.length; i++) {
                            let child = node.children[i];
                            if (child.checked) {
                                checkChild = true;
                                if ($scope.option.no_cascade) return true;
                            } else {
                                checkAll = false;
                            }
                            if ($scope.isIndeterminate(child)) {
                                return true;
                            }
                        }
                        return checkChild && !checkAll;
                    }
                    return false;
                };

                $scope.toggle = function (node, checked) {
                    node.checked = checked !== undefined ? checked : !node.checked;
                    if ($scope.option.no_cascade){
                        return;
                    }
                    if (!node.checked) {
                        let parent = $scope.getParent($scope.data, node.$$hashKey);
                        if (parent) {
                            parent.checked = false;
                        }
                    }
                    $scope.setChildrenAttr(node, "checked", node.checked);
                };

                $scope.disable = function (node, disabled) {
                    node.disabled = disabled !== undefined ? disabled : true;
                    $scope.setChildrenAttr(node, "disabled", node.disabled);
                };

                $scope.setChildrenAttr = function (node, key, value) {
                    if ($scope.hasChildren(node)) {
                        angular.forEach(node.children, function (child) {
                            child[key] = value;
                            $scope.setChildrenAttr(child, key, value);
                        })
                    }
                };

                $scope.getParent = function (nodes, key) {
                    for (let i = 0; i < nodes.length; i++) {
                        let node = nodes[i];
                        if ($scope.hasChildren(node)) {
                            for (let j = 0; j < node.children.length; j++) {
                                let child = node.children[j];
                                if (child.$$hashKey === key) {
                                    return node;
                                }
                            }
                            let parent = $scope.getParent(node.children, key);
                            if (parent) {
                                return parent;
                            }
                        }
                    }
                };

                $scope.getSelected = function (nodes) {
                    let selected = [];
                    angular.forEach(nodes, function (node) {
                        let selelctNode = $scope.addChildrenSelected(node);
                        if ((node.children && node.children.length > 0) || node.checked) {
                            selected.push(node);
                        } else if (selelctNode) {
                            selected.push(selelctNode);
                        }

                    });
                    return selected;
                };

                $scope.addChildrenSelected = function (node) {
                    let selected;
                    if ($scope.hasChildren(node)) {
                        node.selected = [];
                        angular.forEach(node.children, function (child) {
                            let selected = $scope.addChildrenSelected(child);
                            if (selected) {
                                node.selected.push(selected);
                            }
                        });
                        if (node.selected.length > 0) {
                            node.children = node.selected;
                            delete node.selected;
                            selected = node;
                        } else {
                            delete node.children;
                        }
                    } else {
                        if (node.checked) {
                            selected = node;
                        }
                    }
                    if (selected) {
                        delete selected.collapsed;
                        return selected;
                    }
                };

                $scope.getNode = function (nodes, key, value) {
                    for (let i = 0; i < nodes.length; i++) {
                        let node = nodes[i];
                        if (node[key] === value) {
                            return node;
                        } else {
                            if ($scope.hasChildren(node)) {
                                let child = $scope.getNode(node.children, key, value);
                                if (child) return child;
                            }
                        }
                    }
                };

                $scope.$watch("node.checked", function (value) {
                    if (value !== undefined) {
                        if (angular.isFunction($scope.api.onChange)) {
                            $scope.api.onChange($scope.node);
                        }
                    }
                });

                $scope.api = angular.extend($scope.api, {
                    setSelected: function (key, value, checked) {
                        let node = this.getNode(key, value);
                        node.checked = checked;
                    },
                    getSelected: function () {
                        return $scope.getSelected(angular.copy($scope.data));
                    },
                    getNode: function (key, value) {
                        return $scope.getNode($scope.data, key, value);
                    },
                    toggle: function (node, checked) {
                        $scope.toggle(node, checked);
                    },
                    disable: function (node, checked) {
                        $scope.disable(node, checked);
                    },
                    getParent: function (node) {
                        return $scope.getParent($scope.data, node.$$hashKey);
                    }
                });
            }
        };
    });

    F2CModule.directive("treeNodeRadio", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node-radio.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "="
            },
            link: function ($scope) {
                $scope.option = angular.extend({
                    label: "name",
                    key: "id"
                }, $scope.option);
                $scope.hasChildren = function () {
                    return angular.isArray($scope.node.children) && $scope.node.children.length > 0;
                };

                $scope.show = function () {
                    return $scope.option.radio === 'any' || !$scope.hasChildren();
                }
            }
        };
    });

    F2CModule.directive("treeNodeFile", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node-file.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "="
            },
            link: function ($scope) {
                $scope.selected = false;

                $scope.option = angular.extend({
                    label: "name",
                    key: "id",
                    multiple: false,
                }, $scope.option);

                $scope.hasChildren = function () {
                    return angular.isArray($scope.node.children);
                };

                $scope.select = function (node) {
                    if ($scope.option.select === "file" && !$scope.hasChildren()) {
                        $scope.api.selected = node;
                    }
                    if ($scope.option.select === "folder" && $scope.hasChildren()) {
                        $scope.api.selected = node;
                    }
                    if ($scope.option.select === "all") {
                        $scope.api.selected = node;
                    }
                    if ($scope.api.onChange) {
                        $scope.api.onChange(node);
                    }
                };

                $scope.selected = function () {
                    return $scope.api.selected.$$hashKey === $scope.node.$$hashKey;
                }
            }
        };
    });

    F2CModule.directive("treeNodeLink", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node-link.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "="
            },
            link: function ($scope) {
                $scope.selected = false;
                $scope.option = angular.extend({
                    label: "name",
                    key: "id",
                    multiple: false,
                }, $scope.option);

                $scope.hasChildren = function () {
                    return angular.isArray($scope.node.children);
                };

                $scope.select = function (node) {
                    if ($scope.api.onChange) {
                        $scope.api.onChange(node);
                    }
                    $scope.api.selected = node;
                };
                $scope.selected = function () {
                    return $scope.api.selected.id === $scope.node.id;
                }
            }
        };
    });

    F2CModule.directive("treeNodeText", function () {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/tree/tree-node-text.html" + '?_t=' + window.appversion,
            scope: {
                data: "=",
                node: "=",
                api: "=",
                option: "="
            },
            link: function ($scope) {
                $scope.option = angular.extend({
                    label: "name"
                }, $scope.option);
            }
        };
    });
})();

/**
 * 分步向导,Wizard,Stepper
 */
(function () {
    let F2CModule = angular.module('f2c.module.wizard', []);

    F2CModule.service('WizardService', function (Translator) {
        function addAPI(wizard) {
            wizard.api = {};

            wizard.api.isFirst = function (index) {
                return index === 0;
            };

            wizard.api.isLast = function (index) {
                return index === wizard.steps.length - 1;
            };

            wizard.api.isSelected = function (index) {
                return index <= wizard.selected && index >= 0;
            };

            wizard.api.isCurrent = function (position) {
                if (angular.isNumber(position)) {
                    return position === wizard.current;
                } else {
                    return wizard.api.getIndex(position) === wizard.current;
                }
            };

            wizard.api.next = function () {
                if (!wizard.api.isLast(wizard.current)) {
                    wizard.current++;
                    if (wizard.current >= wizard.selected) {
                        wizard.selected = wizard.current;
                    }
                }
            };

            wizard.api.prev = function () {
                if (!wizard.api.isFirst(wizard.current)) {
                    wizard.current--;
                }
            };

            wizard.api.select = function (index) {
                if (wizard.api.isSelected(index)) {
                    wizard.current = index;
                }
            };

            wizard.api.getStep = function (id) {
                for (let i = 0; i < wizard.steps.length; i++) {
                    let step = wizard.steps[i];
                    if (id === step.id) {
                        return step;
                    }
                }
            };

            wizard.api.getIndex = function (id) {
                for (let i = 0; i < wizard.steps.length; i++) {
                    let step = wizard.steps[i];
                    if (id === step.id) {
                        return i;
                    }
                }
                return -1;
            };

            wizard.api.style = function (position) {
                let index = 0;
                if (angular.isNumber(position)) {
                    index = position;
                } else {
                    index = wizard.api.getIndex(position);
                }
                let style = wizard.setting.style.unselected;
                if (wizard.selected >= index) {
                    style = wizard.setting.style.selected;
                }
                if (wizard.current === index) {
                    style = wizard.setting.style.current;
                }
                return style;
            };
        }

        return {
            init: function (wizard) {
                wizard.current = 0;
                wizard.selected = 0;
                wizard.setting = angular.extend({
                    validate: "async",
                    title: "",
                    subtitle: "",
                    width: "100%",
                    height: "600px",
                    style: {
                        selected: "wizard-selected",
                        current: "wizard-current",
                        unselected: "wizard-unselected"
                    },
                    showButtons: true,
                    submitText: Translator.get("i18n_submit"),
                    nextText: Translator.get("i18n_wizard_next"),
                    prevText: Translator.get("i18n_wizard_previous"),
                    closeText: Translator.get("i18n_cancel"),
                    buttons: []
                }, wizard.setting);
                addAPI(wizard);
                return wizard;
            }
        };
    });

    F2CModule.directive("wizard", function (WizardService) {
        return {
            restrict: 'A',
            replace: true,
            transclude: true,
            templateUrl: "web-public/fit2cloud/html/wizard/wizard.html" + '?_t=' + window.appversion,
            scope: {
                wizard: "="
            },
            controller: function ($scope) {
                this.getWizard = function () {
                    return $scope.wizard;
                };
            },
            link: function ($scope, element) {
                WizardService.init($scope.wizard);
                $scope.size = {
                    width: $scope.wizard.setting.width,
                    height: $scope.wizard.setting.height
                };
                $scope.steps = $scope.wizard.steps;
                $scope.showButtons = $scope.wizard.setting.showButtons;
                $scope.buttons = $scope.wizard.setting.buttons;
                $scope.title = $scope.wizard.setting.title;
                $scope.subtitle = $scope.wizard.setting.subtitle;
                $scope.submitText = $scope.wizard.setting.submitText;
                $scope.nextText = $scope.wizard.setting.nextText;
                $scope.prevText = $scope.wizard.setting.prevText;
                $scope.closeText = $scope.wizard.setting.closeText;
                $scope.operation = null;

                $scope.exec = function (func) {
                    if (angular.isFunction(func)) {
                        return func();
                    }
                    return true;
                };

                // 绑定异步执行的操作方法
                $scope.bind = function (func) {
                    $scope.operation = func;
                };

                // 清除异步执行的操作方法
                $scope.unbind = function () {
                    $scope.operation = null;
                };

                $scope.close = function () {
                    $scope.exec($scope.wizard.close);
                };

                $scope.submit = function () {
                    let step = $scope.steps[$scope.wizard.current];
                    $scope.exec(step.next);
                };

                $scope.slide = function (name) {
                    element.find(".slide").removeClass("slide-left").removeClass("slide-right").addClass(name);
                };

                $scope.slideLeft = function () {
                    $scope.slide("slide-left");
                };

                $scope.slideRight = function () {
                    $scope.slide("slide-right");
                };

                $scope.next = function () {
                    $scope.slideLeft();
                    let step = $scope.steps[$scope.wizard.current];
                    $scope.unbind();
                    if ($scope.exec(step.next)) {
                        $scope.wizard.api.next();
                    }
                    $scope.bind(function () {
                        $scope.wizard.api.next();
                    });
                };

                $scope.prev = function () {
                    $scope.slideRight();
                    let step = $scope.steps[$scope.wizard.current];
                    $scope.unbind();
                    if ($scope.exec(step.prev)) {
                        $scope.wizard.api.prev();
                    }
                    $scope.bind(function () {
                        $scope.wizard.api.prev();
                    });
                };

                $scope.select = function (step) {
                    let index = $scope.wizard.api.getIndex(step.id);
                    if (!$scope.wizard.api.isSelected(index)) {
                        return;
                    }
                    if (index >= $scope.wizard.current) {
                        $scope.slideLeft();
                    } else {
                        $scope.slideRight();
                    }
                    $scope.unbind();
                    let isLastSelected = $scope.wizard.current === $scope.wizard.selected;
                    if (isLastSelected || $scope.exec($scope.steps[$scope.wizard.current].next)) {
                        $scope.wizard.api.select(index);
                    }
                    $scope.bind(function () {
                        $scope.wizard.api.select(index);
                    });
                };

                $scope.disable = function () {
                    let step = $scope.steps[$scope.wizard.current];
                    if (step.disable) {
                        return $scope.exec(step.disable);
                    }
                    return false;
                };

                $scope.isFirst = function () {
                    return $scope.wizard.api.isFirst($scope.wizard.current);
                };

                $scope.isLast = function () {
                    return $scope.wizard.api.isLast($scope.wizard.current);
                };

                $scope.isCurrent = function (index) {
                    return $scope.wizard.api.isCurrent(index);
                };

                $scope.isSelected = function (index) {
                    return index !== $scope.wizard.current && $scope.wizard.api.isSelected(index);
                };

                $scope.isDisabled = function (index) {
                    return index !== $scope.wizard.current && !$scope.wizard.api.isSelected(index);
                };

                $scope.style = function (position) {
                    return $scope.wizard.api.style(position);
                };

                $scope.addAPI = function () {
                    // 异步继续执行接口
                    $scope.wizard.continue = function () {
                        $scope.exec($scope.operation);
                        $scope.unbind();
                    };

                    $scope.wizard.next = $scope.next;
                    $scope.wizard.prev = $scope.prev;

                    $scope.wizard.isFirst = $scope.isFirst;
                    $scope.wizard.isLast = $scope.isLast;
                    $scope.wizard.isSelected = $scope.isSelected;
                    $scope.wizard.submit = $scope.submit;
                };

                $scope.addAPI();
            }
        };
    });

    F2CModule.directive("wizardStep", function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            templateUrl: "web-public/fit2cloud/html/wizard/wizard-step.html" + '?_t=' + window.appversion,
            scope: {
                animate: "@"
            },
            require: '^wizard',
            link: function ($scope, element, attr, parentCtrl) {
                $scope.init = function () {
                    $scope.id = attr["id"];
                    $scope.wizard = parentCtrl.getWizard();
                    $scope.index = $scope.wizard.api.getIndex($scope.id);
                    $scope.step = $scope.wizard.api.getStep($scope.id);
                };

                $scope.show = function () {
                    return $scope.index === $scope.wizard.current;
                };

                $scope.select = function () {
                    if (angular.isFunction($scope.step.select)) {
                        $scope.step.select();
                    }
                };

                $scope.init();
            }
        };
    });
})();

/**
 * 表单,Form,Choose
 */
(function () {
    let F2CModule = angular.module('f2c.module.form', ['ngFileUpload']);

    F2CModule.directive("sideForm", function ($mdSidenav) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/form/side-form.html" + '?_t=' + window.appversion,
            compile: function () {
                return {
                    pre: function preLink($scope, element, attr) {
                        // slide-vertical，slide-horizontal，translate-left，translate-right
                        if (!angular.isDefined($scope.formUrl)) {
                            $scope.formUrl = attr["formUrl"];
                        }

                        if ($scope.sideCloseEvent !== true) {
                            attr["mdDisableCloseEvents"] = true;
                        }

                        $scope._side_form_width = attr["width"];
                        if ($scope._side_form_width) {
                            if ($scope._side_form_width.indexOf("%") < 0 && $scope._side_form_width.indexOf("px") < 0) {
                                $scope._side_form_width = $scope._side_form_width + "px";
                            }

                        } else {
                            $scope._side_form_width = "70%";
                        }

                        $scope.toggleForm = function (callback) {
                            $mdSidenav("_side_form").toggle().then(callback);
                        };
                    }
                };
            },
        };
    });

    F2CModule.directive("infoForm", function ($mdSidenav) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/form/info-form.html" + '?_t=' + window.appversion,
            link: function ($scope, element, attr) {
                $scope._info_show = false;
                if (!angular.isDefined($scope.infoUrl)) {
                    $scope.infoUrl = attr["infoUrl"];
                }

                $scope._info_form_width = attr["width"];
                if ($scope._info_form_width) {
                    if ($scope._info_form_width.indexOf("%") < 0 && $scope._info_form_width.indexOf("px") < 0) {
                        $scope._info_form_width = $scope._info_form_width + "px";
                    }

                } else {
                    $scope._info_form_width = "400px";
                }

                $scope.toggleInfoForm = function (state, width) {
                    if (state) {
                        $mdSidenav("_info_form").open();
                    } else {
                        $mdSidenav("_info_form").close();
                    }
                    if(width){
                        $scope._info_form_width = width
                    }
                    $scope._info_show = state === undefined ? !$scope._info_show : state;
                };

                $scope.isShowInfoForm = function () {
                    return $scope._info_show;
                }
            }
        };
    });

    F2CModule.directive("choose", function ($timeout, $log) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/form/choose.html" + '?_t=' + window.appversion,
            scope: {
                items: "=",
                results: "=selected",
                display: "@",
                key: "@",
                width: "@",
                height: "@",
                type: "@"
            },
            link: function ($scope) {
                if ($scope.items === undefined || !angular.isArray($scope.items)) {
                    $log.error("directive choose required items and items must be an array");
                    return;
                }

                if ($scope.display === undefined || $scope.key === undefined) {
                    $log.error("directive choose required display and key");
                    return;
                }

                if ($scope.results === undefined || !angular.isArray($scope.results)) {
                    $log.error("directive choose required selected and selected must be an array");
                    return;
                }
                let OBJECT_TYPE = "object";

                $scope.init = function () {
                    $scope.selected = [];
                    angular.forEach($scope.results, function (value) {
                        angular.forEach($scope.items, function (item) {
                            if ($scope.isObjectType()) {
                                if (item[$scope.key] === value[$scope.key]) {
                                    item._checked = true;
                                    $scope.selected.push(item);
                                }
                            } else {
                                if (item[$scope.key] === value) {
                                    item._checked = true;
                                    $scope.selected.push(item);
                                }
                            }
                        })
                    });
                };

                $scope.filter = function (search) {
                    if (!search) return $scope.items;
                    let items = [];
                    angular.forEach($scope.items, function (item) {
                        if (item[$scope.display].indexOf(search) >= 0) {
                            items.push(item);
                        }
                    });
                    return items;
                };

                $scope.isObjectType = function () {
                    return $scope.type === OBJECT_TYPE;
                };

                $scope.selectAll = function (checked) {
                    angular.forEach($scope.filter($scope.search), function (item) {
                        $scope.select(item, checked);
                    })
                };

                $scope.select = function (item, checked) {
                    item._checked = checked;
                    if (item._checked) {
                        $scope.push(item);
                    } else {
                        $scope.remove(item);
                    }
                };

                $scope.indexOf = function (item) {
                    if ($scope.isObjectType()) {
                        for (let i = 0; i < $scope.results.length; i++) {
                            if ($scope.results[i][$scope.key] === item[$scope.key]) {
                                return i;
                            }
                        }
                    } else {
                        return $scope.results.indexOf(item[$scope.key]);
                    }
                    return -1;
                };

                $scope.push = function (item) {
                    if ($scope.indexOf(item) < 0) {
                        $scope.selected.push(item);
                        if ($scope.isObjectType()) {
                            let copy = angular.copy(item);
                            delete copy._checked;
                            $scope.results.push(copy);
                        } else {
                            $scope.results.push(item[$scope.key]);
                        }
                    }
                };

                $scope.remove = function (item) {
                    let index = $scope.indexOf(item);
                    if (index > -1) {
                        $scope.results.splice(index, 1);
                        $scope.selected.splice(index, 1);
                    }
                };

                $scope.init();
            }
        };
    });

    F2CModule.directive("choosePaging", function ($timeout, $log, HttpUtils) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/form/choose-paging.html" + '?_t=' + window.appversion,
            scope: {
                url: "@",
                searchKey: "@",
                results: "=selected",
                display: "@",
                key: "@",
                width: "@",
                height: "@",
                type: "@"
            },
            link: function ($scope) {
                if ($scope.display === undefined || $scope.key === undefined) {
                    $log.error("directive choose required display and key");
                    return;
                }

                if ($scope.results === undefined || !angular.isArray($scope.results)) {
                    $log.error("directive choose required selected and selected must be an array");
                    return;
                }
                let OBJECT_TYPE = "object";

                $scope.items = [];
                $scope.pagination = {
                    load: 0,
                    page: 1,
                    limit: 50,
                    getItemAtIndex: function (index) {
                        let current = $scope.pagination.page * $scope.pagination.limit;
                        if (index > current) {
                            if ($scope.pagination.load < index && $scope.pagination.page < $scope.pagination.pageCount) {
                                $scope.pagination.load += $scope.pagination.limit;
                                $scope.pagination.page++;
                                $scope.list();
                            }
                            return null;
                        }
                        return $scope.items[index];
                    },
                    getLength: function () {
                        return $scope.pagination.itemCount > 0 ? $scope.pagination.itemCount : $scope.pagination.limit;
                    }
                };

                $scope.list = function () {
                    let url = $scope.url + "/" + $scope.pagination.page + "/" + $scope.pagination.limit;
                    let param = {};
                    param[$scope.searchKey] = $scope.search;
                    $scope.loadingLayer = HttpUtils.post(url, param, function (response) {
                        $scope.pagination.itemCount = response.data["itemCount"];
                        $scope.pagination.pageCount = response.data['pageCount'];
                        angular.forEach(response.data["listObject"], function (item) {
                            if ($scope.indexOf(item) >= 0) {
                                item._checked = true;
                            }
                            $scope.items.push(item);
                        });
                    });
                };

                $scope.init = function () {
                    $scope.list();
                    $scope.selected = [];
                    angular.forEach($scope.results, function (value) {
                        $scope.selected.push(value);
                    });
                };

                $scope.onKeyPress = function (event) {
                    if (event && event.type === "keypress" && event.keyCode !== 13) {
                        return;
                    }
                    $scope.items = [];
                    $scope.pagination.load = 0;
                    $scope.pagination.page = 1;
                    $scope.list();
                };

                $scope.isObjectType = function () {
                    return $scope.type === OBJECT_TYPE;
                };

                $scope.selectAll = function (checked) {
                    angular.forEach($scope.items, function (item) {
                        $scope.select(item, checked);
                    })
                };

                $scope.select = function (item, checked) {
                    item._checked = checked;
                    if (item._checked) {
                        $scope.push(item);
                    } else {
                        $scope.remove(item);
                    }
                };

                $scope.indexOf = function (item) {
                    if (!item) return -1;
                    if ($scope.isObjectType()) {
                        for (let i = 0; i < $scope.results.length; i++) {
                            if ($scope.results[i][$scope.key] === item[$scope.key]) {
                                return i;
                            }
                        }
                    } else {
                        return $scope.results.indexOf(item[$scope.key]);
                    }
                    return -1;
                };

                $scope.push = function (item) {
                    if ($scope.indexOf(item) < 0) {
                        $scope.selected.push(item);
                        if ($scope.isObjectType()) {
                            let copy = angular.copy(item);
                            delete copy._checked;
                            $scope.results.push(copy);
                        } else {
                            $scope.results.push(item[$scope.key]);
                        }
                    }
                };

                $scope.remove = function (item) {
                    let index = $scope.indexOf(item);
                    if (index > -1) {
                        $scope.results.splice(index, 1);
                        $scope.selected.splice(index, 1);
                    }
                };

                $scope.init();
            }
        };
    });

    F2CModule.directive('mdChooseFile', function () {
        return {
            restrict: 'E',
            template: '<input id="fileInput" ng-model="file" type="file" accept="{{accept}}" class="ng-hide">\n' +
                '          <md-input-container flex class="md-block">\n' +
                '              <input type="text" placeholder="{{placeholder}}" ng-required="{{required}}" ng-model="fileName" disabled>\n' +
                '              <div class="hint">{{hint}}</div>\n' +
                '          </md-input-container>\n' +
                '          <div>\n' +
                '              <md-button id="uploadButton" class="md-primary md-fab md-mini">\n' +
                '                  <md-icon class="material-icons">attach_file</md-icon>\n' +
                '              </md-button>\n' +
                '          </div>',
            scope: {
                placeholder: "@",
                file: "=",
                hint: "@",
                accept: "@",
                required: "@",
                multiple: "@"
            },
            link: function (scope, elem, attrs) {
                let button = elem.find('button');
                let input = angular.element(elem[0].querySelector('input#fileInput'));
                if (attrs.multiple === '' || (attrs.multiple && attrs.multiple !== "false")) {
                    input.attr("multiple", "multiple");
                }

                button.bind('click', function () {
                    input[0].click();
                });

                input.bind('change', function (e) {
                    scope.$apply(function () {
                        let files = e.target.files || [];
                        if (files.length === 0) {
                            scope.fileName = null;
                            return;
                        }
                        if (files.length === 1) {
                            scope.fileName = files[0].name;
                        } else {
                            scope.fileName = files.length + ' files';
                        }
                        scope.file = files;
                    });
                });
            }
        };
    });

    // 在外层加form，否则没有错误提示
    F2CModule.directive('mdFileChips', function (HttpUtils, Upload, $http, Translator) {
        return {
            restrict: 'E',
            require: '?^form',
            templateUrl: "web-public/fit2cloud/html/form/file-chips.html" + '?_t=' + window.appversion,
            scope: {
                placeholder: "@",
                hint: "@",
                accept: "@",
                required: "@",
                maxSize: "@",
                maxFiles: "@",
                businessKey: "@",
                disabled: "=",
                api: "="
            },
            link: function ($scope, elem, attr, ctrl) {
                $scope.form = ctrl;
                $scope.multiple = !!(attr.multiple === '' || (attr.multiple && attr.multiple !== "false"));
                $scope.placeholder = $scope.placeholder || Translator.get("i18n_upload");
                $scope.accept = $scope.accept || "*/*";
                $scope.maxSize = $scope.maxSize || "5MB";
                $scope.maxFiles = $scope.maxFiles || "10";

                $scope.method = angular.extend({
                    upload: function (files) {
                        $scope.loadingLayer = Upload.upload({
                            url: 'file/save/' + $scope.businessKey,
                            data: {
                                files: files
                            },
                            arrayKey: '' // 多文件上传时的坑坑
                        }).then(function () {
                            $scope.listFiles();
                        }, function (response) {
                            if (response.status > 0) {
                                $scope.errorMsg = response.status + ': ' + response.data;
                            }
                        });
                    },
                    download: function (file) {
                        $http({
                            url: "/file/" + file.id,
                            method: "GET",
                            responseType: 'arraybuffer'
                        }).then(function (data) {
                            let blob = new Blob([data.data], {type: "text/plain;charset=utf8"});
                            saveAs(blob, file.name);
                        });
                    },
                    clear: function () {
                        $scope.loadingLayer = HttpUtils.get("/file/delete/key/" + $scope.businessKey, function () {
                            $scope.listFiles();
                        });
                    },
                    remove: function (file) {
                        $scope.loadingLayer = HttpUtils.get("/file/delete/id/" + file.id, function () {
                            $scope.listFiles();
                        });
                    },
                    listFiles: function () {
                        HttpUtils.get("/file/list/" + $scope.businessKey, function (response) {
                            $scope.files = response.data;
                        });
                    }
                }, $scope.api);

                $scope.upload = $scope.method.upload;
                $scope.download = $scope.method.download;
                $scope.clear = $scope.method.clear;
                $scope.remove = $scope.method.remove;
                $scope.listFiles = $scope.method.listFiles;

                $scope.listFiles();
            }
        };
    });

    F2CModule.directive("dateSelect", function (DateSelectService) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/date-select/date-select.html" + '?_t=' + window.appversion,
            scope: {
                select: "=",
                period: "=",
                result: "=",
                disableLabel: "=?",
                execute: "&",
            },
            link: function ($scope) {
                let startOf = function (timestamp) {
                    return moment(timestamp).startOf("days").valueOf();
                };

                let endOf = function (timestamp) {
                    return moment(timestamp).endOf("days").valueOf();
                };

                $scope.setRangeDate = function () {
                    let calculateDate = DateSelectService.calculateDate($scope.period);
                    $scope.result.startDate = moment(calculateDate.start);
                    $scope.result.endDate = moment(calculateDate.end);
                };

                $scope.dateChange = function () {
                    let date = DateSelectService.calculateDate($scope.period);
                    if (date) {
                        $scope.result = $.extend($scope.result, date);
                        $scope.setRangeDate();
                    } else {
                        $scope.result.start = startOf($scope.result.startDate);
                        $scope.result.end = endOf($scope.result.endDate);
                    }
                    $scope.execute();
                };
                $scope.setRangeDate();
            }
        };
    });

    F2CModule.directive("virtualSelectMultiple", function ($filter) {
        return {
            replace: true,
            templateUrl: "web-public/fit2cloud/html/form/virtual-select-multiple.html" + '?_t=' + window.appversion,
            require: '?^form',
            scope: {
                virtualWidth: "@",
                name: "@",
                value: "@",
                label: "@",
                items: "=",
                placeholder: "@",
                results: "=",
                change: "=",
                api: "=",
                required: "@",
                hint: "@"
            },
            link: function ($scope, element, attr, ctrl) {
                $scope.selectedItem = null;
                $scope.searchText = null;
                $scope.selected = [];
                $scope.form = ctrl;

                //初始化results

                if (angular.isUndefined($scope.results) || $scope.results === null) {
                    $scope.results = [];
                }

                let tmp = [];
                // 根据virtual.results初始化selected
                for (let i = 0; i < $scope.results.length; i++) {
                    let result = $scope.results[i];
                    for (let j = 0; j < $scope.items.length; j++) {
                        let item = $scope.items[j];
                        if (item[$scope.value] === result) {
                            $scope.selected.push(item);
                            tmp.push(result);
                            break;
                        }
                    }
                }

                //去掉回显没有意义的数据
                if ($scope.results.length > 0) {
                    $scope.results = tmp;
                }

                $scope.querySearch = function (items, query) {
                    if (!query) return items;
                    let obj = {};
                    obj[$scope.label] = query;
                    return $filter("filter")(items, obj);
                };

                if ($scope.api) {
                    $scope.api.clear = function () {
                        $scope.selected = []
                    };
                }

                $scope.changeFunc = function () {
                    $scope.results = [];
                    angular.forEach($scope.selected, function (item) {
                        $scope.results.push(item[$scope.value]);
                    });
                    if ($scope.change) {
                        $scope.change($scope.results);
                    }
                }
            }
        };
    });

    F2CModule.directive("virtualSelect", function ($filter) {
        return {
            templateUrl: "web-public/fit2cloud/html/form/virtual-select.html" + '?_t=' + window.appversion,
            require: '?^form',
            scope: {
                name: "@",
                value: "@",
                label: "@",
                items: "=",
                placeholder: "@",
                result: "=",
                change: "=",
                required: "@",
                disable: "@"
            },
            link: function ($scope, element, attr, ctrl) {
                $scope.selectedItem = null;
                $scope.searchText = null;
                $scope.selectedItem = null;
                $scope.form = ctrl;

                $scope.disabledShow = !(angular.isUndefined($scope.disable) || $scope.disable === 'false');

                $scope.init = function () {
                    $scope.selectedItem = null;
                    let select = false;
                    // 根据result初始化selected
                    if ($scope.result) {
                        for (let j = 0; j < $scope.items.length; j++) {
                            let item = $scope.items[j];
                            if (item[$scope.value] === $scope.result) {
                                $scope.selectedItem = item;
                                select = true;
                                break;
                            }
                        }
                        if (!select) {
                            //如果回显没有选中
                            $scope.result = null;
                        }
                    }
                };

                $scope.init();
                $scope.querySearch = function (items, query) {
                    if (!query) return items;
                    let obj = {};
                    obj[$scope.label] = query;
                    return $filter("filter")(items, obj);
                };

                $scope.changeFunc = function () {
                    $scope.result = null;

                    if ($scope.selectedItem) {
                        $scope.result = $scope.selectedItem[$scope.value];
                    }
                    if ($scope.change) {
                        $scope.change($scope.result);
                    }
                };

                $scope.$watch("items", function () {
                    $scope.init();
                    $scope.changeFunc();
                });
            }
        };
    });
})();

/**
 * 监控,指标,Metric
 */
(function () {
    let F2CModule = angular.module('f2c.module.metric', []);

    F2CModule.directive('cloudMetric', function (HttpUtils, $timeout, $filter, Translator) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/metric/cloud-metric.html' + '?_t=' + window.appversion,
            scope: {
                request: "=",
                url: "=?",
                showDateType: "=",
                timeType: "@",
                disableInitExecute: "=?"
            },
            link: function ($scope, element) {
                $scope.init = function () {
                    let now = moment().utc().valueOf();
                    let end = moment().endOf("days").valueOf();
                    let hour = 60 * 60 * 1000;
                    let day = 24 * hour;
                    $scope.colors = ['#e91e63', '#00abc0', '#c11859', '#31d8d0', '#10bfff', '#e888ff', '#9eabe4'];
                    $scope.timeTypes = {
                        hour: {
                            startTime: now - hour,
                            endTime: end
                        },
                        six_hour: {
                            startTime: now - 6 * hour,
                            endTime: end
                        },
                        day: {
                            startTime: now - day,
                            endTime: end
                        },
                        seven_day: {
                            startTime: now - 7 * day,
                            endTime: end
                        },
                        thirty_day: {
                            startTime: now - 30 * day,
                            endTime: end
                        }
                    };

                    $scope.metricMap = {
                        SERVER_CPU_USAGE: {name: Translator.get("i18n_SERVER_CPU_USAGE"), show: false},
                        SERVER_CPU_USAGE_IN_MHZ: {name: Translator.get("i18n_SERVER_CPU_USAGE_IN_MHZ"), show: false},
                        SERVER_MEMORY_USAGE: {name: Translator.get("i18n_SERVER_MEMORY_USAGE"), show: false},
                        SERVER_MEMORY_USAGE_IN_MB: {
                            name: Translator.get("i18n_SERVER_MEMORY_USAGE_IN_MB"),
                            show: false
                        },
                        SERVER_MEMORY_ALLOCATED_SIZE: {
                            name: Translator.get("i18n_SERVER_MEMORY_ALLOCATED_SIZE"),
                            show: false
                        },
                        SERVER_DISK_ALLOCATED_SIZE: {
                            name: Translator.get("i18n_SERVER_DISK_ALLOCATED_SIZE"),
                            show: false
                        },
                        SERVER_CPU_ALLOCATED_SIZE: {
                            name: Translator.get("i18n_SERVER_CPU_ALLOCATED_SIZE"),
                            show: false
                        },
                        SERVER_BYTES_RECEIVED_PER_SECOND: {
                            name: Translator.get("i18n_SERVER_BYTES_RECEIVED_PER_SECOND"),
                            show: false
                        },
                        SERVER_BYTES_TRANSMITTED_PER_SECOND: {
                            name: Translator.get("i18n_SERVER_BYTES_TRANSMITTED_PER_SECOND"),
                            show: false
                        },
                        SERVER_DISK_READ_AVERAGE: {name: Translator.get("i18n_SERVER_DISK_READ_AVERAGE"), show: false},
                        SERVER_DISK_WRITE_AVERAGE: {
                            name: Translator.get("i18n_SERVER_DISK_WRITE_AVERAGE"),
                            show: false
                        },
                        SERVER_VIRTUAL_DISK_READ_LATENCY_AVERAGE: {
                            name: Translator.get("i18n_SERVER_VIRTUAL_DISK_READ_LATENCY_AVERAGE"),
                            show: false
                        },
                        SERVER_VIRTUAL_DISK_WRITE_LATENCY_AVERAGE: {
                            name: Translator.get("i18n_SERVER_VIRTUAL_DISK_WRITE_LATENCY_AVERAGE"),
                            show: false
                        },
                        SERVER_DISK_USAGE: {name: Translator.get("i18n_SERVER_DISK_USAGE"), show: false},
                        HOST_CPU_USAGE: {name: Translator.get("i18n_HOST_CPU_USAGE"), show: false},
                        HOST_CPU_USAGE_IN_MHZ: {name: Translator.get("i18n_HOST_CPU_USAGE_IN_MHZ"), show: false},
                        HOST_CPU_TOTAL_IN_MHZ: {name: Translator.get("i18n_HOST_CPU_TOTAL_IN_MHZ"), show: false},
                        HOST_MEMORY_USAGE: {name: Translator.get("i18n_HOST_MEMORY_USAGE"), show: false},
                        HOST_MEMORY_USAGE_IN_MB: {name: Translator.get("i18n_HOST_MEMORY_USAGE_IN_MB"), show: false},
                        HOST_MEMORY_TOTAL_IN_MB: {name: Translator.get("i18n_HOST_MEMORY_TOTAL_IN_MB"), show: false},
                        DISK_IOPS_READ: {name: Translator.get("i18n_DISK_IOPS_READ"), show: false},
                        DISK_IOPS_WRITE: {name: Translator.get("i18n_DISK_IOPS_WRITE"), show: false},
                        DISK_BPS_READ: {name: Translator.get("i18n_DISK_BPS_READ"), show: false},
                        DISK_BPS_WRITE: {name: Translator.get("i18n_DISK_BPS_WRITE"), show: false},
                        PHYSICAL_MACHINE_CPU_USAGE: {
                            name: Translator.get("i18n_PHYSICAL_MACHINE_CPU_USAGE"),
                            show: false
                        },
                        PHYSICAL_MACHINE_MEMORY_USAGE: {
                            name: Translator.get("i18n_PHYSICAL_MACHINE_MEMORY_USAGE"),
                            show: false
                        },
                        PHYSICAL_MACHINE_DISK_USAGE: {
                            name: Translator.get("i18n_PHYSICAL_MACHINE_DISK_USAGE"),
                            show: false
                        },
                        DATASTORE_PROVISIONED: {name: Translator.get("i18n_DATASTORE_PROVISIONED"), show: false},
                        DATASTORE_SPACE_USED: {name: Translator.get("i18n_DATASTORE_SPACE_USED"), show: false},
                        DATASTORE_FREE_SIZE: {name: Translator.get("i18n_DATASTORE_FREE_SIZE"), show: false},
                        DATASTORE_TOTAL_SPACE: {name: Translator.get("i18n_DATASTORE_TOTAL_SPACE"), show: false},
                        SYS_CPU_USAGE: {name: Translator.get("i18n_SYS_CPU_USAGE"), show: false},
                        SYS_MEMORY_USAGE: {name: Translator.get("i18n_SYS_MEMORY_USAGE"), show: false},
                        SYS_DISK_USAGE: {name: Translator.get("i18n_SYS_DISK_USAGE"), show: false}
                    };

                    $scope.timeType = $scope.timeType || 'six_hour';
                    $scope.request.startTime = $scope.timeTypes[$scope.timeType].startTime;
                    $scope.request.endTime = $scope.timeTypes[$scope.timeType].endTime;
                    $scope.request.step = $scope.timeTypes[$scope.timeType].step;
                    $scope.chartCount = 0;
                    $scope.url = $scope.url ? $scope.url : 'server/metric/query';
                    $scope.initEchart();
                    if (!$scope.disableInitExecute) {
                        $scope.request.execute();
                    }
                };

                $scope.initEchart = function () {
                    $scope.echarts = {};
                    for (let metric in $scope.metricMap) {
                        $scope.echarts[metric] = echarts.init(element.find("#" + metric)[0], 'fit2cloud-echarts-theme');
                    }
                };

                $scope.changeTime = function (type) {
                    if (type !== $scope.timeType) {
                        $scope.timeType = type;
                        $scope.request.startTime = $scope.timeTypes[$scope.timeType].startTime;
                        $scope.request.endTime = $scope.timeTypes[$scope.timeType].endTime;
                        $scope.request.step = $scope.timeTypes[$scope.timeType].step;
                        $scope.loadingLayer = $scope.request.execute()
                    }
                };

                $scope.refresh = function () {
                    $scope.loadingLayer = $scope.request.execute()
                };

                $scope.isInteger = function (value) {
                    let regex = /^\d+$/g;
                    return regex.test(value);
                };

                $scope.option = {
                    tooltip: {
                        trigger: 'axis',
                        formatter: function (params) {
                            params = params[0];
                            return moment(params.data[0]).format("MM-DD HH:mm") + '<br />' + params.seriesName + ': ' + params.data[1];
                        },
                        axisPointer: {
                            lineStyle: {
                                type: 'dotted'
                            },
                            animation: false
                        }
                    },
                    title: {
                        textStyle: {
                            fontSize: 14
                        },
                        subtextStyle: {
                            fontSize: 10
                        },
                        itemGap: 0
                    },
                    grid: {x: 20, x2: 60},
                    yAxis: {
                        type: 'value', position: 'right', boundaryGap: [0, '100%'],
                        axisLabel: {
                            formatter: function (value) {
                                return $scope.isInteger(value) ? value : $scope.isInteger(value * 10) ? value.toFixed(1) : value.toFixed(2);
                            }
                        },
                        axisTick: {
                            lineStyle: {
                                opacity: 0
                            }
                        },
                        axisLine: {
                            show: false
                        }
                    },
                    xAxis: {
                        splitLine: {show: false},
                        type: 'time'
                    }
                };

                $scope.showLoading = function (echart) {
                    echart.showLoading({
                            text: '',
                            color: '#2B415A',
                            textColor: '#000',
                            maskColor: 'rgba(255, 255, 255, 0.8)',
                            zlevel: 5
                        }
                    );
                };

                $scope.metrics = [];
                if (!angular.isDefined($scope.request.execute)) {
                    $scope.request.execute = function () {
                        for (metric in $scope.metricMap) {
                            $scope.metricMap[metric].show = false;
                        }
                        $scope.chartCount = 0;
                        $scope.isLoading = true;
                        $scope.request.metricDataQueries.forEach(function (query, index) {
                            let echart = $scope.echarts[query.metric];
                            $scope.showLoading(echart);
                            let request = angular.copy($scope.request);
                            request.metricDataQueries = [query];
                            HttpUtils.post($scope.url, request, function (response) {
                                $scope.isLoading = false;
                                if (response.data.length === 0) {
                                    return;
                                }
                                $scope.chartCount++;
                                let item = response.data[0], i;
                                $scope.metricMap[query.metric].show = true;
                                let option = angular.copy($scope.option), series = [];
                                for (i = 0; i < item.values.length; i++) {
                                    if (angular.isNumber(item.values[i])) {
                                        item.values[i] = parseFloat(item.values[i].toFixed(2));
                                    }
                                }
                                option.title.text = $filter('translator')(item.metric, $scope.metricMap[item.metric].name);
                                option.title.subtext = Translator.get("i18n_unit") + ':' + item.unit;

                                let data = [];
                                for (i = 0; i < item.values.length; i++) {
                                    let dataPoint = [];
                                    dataPoint.push(item.timestamps[i]);
                                    dataPoint.push(item.values[i]);
                                    data.push(dataPoint);
                                }

                                let name = $filter('translator')(item.metric, $scope.metricMap[item.metric].name);
                                series.push({
                                    name: name,
                                    type: 'line',
                                    data: data,
                                    showSymbol: false,
                                    hoverAnimation: false,
                                    itemStyle: {
                                        normal: {
                                            lineStyle: {
                                                color: $scope.colors[index % $scope.colors.length],
                                                width: 2
                                            }
                                        }
                                    }
                                });
                                echart.clear();
                                option.series = series;
                                echart.setOption(option);
                                $timeout(function () {
                                    echart.resize();
                                    echart.hideLoading();
                                });
                            });
                        });
                    };
                }
                $scope.init();
            }
        }
    });

    // cloudMetric指令的不限制指标名称，可自定义promQL的版本
    F2CModule.directive('dynamicCloudMetric', function (HttpUtils, $timeout, $filter, $compile, $cacheFactory, Translator) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/metric/dynamic-cloud-metric.html',
            scope: {
                request: "=",
                url: "=?",
                showDateType: "=",
                timeType: "@",
                disableInitExecute: "=?",
                containerId: "@"
            },
            link: function ($scope, element) {
                $scope.init = function () {
                    let now = moment().utc().valueOf();
                    let end = moment().endOf("days").valueOf();
                    let hour = 60 * 60 * 1000;
                    let day = 24 * hour;
                    $scope.colors = ['#c11859', '#31d8d0', '#e91e63', '#00abc0', '#10bfff', '#e888ff', '#9eabe4'];
                    $scope.timeTypes = {
                        hour: {
                            startTime: now - hour,
                            endTime: end,
                            step: 20,
                        },
                        six_hour: {
                            startTime: now - 6 * hour,
                            endTime: end,
                            step: 20,
                        },
                        day: {
                            startTime: now - day,
                            endTime: end,
                            step: 120,
                        },
                        seven_day: {
                            startTime: now - 7 * day,
                            endTime: end,
                            step: 600,
                        },
                        thirty_day: {
                            startTime: now - 30 * day,
                            endTime: end,
                            step: 3600,
                        }
                    };

                    $scope.timeType = $scope.timeType || 'hour';
                    $scope.request.startTime = $scope.timeTypes[$scope.timeType].startTime;
                    $scope.request.endTime = $scope.timeTypes[$scope.timeType].endTime;
                    $scope.request.step = $scope.timeTypes[$scope.timeType].step;
                    $scope.chartCount = 0;
                    $scope.url = $scope.url ? $scope.url : 'dynamic/metric/query';
                    $scope.initEchart();
                    if (!$scope.disableInitExecute) {
                        $scope.request.execute();
                    }
                };

                $scope.$on("$destroy", function () {
                    let tpl = "web-public/fit2cloud/html/metric/dynamic-cloud-metric.html";
                    $cacheFactory.get('templates').remove(tpl);
                });
                $scope.initEchart = function () {
                    let numberOfCharts = $scope.request.metricDataQueries.length;
                    $scope.echarts = {};
                    for (let i = 0; i < numberOfCharts; i++) {
                        let chartElement = angular.element("<div class='f2c-cloud-metric-chart' id='chart_" + i + "'></div>");
                        $compile(chartElement)($scope);
                        angular.element(chartElement).appendTo(element.find('#' + $scope.containerId)[0]);
                        $scope.echarts["chart_" + i] = echarts.init(chartElement[0], 'fit2cloud-echarts-theme', {devicePixelRatio: 2});
                    }
                };

                $scope.changeTime = function (type) {
                    if (type !== $scope.timeType) {
                        $scope.timeType = type;
                        $scope.request.startTime = $scope.timeTypes[$scope.timeType].startTime;
                        $scope.request.endTime = $scope.timeTypes[$scope.timeType].endTime;
                        $scope.request.step = $scope.timeTypes[$scope.timeType].step;
                        $scope.loadingLayer = $scope.request.execute()
                    }
                };

                $scope.isInteger = function (value) {
                    let regex = /^\d+$/g;
                    return regex.test(value);
                };

                $scope.option = {
                    tooltip: {
                        trigger: 'axis',
                        formatter: function (params) {
                            params = params[0];
                            return moment(params.data[0]).format("MM-DD HH:mm") + '<br />' + params.seriesName + ': ' + params.data[1];
                        },
                        axisPointer: {
                            lineStyle: {
                                type: 'dotted'
                            },
                            animation: false
                        }
                    },
                    title: {
                        textStyle: {
                            fontSize: 14
                        },
                        subtextStyle: {
                            fontSize: 10
                        },
                        itemGap: 0
                    },
                    grid: {x: 20, x2: 60},
                    yAxis: {
                        type: 'value', position: 'right', boundaryGap: [0, '100%'],
                        axisLabel: {
                            formatter: function (value) {
                                return $scope.isInteger(value) ? value : $scope.isInteger(value * 10) ? value.toFixed(1) : value.toFixed(2);
                            }
                        },
                        axisTick: {
                            lineStyle: {
                                opacity: 0
                            }
                        },
                        axisLine: {
                            show: false
                        }
                    },
                    xAxis: {
                        splitLine: {show: false},
                        type: 'time'
                    }
                };

                $scope.refresh = function () {
                    $scope.loadingLayer = $scope.request.execute()
                };
                $scope.showLoading = function (echart) {
                    echart.showLoading({
                            text: '',
                            color: '#2B415A',
                            textColor: '#000',
                            maskColor: 'rgba(255, 255, 255, 0.8)',
                            zlevel: 5
                        }
                    );
                };

                $scope.metrics = [];
                if (!angular.isDefined($scope.request.execute)) {
                    $scope.request.execute = function () {
                        $scope.chartCount = 0;
                        $scope.isLoading = true;
                        $scope.request.metricDataQueries.forEach(function (query, index) {
                            let echart = $scope.echarts["chart_" + query.index];
                            $scope.showLoading(echart);
                            let request = angular.copy($scope.request);
                            if (query.promQLs) {
                                request.metricDataQueries = [];
                                for (let i = 0; i < query.promQLs.length; i++) {
                                    let p = query.promQLs[i];
                                    let q = angular.copy(query);
                                    q.promQL = query.promQLProcessor ? query.promQLProcessor(p.promQL, $scope.request.detail, request.startTime, request.endTime) : p.promQL;
                                    q.seriesName = p.seriesName;
                                    request.metricDataQueries.push(q);
                                }
                            } else {
                                query.promQL = query.promQLProcessor ? query.promQLProcessor(query.promQL, $scope.request.detail, request.startTime, request.endTime) : query.promQL;
                                request.metricDataQueries = [query];
                            }
                            request.step = query.stepFunc ? query.stepFunc(request.startTime, request.endTime, $scope.timeType) : request.step;
                            let option = angular.copy($scope.option);
                            option.title.text = $filter('translator')(query.metricName);
                            option.title.subtext = Translator.get("i18n_unit") + ':' + (query.unit ? query.unit : 'N/A');
                            echart.clear();
                            echart.setOption(option);
                            $timeout(function () {
                                echart.resize();
                            });

                            HttpUtils.post($scope.url, request, function (response) {
                                $scope.isLoading = false;
                                $scope.chartCount++;
                                let series = [];
                                if (response.data.length > 1) {
                                    delete option.tooltip.formatter;
                                }
                                for (let j = 0; j < response.data.length; j++) {
                                    let item = response.data[j], i;
                                    for (i = 0; i < item.values.length; i++) {
                                        if (angular.isNumber(item.values[i])) {
                                            if (query.resultNumberValueProcessor) {
                                                item.values[i] = query.resultNumberValueProcessor(item.values[i]);
                                            } else {
                                                item.values[i] = parseFloat(item.values[i].toFixed(2));
                                            }
                                        }
                                    }

                                    let data = [];
                                    for (i = 0; i < item.values.length; i++) {
                                        let dataPoint = [];
                                        dataPoint.push(item.timestamps[i]);
                                        dataPoint.push(item.values[i]);
                                        data.push(dataPoint);
                                    }

                                    let name = $filter('translator')(item.seriesName ? item.seriesName : query.metricName);
                                    series.push({
                                        name: name,
                                        type: 'line',
                                        data: data,
                                        showSymbol: false,
                                        hoverAnimation: false,
                                        itemStyle: {
                                            normal: {
                                                color: $scope.colors[j % $scope.colors.length]
                                            }
                                        }
                                    });
                                }
                                echart.clear();
                                option.series = series;
                                echart.setOption(option);
                                $timeout(function () {
                                    echart.resize();
                                    echart.hideLoading();
                                });
                            });
                        });
                    };
                }
                $timeout($scope.init);
            },

        }
    });

})();

/**
 * Echarts
 */
(function () {
    let F2CModule = angular.module('f2c.module.chart', []);

    F2CModule.directive('lineChart', function (HttpUtils, Notification, $timeout, Translator) {
        return {
            restrict: 'E',
            templateUrl: function (element, attr) {
                if (attr.dashboard) {
                    return (attr.templateUrl || "web-public/fit2cloud/html/dashboard/dashboard-chart.html") + '?_t=' + window.appversion;
                }
                return "web-public/fit2cloud/html/chart/line-chart.html" + '?_t=' + window.appversion;
            },
            scope: {
                setting: "="
            },
            link: function ($scope, element) {
                let option = {
                    title: {
                        text: $scope.setting.name,
                        x: 'center',
                        align: 'right'
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                            type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
                        },
                        // formatter: '{b0}: {c0}<br />{b1}: {c1}'
                    },
                    legend: {
                        data: [],
                        top: '10%'
                    },
                    toolbox: {
                        feature: {},
                    },
                    grid: {
                        y: 70,
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: [{
                        type: 'category',
                        data: []
                    }],
                    yAxis: [{
                        type: 'value',
                        name: $scope.setting.unit,
                        axisLabel: {}
                    }],
                    series: []
                };
                let type = $scope.setting.type;
                $scope.echart = echarts.init(element.find("#es_chart")[0], 'fit2cloud-echarts-theme');
                $scope.showLoading = function () {
                    $scope.echart.showLoading({
                            text: '',
                            color: '#2B415A',
                            textColor: '#000',
                            maskColor: 'rgba(255, 255, 255, 0.8)',
                            zlevel: 5
                        }
                    );

                };
                if (!angular.isDefined($scope.setting.execute)) {
                    $scope.setting.execute = function () {
                        $timeout(function () {
                            $scope.echart.resize();
                            $scope.showLoading();
                        });
                        $scope.noData = false;
                        option.title.text = $scope.setting.name;
                        return HttpUtils.post($scope.setting.url, $scope.setting.param, function (response) {
                            let legend = [], series = {}, xAxis = [], seriesData = [];
                            let chartData = response.data;
                            if (!chartData || chartData.length === 0) {
                                $scope.noData = true;
                            }
                            $scope.setting.table = chartData;
                            chartData.forEach(function (item) {
                                if ($.inArray(item.xAxis, xAxis) === -1) {
                                    xAxis.push(item.xAxis);
                                }
                                xAxis.sort();
                                let name = item.groupName || Translator.get("i18n_chart_other");
                                if ($.inArray(name, legend) === -1) {
                                    legend.push(name);
                                    series[name] = [];
                                }
                                //
                                series[name].splice(xAxis.indexOf(item.xAxis), 0, [item.xAxis, item.yAxis.toFixed(2)]);
                            });
                            $scope.echart.clear();
                            option.legend.data = legend;
                            option.xAxis[0].data = xAxis;
                            for (let name in series) {
                                let data = series[name];
                                let items = {
                                    name: name,
                                    type: type || 'line',
                                    data: data,
                                    smooth: $scope.setting.smooth,
                                    stack: $scope.setting.stack ? Translator.get("i18n_chart_unknown") : null,
                                    barWidth: $scope.setting.width ? $scope.setting.width : null,
                                    areaStyle: $scope.setting.area ? {normal: {}} : null,//堆叠样式，只有type为line时有效
                                    barMaxWidth: '40',
                                    label: {
                                        normal: {
                                            show: false,
                                            position: 'top'
                                        }
                                    }
                                };
                                seriesData.push(items);
                            }
                            option.series = seriesData;
                            if (chartData.length === 0) {
                                $scope.echart.setOption({
                                    title: {
                                        text: $scope.setting.name,
                                        x: 'center',
                                        align: 'right'
                                    }
                                });
                            } else {
                                $scope.echart.setOption(option);
                            }
                            $timeout(function () {
                                $scope.echart.resize();
                                $scope.echart.hideLoading();
                            });
                        }, function (response) {
                            Notification.danger(response.data ? response.data.message ? response.data.message : response.message : response.message);
                            $timeout(function () {
                                $scope.echart.resize();
                                $scope.echart.hideLoading();
                            });
                        });
                    };
                }
                $scope.loadingLayer = $scope.setting.execute();
            }
        }
    });

    F2CModule.directive('pieChart', function (HttpUtils, Notification, $timeout, Translator) {
        return {
            restrict: 'E',
            templateUrl: function (element, attr) {
                if (attr.dashboard) {
                    return (attr.templateUrl || "web-public/fit2cloud/html/dashboard/dashboard-chart.html") + '?_t=' + window.appversion;
                }
                return "web-public/fit2cloud/html/chart/pie-chart.html" + '?_t=' + window.appversion;
            },
            scope: {
                setting: "=",
            },
            link: function ($scope, element) {


                let option = {
                    title: {
                        text: $scope.setting.name,
                        x: 'center',
                        align: 'right'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c}"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data: []
                    },
                    series: [
                        {
                            name: $scope.setting.name,
                            type: 'pie',
                            radius: '75%',
                            center: ['50%', '60%'],
                            data: [],
                            label: {
                                normal: {
                                    formatter: '  {b|{b}：}{c}  ',
                                    backgroundColor: '#fff',
                                    borderColor: '#aaa',
                                    borderWidth: 1,
                                    borderRadius: 4,
                                    rich: {
                                        b: {
                                            fontSize: 16,
                                            lineHeight: 33
                                        }
                                    }
                                }
                            }
                        }
                    ]
                };

                if ($scope.setting.dashboard) {
                    option.tooltip.formatter = "{b} : {c}";
                    option.series[0] =
                        {
                            label: {
                                normal: {
                                    position: 'outside',
                                    formatter: '{c}'
                                },
                                emphasis: {
                                    show: true,
                                    textStyle: {
                                        fontSize: '12',
                                        fontWeight: 'bold'
                                    }
                                }
                            },
                            type: 'pie',
                            radius: ['35%', '50%'],
                            center: ['50%', '60%'],
                            startAngle: 0,
                            data: [],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }


                }
                $scope.echart = echarts.init(element.find("#es_chart")[0], 'fit2cloud-echarts-theme');
                $scope.echart.on('click', function (params) {
                    if ($scope.setting.click) {
                        $scope.setting.click(params, $scope.setting.seriesNames);
                    }
                });
                $scope.showLoading = function () {
                    $scope.echart.showLoading({
                            text: '',
                            color: '#2B415A',
                            textColor: '#000',
                            maskColor: 'rgba(255, 255, 255, 0.8)',
                            zlevel: 5
                        }
                    );
                };

                $scope.sort = function (series) {
                    for (let i = 1; i < series.length; i++)
                        for (let j = 0; j < series.length - i; j++)
                            if (series[j].value < series[j + 1].value) {
                                let tmp = series[j];
                                series[j] = series[j + 1];
                                series[j + 1] = tmp;
                            }
                };

                $scope.setting.execute = function () {
                    $timeout(function () {
                        $scope.echart.resize();
                        $scope.showLoading();
                    });
                    $scope.noData = false;
                    return HttpUtils.post($scope.setting.url, $scope.setting.param, function (response) {
                        let legend = [], seriesData = [];
                        let pieChartData = response.data;
                        if (!pieChartData || pieChartData.length === 0) {
                            $scope.noData = true;
                        }
                        $scope.setting.table = pieChartData;
                        pieChartData.forEach(function (item) {
                            let name = item.groupName || 'NULL';
                            if ($.inArray(name, legend) === -1) {
                                legend.push(name);
                                seriesData.push({
                                    value: item.yAxis,
                                    name: name
                                });
                            } else {
                                for (let i = 0; i < seriesData.length; i++) {
                                    if (seriesData[i].name === name) {
                                        seriesData[i].value = seriesData[i].value + item.yAxis;
                                    }
                                }
                            }
                        });
                        if (seriesData.length > 2) {
                            $scope.sort(seriesData);
                        }
                        $scope.setting.seriesNames = [];
                        let limit = $scope.setting.limit ? $scope.setting.limit : 10;
                        if (limit < 2) {
                            limit = 2
                        }
                        if (seriesData.length > limit) {
                            let tmp = [];
                            for (let i = 0; i < limit - 1; i++) {
                                tmp[i] = seriesData[i];
                            }
                            tmp[limit - 1] = {name: Translator.get("i18n_chart_other"), value: 0};
                            legend.push(Translator.get("i18n_chart_other"));
                            for (let i = limit - 1; i < seriesData.length; i++) {
                                tmp[limit - 1].value += seriesData[i].value;
                                $scope.setting.seriesNames.push(seriesData[i].name)
                            }
                            seriesData = tmp;
                        }

                        $scope.echart.clear();
                        option.legend.data = legend;
                        option.series[0].data = seriesData;
                        option.series[0].name = $scope.setting.name;
                        option.title.text = $scope.setting.name;
                        $scope.echart.setOption(option);
                        $timeout(function () {
                            $scope.echart.resize();
                            $scope.echart.hideLoading();
                        });
                    }, function (response) {
                        Notification.danger(response.data ? response.data.message ? response.data.message : response.message : response.message);
                        $timeout(function () {
                            $scope.echart.resize();
                            $scope.echart.hideLoading();
                        });
                    });
                };
                if (!$scope.setting.disableAutoExec) {
                    $scope.loadingLayer = $scope.setting.execute();
                }
            }
        }
    });

    F2CModule.directive('chartTable', function (HttpUtils, $timeout, Translator) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/chart/chart-table.html' + '?_t=' + window.appversion,
            scope: {
                table: "=",
                columns: "=",
                api: "=",
            },
            link: function ($scope, element) {
                let timeoutHandler = [];

                $scope.watched = false;
                $scope.initContent = function () {
                    if (!$scope.watched) {
                        $scope.watched = true;
                        $scope.$watch('table', $scope.showData);
                    } else {
                        if ($scope.table) {
                            $scope.showData();
                        }
                    }
                };

                $scope.clearContent = function () {
                    for (let i = 0; i < timeoutHandler.length; i++) {
                        clearTimeout(timeoutHandler[i]);
                    }
                    let tbody = element.find("#chart-table-tbody");
                    tbody.empty();
                };
                $scope.$on("$destroy", $scope.clearContent);
                $scope.refreshContent = function () {
                    $scope.clearContent();
                    // 等待md-tab点击后md-active这个class生效
                    $timeout($scope.initContent, 200);
                };
                $scope.refreshContent();

                $('md-tab-item').click(function () {
                    $scope.refreshContent();
                });

                $scope.showData = function () {
                    if ($scope.table) {
                        let tbody = element.find("#chart-table-tbody");
                        if (!tbody[0].closest('.md-active')) {
                            return;
                        }
                        tbody.empty();
                        for (let i = 0; i < timeoutHandler.length; i++) {
                            clearTimeout(timeoutHandler[i]);
                        }
                        let table = $scope.table;

                        let extracted = function (start, end) {
                            // 不足一页
                            if (end > table.length) {
                                end = table.length;
                            }

                            for (let i = start; i < end; i++) {
                                let item = table[i];
                                let tr = document.createElement("tr");
                                let td1 = document.createElement("td");
                                let td2 = document.createElement("td");
                                let td3 = document.createElement("td");

                                let span1 = document.createElement("span");
                                span1.innerHTML = item.groupName ? item.groupName : Translator.get("i18n_chart_other");
                                td1.appendChild(span1);

                                let span2 = document.createElement("span");
                                span2.innerHTML = item.xAxis;
                                td2.appendChild(span2);

                                let span3 = document.createElement("span");
                                span3.innerHTML = item.yAxis;
                                td3.appendChild(span3);

                                tr.appendChild(td1);
                                tr.appendChild(td2);
                                tr.appendChild(td3);
                                tbody[0].appendChild(tr);
                            }

                            if (end === table.length) {
                                return;
                            }

                            start = end;
                            end = end + 100;
                            timeoutHandler.push(setTimeout(function (start, end) {
                                extracted(start, end);
                            }, 200, start, end));
                        };
                        extracted(0, 100);
                    }
                };

                $scope.export = function (rows) {
                    let filename = moment().format("YYYY-MM-DD HH:mm:ss") + ".xlsx";
                    HttpUtils.download('chart/table/export', {
                        columns: $scope.columns,
                        data: rows
                    }, filename, 'application/octet-stream');
                };
            }
        }
    });

    F2CModule.directive('pieChartTable', function (HttpUtils) {
        return {
            restrict: 'E',
            templateUrl: 'web-public/fit2cloud/html/chart/pie-chart-table.html' + '?_t=' + window.appversion,
            scope: {
                table: "=",
                columns: "=",
            },
            link: function ($scope) {
                $scope.sort = function (data) {
                    for (let i = 1; i < data.length; i++)
                        for (let j = 0; j < data.length - i; j++)
                            if (data[j].yAxis < data[j + 1].yAxis) {
                                let tmp = data[j];
                                data[j] = data[j + 1];
                                data[j + 1] = tmp;
                            }
                    return data;
                };

                $scope.export = function (rows) {
                    let filename = moment().format("YYYY-MM-DD HH:mm:ss") + ".xlsx";
                    HttpUtils.download('chart/table/export', {
                        columns: $scope.columns,
                        data: $scope.sort(angular.copy(rows))
                    }, filename, 'application/octet-stream');
                };
            }
        };
    });
})();

/**
 * 其他, slide，drag-repeat(拖拽)
 */
(function () {
    let F2CModule = angular.module('f2c.module.others', []);

    F2CModule.directive('slide', function () {
        return {
            restrict: 'A',
            scope: {
                slide: "@"
            },
            compile: function () {
                return {
                    pre: function preLink($scope, element) {
                        // slide-vertical，slide-horizontal，translate-left，translate-right
                        $scope.direction = $scope.slide || "slide-vertical";
                        element.addClass("slide").addClass($scope.direction);
                    }
                };
            }
        }
    });

    F2CModule.service("DragDrop", function () {
        let data;

        this.drag = function (element, drag) {
            element.attr("draggable", "true");
            element.addClass("drag-able");
            element.bind("dragstart", function (event) {
                event = event.originalEvent || event;
                event.dataTransfer.setData('text', '');
                event.dataTransfer.effectAllowed = "move";
                if (drag) {
                    data = drag(event);
                }
            });
        };

        this.drop = function (element, drop) {
            element.bind("dragenter", function (event) {
                event = event.originalEvent || event;
                event.preventDefault();
                element.addClass("drag-enter");
            });

            element.bind("dragleave", function (event) {
                event = event.originalEvent || event;
                event.preventDefault();
                element.removeClass("drag-enter");
            });

            element.bind("dragover", function (event) {
                event = event.originalEvent || event;
                event.preventDefault();
                event.dataTransfer.dropEffect = "move";
            });

            element.bind("drop", function (event) {
                element.removeClass("drag-enter");
                if (drop) {
                    drop(data, event);
                }
                data = null;
            });
        };
    });

    // 注意：如果用了md-ink-ripple，会有冲突，把md-ink-ripple放外层，例如<div md-ink-ripple><div drag-repeat=...></div></div>
    F2CModule.directive("dragRepeat", function (DragDrop) {
        return {
            scope: {
                list: "=dragRepeat",
                index: "=",
                dropMethod: "&",
                changeClass: "@",
                mode: "@",
                disabled: "=ngDisabled"
            },
            link: function ($scope, element) {
                DragDrop.drag(element, function () {
                    return $scope.index;
                });

                DragDrop.drop(element, function (index) {
                    let source = index;
                    let target = $scope.index;
                    if ($scope.mode === "swap") {
                        let temp = $scope.list[source];
                        $scope.list[source] = $scope.list[target];
                        $scope.list[target] = temp;
                    } else {
                        if (target > source) {
                            $scope.list.splice(target + 1, 0, angular.copy($scope.list[source]));
                            $scope.list.splice(source, 1);
                        } else {
                            $scope.list.splice(target, 0, angular.copy($scope.list[source]));
                            $scope.list.splice(source + 1, 1);
                        }
                    }
                    $scope.$apply();
                });

                $scope.$watch('disabled', function (v) {
                    if (!v) {
                        element.attr("draggable", "true");
                        element.addClass("drag-able");
                    } else {
                        element.attr("draggable", "false");
                        element.removeClass("drag-able");
                    }
                })
            }
        };
    });

})();

/**
 * 运行时流程
 */
(function () {
    let F2CProcess = angular.module('f2c.module.process', []);

    F2CProcess.directive("processLog", function (HttpUtils) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "web-public/fit2cloud/html/process/process-log.html" + '?_t=' + window.appversion,
            scope: {
                processId: "="
            },
            link: function ($scope) {
                $scope.$watch('processId', function (value) {
                    if (value) {
                        HttpUtils.get("flow/runtime/task/log/" + $scope.processId, function (response) {
                            $scope.logs = response.data;
                            $scope.logs.forEach(function (log) {
                                if (log.taskExecutor) {
                                    log.executors = log.taskExecutor.split(",");
                                    log.executor = (log.executors.length > 1) ? (log.executors[0] + ", ...") : log.executors[0];
                                }
                            })
                        });
                    }
                });
            }
        };
    });

})();