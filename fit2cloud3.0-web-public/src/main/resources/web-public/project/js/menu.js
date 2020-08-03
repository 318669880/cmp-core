/**
 * FIT2CLOUD Menu Frame
 */

let MenuApp = angular.module('MenuApp', ['ngMaterial', 'pascalprecht.translate', 'ngMessages', 'ngSanitize', 'cgBusy', 'TaskApp']);

MenuApp.config(function ($httpProvider, $mdThemingProvider, $mdAriaProvider) {
    $mdAriaProvider.disableWarnings();
    $httpProvider.interceptors.push(['$q', '$injector', function ($q, $injector) {
        return {
            response: function (response) {
                let deferred = $q.defer();
                if (response.headers("Authentication-Status") === "invalid" || (typeof (response.data) === "string" && response.data.indexOf("action=\"") > -1 && response.data.indexOf("<form") > -1 && response.data.indexOf("/form>") > -1)) {
                    // if (response.headers("Authentication-Status") === "invalid") {
                    deferred.reject('invalid session');
                    let Translator = $injector.get('Translator');
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
            },
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
            }
        };
    }]);

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

MenuApp.config(function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'anonymous/i18n/',
        suffix: '.json?_t=' + window.appversion
    });
    $translateProvider.useSanitizeValueStrategy('escape', 'sanitizeParameters');
    $translateProvider.preferredLanguage(window.parent.userLocale);
});

MenuApp.value('cgBusyDefaults', {
    message: '',
    templateUrl: '/web-public/fit2cloud/html/loading/loading.html' + '?_t=' + window.appversion,
    backdrop: true
});

MenuApp.directive('passwordCheck', function () {
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

MenuApp.service('Translator', function ($filter, $translate) {
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
MenuApp.filter('translator', function ($translate) {
    // 翻译过滤器，兼容没有使用国际化的情况
    function translator(input, defaultStr) {
        try {
            let str = $translate.instant(input);
            if (str === input && defaultStr) {
                return defaultStr;
            }
            return str === input ? "" : str;
        } catch (e) {
            return defaultStr ? defaultStr : input;
        }
    }

    if ($translate.statefulFilter()) {
        translator.$stateful = true;
    }

    return translator;
});

MenuApp.service('HttpUtils', function ($http, $log, Notification) {
    this.get = function (url, success, error, config) {
        return $http.get(url, config).then(function (response) {
            if (!response.data) {
                //处理不是ResultHolder的结果
                if (success) {
                    success(response);
                }
            } else if (response.data.success) {
                if (success) {
                    success(response.data);
                }
            } else {
                if (error) {
                    error(response.data);
                } else {
                    Notification.danger(response.data.message);
                }
            }
        }, function (response) {
            if (error) {
                error(response.data);
            } else {
                Notification.danger(response.data.message);
            }
        });
    };

    this.post = function (url, data, success, error, config) {
        return $http.post(url, data, config).then(function (response) {
            if (!response.data) {
                //处理不是ResultHolder的结果
                if (success) {
                    success(response);
                }
            } else if (response.data.success) {
                if (success) {
                    success(response.data);
                }
            } else {
                $log.error(response);
                if (error) {
                    error(response.data);
                } else {
                    Notification.danger(response.data.message);
                }
            }
        }, function (response) {
            $log.error(response);
            if (error) {
                error(response.data);
            } else {
                Notification.danger(response.data.message);
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
});

MenuApp.service("DragDrop", function () {
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

MenuApp.directive("dragRepeat", function (DragDrop) {
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
                $scope.dropMethod();
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

MenuApp.service('Notification', function ($mdToast, $mdDialog, Translator) {

    this.info = function (msg) {
        $mdToast.show($mdToast.simple().textContent(msg).toastClass('notice-menu-success').position('top right').hideDelay(1500));
    };

    this.danger = function (msg) {
        $mdToast.show($mdToast.simple().textContent(msg).toastClass('notice-menu-danger').position('top right').hideDelay(5000));
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
});

MenuApp.service('eyeService', function () {
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

MenuApp.controller('UserInfoController', function ($scope, HttpUtils, Notification, Translator) {

    $scope.loadingLayer = HttpUtils.get("user/info", function (response) {
        $scope.itemUser = response.data;
    });


    $scope.submitUser = function (itemUser) {
        HttpUtils.post("user/current/edit/info", itemUser, function () {
            $scope.closeDialog();
            Notification.info(Translator.get("i18n_menu_update_success"));
            $scope.getUserInfo();
        }, function (rep) {
            Notification.danger(Translator.get("i18n_menu_update_fail") + ":" + rep.message);
        });
    };
});

MenuApp.controller('MenuCtrl', function ($scope, $http, $mdSidenav, $mdDialog, $mdToast, $document, $log, $window, HttpUtils, Notification, $rootScope, eyeService, Translator) {
    $scope.currentSystemLanguage = {};
    window.languageOptions = [
        {
            id: "zh_CN",
            name: "简体中文"
        },
        {
            id: "zh_TW",
            name: "繁體中文"
        },
        {
            id: "en_US",
            name: "English"
        }
    ];
    $scope.languageOptions = window.languageOptions;
    $scope.setCurrentSystemLanguage = function (lang) {
        $scope.languageOptions.forEach(function (language) {
            if (language.id === lang) {
                $scope.currentSystemLanguage = language;
                return;
            }
        });
    };
    $scope.setCurrentSystemLanguage(window.parent.userLocale);
    Translator.setLang(window.parent.userLocale);

    $scope.licenseStatus = true;
    $scope.licenseInfo = function () {
        HttpUtils.get("anonymous/license/validate", function () {

        }, function (response) {
            $scope.licenseStatus = false;
            $scope.licenseMessage = response.message;
        })
    };

    $scope.licenseInfo();

    $scope.help = function () {
        window.open("/documentation/", "_blank");
    };

    $scope.home = function () {
        $rootScope.inbox = false;
        let frame = angular.element("#frame");
        let isReleaseMode = window.IndexConstants["release.model"];
        if (isReleaseMode === 'false') {
            $scope.getJumpLocalhostUrl(frame);

        } else {
            frame.attr("src", "/dashboard/?banner=false");
            sessionStorage.setItem('module', "/dashboard/?banner=false");
            sessionStorage.setItem('url', "");
        }
        $scope.closeMenu();
    };

    $scope.getJumpLocalhostUrl = function (frame) {
        if (angular.isArray($scope.modules) && $scope.modules.length > 0) {
            let module = $scope.modules[0];
            if (angular.isArray(module.menus) && module.menus.length > 0) {
                let menu = module.menus[0];
                if (angular.isArray(menu.children) && menu.children.length > 0) {
                    let child = menu.children[0];
                    frame.attr("src", child.url);
                    sessionStorage.setItem('module', child.url.split("#!")[0]);
                    sessionStorage.setItem('url', child.url.split("#!")[1]);
                } else {
                    frame.attr("src", menu.url);
                    sessionStorage.setItem('module', menu.url.split("#!")[0]);
                    sessionStorage.setItem('url', menu.url.split("#!")[1]);
                }
            }
        }
    };

    $scope.closeMenu = function () {
        $mdSidenav('menu').close();
    };

    $scope.toggleMenu = function () {
        $mdSidenav('menu').toggle();
    };

    $scope.getModules = function () {
        $scope.indexLoadingLayer = HttpUtils.get("module/all", function (response) {
            $scope.modules = response.data.modules;
            $scope.tops = response.data.tops;
            $scope.tops.forEach(function (item) {
                let menu = item.title.split("：");
                item.module = menu[0];
                item.name = menu[1];
            });
            $scope.menuFilter();
            $scope.refresh();
            $scope.convertPermission($scope.modules);
        });
    };

    $scope.calcTopH = function () {
        $scope.ttopsH = Math.ceil($scope.ttops.length / 3) * 40 + 52;
        $scope.ttopsHCss = $scope.ttopsH + 'px';
    };

    $scope.menuFilter = function () {
        if (!$scope.search) {
            $scope.ttops = $scope.tops;
            $scope.calcMenu($scope.modules);
            $scope.calcTopH();
            return;
        }

        let tt = [];

        if (angular.isArray(tt)) {
            angular.forEach($scope.tops, function (menu) {
                if (menu.title.toLowerCase().indexOf($scope.search.toLowerCase()) !== -1) {
                    tt.push(menu);
                }
            })
        }

        $scope.ttops = tt;
        $scope.calcTopH();

        let output = [];
        if (angular.isArray($scope.modules)) {
            angular.forEach($scope.modules, function (module) {
                if (module.name.toLowerCase().indexOf($scope.search.toLowerCase()) !== -1) {
                    output.push(module);
                } else {
                    let m = [];
                    if (angular.isArray(module.menus)) {
                        angular.forEach(module.menus, function (menu) {
                            if (menu.title.toLowerCase().indexOf($scope.search.toLowerCase()) !== -1) {
                                m.push(menu);
                            }
                        })
                    }
                    if (m.length > 0) {
                        let tmodule = angular.copy(module);
                        tmodule.menus = m;
                        output.push(tmodule);
                    }
                }

            })
        }
        $scope.calcMenu(output);
    };

    $scope.calcMenu = function (modules) {
        let left = 0, center = 0, right = 0;
        $scope.tmodules = [];
        if (angular.isArray(modules)) {
            modules.forEach(function (module) {
                let tmodule = module;
                let min = Math.min(left, center, right);
                if (left === min) {
                    left += tmodule.menus.length * 40 + 64;
                    tmodule.location = 0;
                } else if (center === min) {
                    center += tmodule.menus.length * 40 + 64;
                    tmodule.location = 1;
                } else {
                    right += tmodule.menus.length * 40 + 64;
                    tmodule.location = 2;
                }
                $scope.tmodules.push(tmodule);
            });
        }
    };

    $scope.convertPermission = function (modules) {
        $window.modulePermissions = {};
        if (angular.isArray(modules)) {
            angular.forEach(modules, function (module) {
                let permissionMap = {};
                angular.forEach(module.permissions, function (permission) {
                    let key = permission.split(":")[0];
                    let list = permissionMap[key];
                    if (list === undefined) {
                        list = [];
                    }
                    list.push(permission);
                    permissionMap[key] = list;
                });
                modulePermissions[module.id] = permissionMap;
            });
        }
    };

    $scope.view = function (password, eye) {
        eyeService.view("#" + password, "#" + eye);
    };

    $scope.changePwd = function () {
        $mdDialog.show({
            templateUrl: 'web-public/project/html/change-password.html' + '?_t=' + window.appversion,
            parent: angular.element($document[0].body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: false
        });
    };

    $scope.editUserInfo = function () {
        $mdDialog.show({
            templateUrl: 'web-public/project/html/user-info.html' + '?_t=' + window.appversion,
            parent: angular.element($document[0].body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: false
        });
    };

    $scope.getUserRoleList = function () {
        HttpUtils.get("user/source/list", function (response) {
            $scope.userRoleList = [];
            angular.forEach(response.data, function (item) {
                    item.current = item.id === $scope.user.sourceId;
                    if (item.current) {
                        $scope.switchInfo = item.name + " [" + item.desc + "]";
                    }
                    if (!item.parentId) {
                        item.hasChild = false;
                        item.children = [];
                        $scope.userRoleList.push(item);
                    } else {
                        angular.forEach($scope.userRoleList, function (userRole) {
                            if (userRole.id === item.parentId) {
                                userRole.children.push(item);
                                userRole.hasChild = true;
                            }
                        });
                    }
                }
            );


        });
    };

    $scope.select = function (item) {
        if (!item.switchable || item.current) {
            return;
        }

        angular.forEach($scope.userRoleList, function (p) {
            p.selected = item.id === p.id;

            if (angular.isArray(p.children)) {
                angular.forEach(p.children, function (c) {
                    c.selected = item.id === c.id;
                });
            }
        });

        $scope.selectItemId = item.id;
    };

    $scope.switchSHow = function (item) {
        angular.forEach($scope.userRoleList, function (r) {
            if (item.id === r.id) {
                r.show = !item.show;
            }
        });
    };

    $scope.switchRole = function (id) {
        $scope.indexLoadingLayer = HttpUtils.post("user/switch/source/" + id, {}, function () {
            $scope.closeDialog();
            window.location.reload();
        });
    };

    $scope.changeSubmit = function () {
        $scope.switchRole($scope.selectItemId);
    };

    $scope.changeLanguage = function (lang) {
        $scope.indexLoadingLayer = HttpUtils.get("lang/change/" + lang, function () {
            window.location.reload();
        }, function (rep) {
            Notification.danger(rep.message);
        })
    };

    $scope.switch = function () {
        $mdDialog.show({
            templateUrl: 'web-public/project/html/switch-source.html' + '?_t=' + window.appversion,
            parent: angular.element($document[0].body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: false
        });
    };


    $scope.submit = function () {
        HttpUtils.post("user/current/reset/password", $scope.item, function () {
            $scope.closeDialog();
            Notification.info(Translator.get("i18n_menu_update_success"));
        }, function (rep) {
            Notification.danger(Translator.get("i18n_menu_update_fail") + ":" + rep.message);
        });

    };

    $scope.getUserKeyList = function () {
        HttpUtils.get("user/key/info", function (response) {
            $scope.userKeys = response.data;
            angular.forEach($scope.userKeys, function (userKey) {
                userKey.active = userKey.status === 'ACTIVE';
            });
        });
    };

    $scope.openApi = function () {
        $scope.getUserKeyList();
        $mdDialog.show({
            templateUrl: 'web-public/project/html/access-secret.html' + '?_t=' + window.appversion,
            parent: angular.element($document[0].body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: false
        });
    };

    $scope.changeUserKeyStatus = function (item) {

        if (item.active) {
            HttpUtils.post("user/key/active/" + item.id, {}, function () {
                Notification.info(Translator.get("i18n_menu_activate_success"));
                $scope.getUserKeyList();
            }, function (rep) {
                Notification.danger(Translator.get("i18n_menu_activate_fail") + ": " + rep.message);
            });
        } else {
            HttpUtils.post("user/key/disabled/" + item.id, {}, function () {
                Notification.info(Translator.get("i18n_menu_forbidden_success"));
                $scope.getUserKeyList();
            }, function (rep) {
                Notification.danger(Translator.get("i18n_menu_forbidden_fail") + ": " + rep.message);
            });
        }

    };

    $scope.generateUserKey = function () {
        HttpUtils.post("user/key/generate", {}, function () {
            Notification.info(Translator.get("i18n_menu_generate_success"));
            $scope.getUserKeyList();
        }, function (rep) {
            Notification.danger(Translator.get("i18n_menu_generate_fail") + ": " + rep.message);
        });
    };

    $scope.deleteUserKey = function (item) {
        Notification.confirm(Translator.get("i18n_menu_delete_confirm") + item.accessKey + '?', function () {
            HttpUtils.post("user/key/delete/" + item.id, {}, function () {
                Notification.info(Translator.get("i18n_menu_delete_success"));
                $scope.getUserKeyList();
            }, function (rep) {
                Notification.danger(Translator.get("i18n_menu_delete_fail") + ": " + rep.message);
            });
        });
    };

    $scope.getUserInfo = function () {
        return HttpUtils.get("user/current/info", function (response) {
            $scope.user = response.data;
            $window.userInfo = $scope.user;
            $scope.getUserRoleList();
        });
    };

    $scope.closeDialog = function () {
        $mdDialog.cancel();
    };

    $scope.getParams = function () {
        if (location.search.length <= 1) return {};
        let qs = location.search.substr(1),
            args = {},
            items = qs.length ? qs.split("&") : [],
            item = null,
            len = items.length;

        for (let i = 0; i < len; i++) {
            item = items[i].split("=");
            let name = decodeURIComponent(item[0]),
                value = decodeURIComponent(item[1]);
            if (name) {
                args[name] = value;
            }
        }
        return args;
    };

    $scope.isAdditional = function (url) {
        return url.indexOf("http") > -1;
    };

    $scope.setFrameSrc = function (frame, moduleId, url) {
        let selected = false;
        if ($scope.isAdditional(url)) {
            $scope.modules.forEach(function (module) {
                if (module.moduleUrl === url) {
                    frame.attr("src", url);
                    selected = true;
                    return false;
                }
            });
        } else {
            let src = moduleId + "#!" + url;
            $scope.modules.forEach(function (module) {
                if (module.menus) {
                    module.menus.forEach(function (menu) {
                        if (menu.children) {
                            menu.children.forEach(function (child) {
                                if (child.url === src) {
                                    frame.attr("src", src);
                                    selected = true;
                                    return false;
                                }
                            });
                        } else if (menu.url === src) {
                            frame.attr("src", src);
                            selected = true;
                            return false;
                        }
                    });
                } else if (module.moduleUrl === src) {
                    frame.attr("src", src);
                    selected = true;
                    return false;
                }
            });
        }
        if (!selected) {
            $scope.home();
        }
    };

    $scope.getFirstModuleUrl = function (m) {
        let defaultUrl = "";
        $scope.modules.some(function (module) {
            if (module.id === m) {
                if (angular.isArray(module.menus) && module.menus.length > 0) {
                    let menu = module.menus[0];
                    if (angular.isArray(menu.children) && menu.children.length > 0) {
                        defaultUrl = menu.children[0].url.split("#!")[1];
                    } else {
                        defaultUrl = menu.url.split("#!")[1];
                    }
                }
            }
        });
        return defaultUrl;
    };

    $scope.refresh = function () {
        $scope.indexLoadingLayer = $scope.getUserInfo();

        // 上次的记录
        let frame = angular.element("#frame");
        let url, module;


        // 地址栏直接输入
        let params = $scope.getParams();
        if (params && params._module !== undefined) {
            module = params._module + "/?banner=false";
            if (params.url !== undefined && params.url !== '') {
                url = params.url;
            } else {
                url = $scope.getFirstModuleUrl(params._module);
            }

            if (module.indexOf("/") !== 0) {
                module = "/" + module;
            }
            if (url.indexOf("/") !== 0) {
                url = "/" + url;
            }
            sessionStorage.setItem('module', module);
            sessionStorage.setItem('url', url);
        } else {
            //没有传 _module
            module = sessionStorage.getItem('module');
            url = sessionStorage.getItem('url');
        }

        if (module && url) {
            $scope.setFrameSrc(frame, module, url);
        } else {
            $scope.home();
        }

        angular.element("#_content").removeClass("hideContent");
    };

    $scope.open = function (url) {
        let frame = angular.element("#frame");
        frame.attr("src", url);
        sessionStorage.setItem('module', url.split("#!")[0]);
        sessionStorage.setItem('url', url.split("#!")[1]);
        $scope.closeMenu();
    };

    $scope.navigateTo = function (menu) {
        if (menu.menus) {
            return null;
        }

        if (menu.open === 'new') {
            $scope.toggleMenu();
            window.open(menu.url, "_blank");
            return;
        }
        $rootScope.inbox = false;
        let frame = angular.element("#frame");
        frame.attr("src", menu.url);
        sessionStorage.setItem('module', menu.url.split("#!")[0]);
        sessionStorage.setItem('url', menu.url.split("#!")[1]);
        $scope.toggleMenu();
    };

    $scope.inbox = function (value) {
        $rootScope.inbox = value;
        $scope.$apply();
    };

    if ($window.api) {
        $window.api.open = $scope.open;
        $window.api.inbox = $scope.inbox;
    } else {
        $window.api = {
            open: $scope.open,
            inbox: $scope.inbox
        };
    }

    $scope.getModules();


    $scope.menuToTop = function (module, menu) {
        if (menu.top) {
            let m = angular.copy(menu);
            m.module = module.name;
            m.name = menu.title;
            m.title = module.name + "：" + menu.title;
            for (let i = 0; i < $scope.tops.length; i++) {
                if ($scope.tops[i].id === menu.id) {
                    return;
                }
            }
            $scope.tops.push(m);
        } else {
            for (let i = 0; i < $scope.tops.length; i++) {
                if ($scope.tops[i].id === menu.id) {
                    $scope.tops.splice(i, 1);
                    break;
                }
            }
        }

        $scope.menuFilter();
    };

});

MenuApp.component('headerMenu', {
    templateUrl: 'web-public/project/html/header-menu.html' + '?_t=' + window.appversion,
    bindings: {
        module: '<',
        toggleMenu: '&',
        menuToTop: '='
    },
    controller: function ($rootScope, HttpUtils) {
        let ctrl = this;
        let frame = angular.element("#frame");

        ctrl.open = function (module) {
            if (module.open === 'new') {
                window.open(module.moduleUrl, "_blank");
            } else {
                $rootScope.inbox = false;
                sessionStorage.setItem('module', module.id);
                sessionStorage.setItem('url', module.moduleUrl);
                frame.attr("src", module.moduleUrl);
            }
            ctrl.toggleMenu();
        };

        ctrl.top = function (module, menu) {
            let menuPreference = {};
            menuPreference.moduleId = module.id;
            menuPreference.menuId = menu.id;
            menu.top = !menu.top;
            HttpUtils.post("module/menu/preference", menuPreference, function () {
                ctrl.menuToTop(module, menu);
            });
        };

        ctrl.navigateTo = function (menu) {
            if (menu.menus) {
                return null;
            }

            if (menu.open === 'new') {
                ctrl.toggleMenu();
                window.open(menu.url, "_blank");
                return;
            }
            $rootScope.inbox = false;
            frame.attr("src", menu.url);
            sessionStorage.setItem('module', menu.url.split("#!")[0]);
            sessionStorage.setItem('url', menu.url.split("#!")[1]);
            ctrl.toggleMenu();
        };

        ctrl.haveSubMenu = function (menu) {
            return angular.isDefined(menu.menus) && menu.menus.length > 0;
        };
    }
});

/**
 * 待办，通知
 */
(function () {
    let TaskApp = angular.module('TaskApp', []);

    TaskApp.filter('translator', function ($filter) {
        // 翻译过滤器，兼容没有使用国际化的情况
        return function (input, args) {
            try {
                return $filter('translate')(input);
            } catch (e) {
                return args;
            }
        }
    });

    TaskApp.filter("selects", function () {
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

    TaskApp.directive('pagination', function () {
        return {
            restrict: 'E',
            templateUrl: "/web-public/project/html/pagination.html" + '?_t=' + window.appversion,
            scope: {
                pagination: "="
            },
            link: function ($scope) {
                function isPositive(number) {
                    return parseInt(number, 10) > 0;
                }

                $scope.hasNext = function () {
                    return $scope.pagination.page * $scope.pagination.limit < $scope.pagination.itemCount;
                };

                $scope.hasPrevious = function () {
                    return $scope.pagination.page > 1;
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
                    if ($scope.pagination.page < 1) {
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

    TaskApp.component('taskList', {
        templateUrl: "web-public/project/html/task-list.html" + '?_t=' + window.appversion,
        bindings: {},
        controller: function ($scope, $window, $rootScope, HttpUtils, Translator) {
            $scope.type = "pending";
            $scope.frame = angular.element("#frame");
            Translator.wait(function () {
                $scope.status = [
                    {key: "PENDING", name: Translator.get("i18n_task_wait")},
                    {key: "COMPLETED", name: Translator.get("i18n_process_complete")},
                    {key: "TERMINATED", name: Translator.get("i18n_process_terminal")},
                    {key: "CANCEL", name: Translator.get("i18n_process_cancel")}
                ];
            });

            $scope.pagination = {
                page: 1,
                limit: 50
            };

            $scope.search = null;

            $scope.clear = function () {
                $scope.search = null;
                $scope.list();
            };

            $scope.change = function () {
                $scope.pagination.page = 1;
                $scope.list();
            };

            $scope.list = function () {
                HttpUtils.paging($scope, "flow/runtime/task/" + $scope.type, {search: $scope.search});
            };

            $scope.close = function () {
                $scope.frame.attr("src", "");
            };

            $scope.reload = function () {
                $scope.list();
                $scope.frame.attr("src", "");
            };

            if ($window.api) {
                $window.api.tasks = $scope.reload;
            } else {
                $window.api = {
                    tasks: $scope.reload
                };
            }

            $scope.onKeyPress = function (event) {
                if (event && event.type === "keypress" && event.keyCode !== 13) {
                    return;
                }
                $scope.list();
            };

            $scope.open = function (task) {
                $rootScope.selected = task.taskId;
                let url = task.taskFormUrl;
                if (url === "TEMPLATE" || !url) {
                    let isReleaseMode = window.IndexConstants["release.model"];
                    if (isReleaseMode === 'false') {
                        url = "/?banner=false&task=true&taskId={taskId}&businessKey={businessKey}&_t=" + window.appversion;
                    } else {
                        url = "/" + task.module + "/?banner=false&task=true&taskId={taskId}&businessKey={businessKey}&_t=" + window.appversion;
                    }
                    url = url.replace("{taskId}", task.taskId).replace("{businessKey}", task.businessKey);
                }
                $scope.frame.attr("src", url);
            };

            $scope.list();
        }
    });

    TaskApp.component('taskMenus', {
        templateUrl: "web-public/project/html/task-menus.html" + '?_t=' + window.appversion,
        bindings: {},
        controller: function ($scope, $window, $rootScope, $interval, HttpUtils) {
            $scope.frame = angular.element("#frame");

            $scope.pagination = {
                page: 1,
                limit: 50
            };

            $scope.search = null;

            $scope.list = function () {
                HttpUtils.paging($scope, "flow/runtime/task/pending", {}, null, function () {
                    $interval.cancel($scope.timer);
                });
            };

            $scope.open = function (task) {
                $scope.show();
                $rootScope.selected = task.taskId;
                let url = task.taskFormUrl;
                if (url === "TEMPLATE" || !url) {
                    let isReleaseMode = window.IndexConstants["release.model"];
                    if (isReleaseMode === 'false') {
                        url = "/?banner=false&task=true&taskId={taskId}&businessKey={businessKey}";
                    } else {
                        url = "/" + task.module + "/?banner=false&task=true&taskId={taskId}&businessKey={businessKey}";
                    }
                    url = url.replace("{taskId}", task.taskId).replace("{businessKey}", task.businessKey);
                }
                $scope.frame.attr("src", url);
            };

            $scope.show = function () {
                $window.api.tasks();
                $scope.frame.attr("src", "");
                $rootScope.inbox = true;
            };

            $scope.timer = $interval($scope.list, 30000);

            $scope.$on("$destroy", function () {
                if ($scope.timer) {
                    $interval.cancel($scope.timer);
                }
            });

            $scope.list();
        }
    });

    TaskApp.component('notificationMenus', {
        templateUrl: "web-public/project/html/notification-menus.html" + '?_t=' + window.appversion,
        bindings: {},
        controller: function ($scope, $window, $rootScope, $interval, HttpUtils) {
            $scope.frame = angular.element("#frame");

            $scope.pagination = {
                page: 1,
                limit: 50
            };

            $scope.list = function () {
                HttpUtils.paging($scope, "notification/list/unread", {}, null, function () {
                    $interval.cancel($scope.timer);
                });
            };

            $scope.open = function (notification) {
                $rootScope.inbox = false;
                let url = "notification-index.html?id=" + notification.id;
                $scope.frame.attr("src", url);
            };

            $scope.show = function () {
                $rootScope.inbox = false;
                let url = "notification-index.html";
                $scope.frame.attr("src", url);
            };

            $scope.timer = $interval($scope.list, 30000);

            $scope.$on("$destroy", function () {
                if ($scope.timer) {
                    $interval.cancel($scope.timer);
                }
            });

            $scope.list();
        }
    });

    TaskApp.controller('NotificationListCtrl', function ($scope, $window, $timeout, $rootScope, $sce, HttpUtils) {
        $scope.type = "all";

        $scope.pagination = {
            page: 1,
            limit: 50
        };

        $scope.search = null;

        $scope.clear = function () {
            $scope.search = null;
            $scope.list();
        };

        $scope.change = function () {
            $scope.pagination.page = 1;
            $scope.list();
        };

        $scope.list = function () {
            HttpUtils.paging($scope, "notification/list/" + $scope.type, {title: $scope.search});
        };

        $scope.getParams = function () {
            if (location.search.length <= 1) return {};
            let qs = location.search.substr(1),
                args = {},
                items = qs.length ? qs.split("&") : [],
                item = null,
                len = items.length;

            for (let i = 0; i < len; i++) {
                item = items[i].split("=");
                let name = decodeURIComponent(item[0]),
                    value = decodeURIComponent(item[1]);
                if (name) {
                    args[name] = value;
                }
            }
            return args;
        };

        $scope.onKeyPress = function (event) {
            if (event && event.type === "keypress" && event.keyCode !== 13) {
                return;
            }
            $scope.list();
        };

        $scope.open = function (id) {
            $scope.id = id;
            $scope.loadingGet = HttpUtils.get("notification/get/" + $scope.id, function (response) {
                $scope.notification = response.data;
                $scope.notification.html = $sce.trustAsHtml($scope.notification.content);
                if ($scope.notification.status === "UNREAD") {
                    HttpUtils.get("notification/read/" + $scope.id, function () {
                        $scope.list();
                    });
                }
            });
        };

        $scope.readAll = function () {
            $scope.loadingLayer = HttpUtils.get("notification/read/all", function () {
                $scope.count();
                $scope.list();
            });
        };

        $scope.count = function () {
            $scope.loadingCount = HttpUtils.post("notification/count", {status: "UNREAD"}, function (response) {
                $scope.unread = response.data;
            });

            $scope.loadingCount = HttpUtils.post("notification/count", {status: "READ"}, function (response) {
                $scope.read = response.data;
            });
        };

        $scope.close = function () {
            $scope.notification = null;
        };

        $scope.params = $scope.getParams();
        if ($scope.params.id) {
            $scope.id = $scope.params.id;
            $scope.open($scope.id);
        }

        $scope.count();
        $scope.list();
    });

})();
