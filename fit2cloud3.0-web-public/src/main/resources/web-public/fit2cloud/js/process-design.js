/**
 * FIT2CLOUD 简易流程模块,需要使用f2c.common
 */

(function () {

    let F2CProcess = angular.module('f2c.process', ['f2c.common']);

    /**
     * 流程专用路由
     */
    F2CProcess.run(function (MenuRouter, Translator) {
        let PROCESS_MENUS = [
            {
                title: Translator.get("i18n_process_model"),
                name: "flow_model",
                url: "/flow/model",
                params: {
                    modelId: "",
                    parent: "",
                    type: ""
                },
                templateUrl: "web-public/fit2cloud/html/process/model-edit.html" + '?_t=' + window.appversion
            }, {
                title: Translator.get("i18n_process_role"),
                name: "flow_role",
                url: "/flow/role",
                params: {
                    role: null,
                    parent: "",
                    type: ""
                },
                templateUrl: "web-public/fit2cloud/html/process/role-edit.html" + '?_t=' + window.appversion
            }, {
                title: Translator.get("i18n_process_link"),
                name: "link_values",
                url: "/flow/link/values",
                params: {
                    link: null,
                    parent: "",
                    type: ""
                },
                templateUrl: "web-public/fit2cloud/html/process/link-value.html" + '?_t=' + window.appversion
            }
        ];
        MenuRouter.addStates(PROCESS_MENUS);
    });

    F2CProcess.controller('ManagementCtrl', function ($scope, $state, ModelManagement, RoleManagement, LinkManagement) {
        $scope.model = "web-public/fit2cloud/html/process/model-list.html" + '?_t=' + window.appversion;
        $scope.role = "web-public/fit2cloud/html/process/role-list.html" + '?_t=' + window.appversion;
        $scope.link = "web-public/fit2cloud/html/process/link-list.html" + '?_t=' + window.appversion;
        $scope.selected = sessionStorage.getItem("flow_tab") || "model";

        $scope.addModel = function () {
            ModelManagement.add();
        };

        $scope.addRole = function () {
            RoleManagement.add();
        };

        $scope.addLink = function () {
            LinkManagement.init($scope);
            LinkManagement.add();
        };

        $scope.$on("type",function (event , data) {
            $scope.type = data;
        });

        $scope.$on("linkItem",function (event , data) {
            $scope.linkItem = data;
        });

        $scope.$on("formUrl",function (event , data) {
            $scope.formUrl = data;
            $scope.toggleForm();
        });

        $scope.submit  =function(){
            LinkManagement.init($scope);
            LinkManagement.submit();
        };

        $scope.select = function (tab) {
            $scope.selected = tab;
            sessionStorage.setItem("flow_tab", tab);
        }
    });

    F2CProcess.service('ModelManagement', function ($state, HttpUtils, Notification, Translator) {
        let $scope;

        this.init = function (scope) {
            $scope = scope
        };

        this.add = function () {
            sessionStorage.setItem("parent", $state.current.name);
            $state.go("flow_model", {modelId: "", parent: $state.current.name, type: "add"});
        };

        this.edit = function (modelId) {
            sessionStorage.setItem("parent", $state.current.name);
            $state.go("flow_model", {modelId: modelId, parent: $state.current.name, type: "update"});
        };

        this.copy = function (oldId, newId, callback) {
            return HttpUtils.get('flow/design/model/copy/' + oldId + "/" + newId, function () {
                if (callback) callback();
                Notification.success(Translator.get("i18n_process_copy_success"));
            }, function (response) {
                Notification.danger(Translator.get("i18n_process_copy_fail") + "，" + response.message);
            });
        };

        this.delete = function (item) {
            let self = this;
            return HttpUtils.get('flow/design/model/delete/' + item.modelId, function () {
                self.list($scope);
                Notification.success(Translator.get("i18n_menu_delete_success"));
            }, function (response) {
                Notification.danger(Translator.get("i18n_menu_delete_fail") + "，" + response.message);
            });
        };

        this.publish = function (item) {
            let self = this;
            return HttpUtils.get('flow/design/model/publish/' + item.modelId, function () {
                self.list($scope);
                Notification.success(Translator.get("i18n_process_publish_success"));
            }, function (response) {
                Notification.danger(Translator.get("i18n_process_publish_fail") + "，" + response.message);
            });
        };

        this.list = function () {
            HttpUtils.paging($scope, "flow/design/model/list");
        };
    });

    F2CProcess.service('RoleManagement', function ($state, HttpUtils, Notification, Translator) {
        let $scope;

        this.init = function (scope) {
            $scope = scope;
        };

        this.add = function () {
            sessionStorage.setItem("parent", $state.current.name);
            $state.go("flow_role", {role: {}, parent: $state.current.name, type: "add"});
        };

        this.edit = function (item) {
            sessionStorage.setItem("parent", $state.current.name);
            $state.go("flow_role", {role: item, parent: $state.current.name, type: "update"});
        };

        this.delete = function (item) {
            let self = this;
            var desc ;
            if(item.used){
                desc = Translator.get("i18n_process_role_used");
                if(item.models){
                    desc = desc + Translator.get("i18n_process_model") + ": " + item.models + ", "
                }
                if(item.linkKeys){
                    desc = desc + Translator.get("i18n_process_model_link_predefine") + ": " + item.linkKeys + ", "
                }
                desc = desc + Translator.get("i18n_delete_after_reconfig");
                Notification.danger(desc);
            }else {
                desc = Translator.get("i18n_confirm_perform_delete");
                Notification.confirm(desc, function () {
                    HttpUtils.get('flow/design/role/delete?key=' + item.roleKey, function () {
                        self.list($scope);
                        Notification.success(Translator.get("i18n_menu_delete_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_menu_delete_fail") + "，" + response.message);
                    });
                });
            }
        };

        this.list = function () {
            return HttpUtils.paging($scope, "flow/design/role/list", {}, function () {
                let roleKeys = $scope.items.map(function (value) {
                    return value.roleKey;
                });
                HttpUtils.post("flow/design/role/used", roleKeys, function (response) {
                    angular.forEach($scope.items, function (item) {
                        angular.forEach(response.data, function (value) {
                            if(item.roleKey === value.roleKey){
                                item.used = value.used;
                                item.models = value.models;
                                item.linkKeys = value.linkKeys;
                            }
                        });
                    });
                });
            });
        };
    });


    F2CProcess.controller('ModelListCtrl', function ($scope, ModelManagement, Notification, Translator) {

        $scope.columns = [
            {value: Translator.get("i18n_process_model_id"), key: "modelId", sort: false},
            {value: Translator.get("i18n_process_model_name"), key: "modelName", sort: false},
            {value: Translator.get("i18n_process_model_version"), key: "modelVersion", sort: false},
            {value: Translator.get("i18n_process_model_update_time"), key: "modelTime", sort: false},
            {value: Translator.get("i18n_publish"), key: "deployId", sort: false}
        ];

        ModelManagement.init($scope);

        $scope.edit = function (item) {
            ModelManagement.edit(item.modelId);
        };

        $scope.copy = function (item) {
            Notification.prompt({
                title: Translator.get("i18n_process_model_copy"),
                text: Translator.get("i18n_process_model_id"),
                required: true,

            }, function (result) {
                $scope.loadingLayer = ModelManagement.copy(item.modelId, result, function () {
                    ModelManagement.edit(result);
                });
            });
        };

        $scope.delete = function (item) {
            Notification.confirm(Translator.get("i18n_confirm_perform_delete"), function () {
                ModelManagement.delete(item);
            });
        };

        $scope.publish = function (item) {
            $scope.loadingLayer = ModelManagement.publish(item);
        };

        $scope.list = function () {
            ModelManagement.list();
        };

        $scope.list();
    });

    F2CProcess.controller('ModelEditCtrl', function ($scope, $state, $stateParams, $timeout, $mdSidenav, HttpUtils, Translator) {

        $scope.init = function () {
            $scope.toggle = true;
            $scope.selected = null;
            $scope.parent = $stateParams.parent ? $stateParams.parent : sessionStorage.getItem("parent") || "flow_manager";
            $scope.modelId = $stateParams.modelId;
            $scope.type = $stateParams.type;

            // 刷新页面，没有传参数，则返回
            if (!$scope.type) {
                $scope.back();
                return;
            }

            if ($scope.type === "add") {
                $scope.model = {
                    modelId: "",
                    modelName: ""
                };
                $scope.activities = [
                    {
                        step: 0,
                        activityId: $scope.uuid(),
                        name: Translator.get("i18n_process_submit"),
                        url: "",
                        assigneeType: "CREATOR",
                        assignee: "OWNER"
                    }
                ];
                $scope.open($scope.model);
            } else {
                $scope.fixedHistoryData();
                $scope.list.model();
            }
        };

        // 2020-1-21以后可以删除这个功能
        $scope.fixedHistoryData = function () {
            if ($scope.type !== 'update') return;
            $scope.loadingLayer = HttpUtils.get("flow/design/fixed/" + $scope.modelId, function (response) {
            });
        };

        $scope.list = {
            model: function () {
                if ($scope.type !== 'update') return;
                $scope.loadingLayer = HttpUtils.get("flow/design/model/get/" + $scope.modelId, function (response) {
                    $scope.model = response.data;
                    if ($scope.model && $scope.model.modelContent) {
                        let obj = angular.fromJson($scope.model.modelContent);
                        $scope.activities = obj.activities;
                        $scope.open($scope.model);
                    }
                });
            },
            event: function () {
                if ($scope.type !== 'update') return;
                $scope.loadingEvent = HttpUtils.get("flow/design/event/list/" + $scope.model.modelId + "/" + $scope.selected + "/" + $scope.item.activityId, function (response) {
                    $scope.step = $scope.selected;
                    $scope.events = response.data;
                });
            },
            message: function () {
                if ($scope.type !== 'update') return;
                $scope.loadingMessage = HttpUtils.get("flow/design/notification/list/" + $scope.model.modelId + "/" + $scope.selected + "/" + $scope.item.activityId, function (response) {
                    $scope.step = $scope.selected;
                    $scope.messages = response.data;
                    angular.forEach($scope.messages, function (msg) {
                        msg.json = angular.fromJson(msg.template);
                    });
                });
            },
            open: function (item) {
                let step = item.step === undefined ? -1 : item.step;
                if (step === -1) {
                    $scope.title = Translator.get("i18n_process_process");
                } else {
                    $scope.title = Translator.get("i18n_process_link");
                }
                $scope.selected = step;
                $scope.item = item;
                $scope.toggle = true;
            }
        };

        $scope.refresh = function () {
            $scope.list.event();
            $scope.list.message();
        };

        $scope.back = function () {
            $state.go($scope.parent);
        };

        $scope.uuid = function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        };

        $scope.add = function (index, event) {
            if (event) event.stopPropagation();
            let step = index + 1;
            let act = {
                activityId: $scope.uuid()
            };
            $scope.activities.splice(step, 0, act);
            $scope.activities.forEach(function (act, step) {
                act.step = step;
            });
        };

        $scope.remove = function (index, event) {
            if (event) event.stopPropagation();
            let item = $scope.activities.splice(index, 1);
            if ($scope.item.activityId === item[0].activityId) {
                $scope.item = null;
                $scope.close();
            }
            $scope.activities.forEach(function (act, step) {
                act.step = step;
            });
        };

        $scope.open = function (item, event) {
            if (event) event.stopPropagation();
            let step = item.step === undefined ? -1 : item.step;
            if (step === -1) {
                $scope.title = Translator.get("i18n_process_process");
            } else {
                $scope.title = Translator.get("i18n_process_link");
            }

            // 流程信息关闭
            if (step === -1 && $scope.selected === step) {
                $scope.close();
                return;
            }
            // 环节信息关闭
            if ($scope.item && $scope.item.activityId === item.activityId) {
                $scope.close();
                return;
            }

            $scope.selected = step;
            $scope.item = item;
            $scope.refresh();
            $scope.toggle = true;
        };

        $scope.close = function () {
            $scope.selected = null;
            $scope.item = null;
            $scope.toggle = false;
            $mdSidenav('sidenav_form').close();
        };

        $scope.save = function () {
            if (angular.isFunction($scope.list.save)) {
                $scope.list.save(function () {
                    $scope.modelId = $scope.model.modelId;
                    $scope.type = "update";
                    $scope.list.model();
                });
            }
        };

        $scope.publish = function () {
            if (angular.isFunction($scope.list.publish)) $scope.list.publish();
        };

        $scope.init();
    });

    F2CProcess.directive("modelConfig", function (HttpUtils, Notification, Translator) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "web-public/fit2cloud/html/process/model-config.html" + '?_t=' + window.appversion,
            scope: {
                model: "=",
                activities: "=",
                item: "=",
                type: "=",
                api: "="
            },
            link: function ($scope) {
                let validate = function (value, msg) {
                    if (angular.isArray(value)) {
                        if (value.length === 0) {
                            Notification.warn(msg);
                            return false;
                        }
                    } else {
                        if (value === undefined || value === null || value.trim() === '') {
                            Notification.warn(msg);
                            return false;
                        }
                    }
                    return true;
                };

                let validateModel = function () {
                    let result = validate($scope.model.modelId, Translator.get("i18n_process_model_id_required")) && validate($scope.model.modelName, Translator.get("i18n_process_model_name_required"));
                    if (!result) {
                        $scope.api.open($scope.model);
                    }
                    return result;
                };

                let validateActivities = function () {
                    if ($scope.activities.length < 1) {
                        Notification.warn(Translator.get("i18n_process_need_least_one"));
                        return false;
                    }

                    for (let i = 0; i < $scope.activities.length; i++) {
                        let act = $scope.activities[i];
                        let name = validate(act.name, Translator.get("i18n_process_link_name_required"));
                        let linkType = validate(act.linkType, Translator.get("i18n_process_model_link_type_required"));
                        if (!(name && linkType)) {
                            $scope.api.open(act);
                            return false;
                        }
                        if(act.linkType !== 'PREDEFINE'){
                            let assignee = validate(act.assignee, Translator.get("i18n_process_link_handler_required"));
                            if (!assignee) {
                                $scope.api.open(act);
                                return false;
                            }
                        }

                    }
                    return true;
                };

                let convert = function (activity) {
                    switch (activity.assigneeType) {
                        case "CREATOR":
                            activity.assignee = "OWNER";
                            break;
                        case "USER":
                            let ids = [];
                            angular.forEach(activity.assigneeValue, function (user) {
                                ids.push(user.id);
                            });
                            activity.assignee = ids.join(",");
                            break;
                        case "SYSTEM_ROLE":
                            activity.assignee = activity.assigneeValue;
                            break;
                        case "PROCESS_ROLE":
                            activity.assignee = activity.assigneeValue;
                            break;
                        case "VARIABLES":
                            activity.assignee = activity.assigneeValue;
                            break;
                        default:
                            break;
                    }
                };

                $scope.api.save = function (callback) {
                    angular.forEach($scope.activities, function (activity) {
                        convert(activity);
                    });
                    if (!validateModel() || !validateActivities()) {
                        return;
                    }
                    let obj = {activities: $scope.activities};
                    $scope.model.modelContent = angular.toJson(obj, 4);
                    return HttpUtils.post('flow/design/model/' + $scope.type, $scope.model, function () {
                        if (angular.isFunction(callback)) callback();
                        Notification.success(Translator.get("i18n_save_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_save_fail") + "，" + response.message);
                    });
                };

                $scope.api.publish = function () {
                    return HttpUtils.get('flow/design/model/publish/' + $scope.model.modelId, function () {
                        $scope.api.model();
                        Notification.success(Translator.get("i18n_process_publish_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_process_publish_fail") + "，" + response.message);
                    });
                };

                $scope.changeLinkType = function (linkType) {
                    // $scope.item.name = "";
                    $scope.item.linkKey = "";
                };

                $scope.changeLinkKey = function (linkKey) {
                    angular.forEach($scope.preDefineLinks, function (link) {
                        if(link.linkKey === linkKey){
                            $scope.item.name = link.linkAlias;
                        }
                    });
                };

                $scope.selectType = function (type) {
                    if (type === 'USER') {
                        $scope.item.assigneeValue = [];
                    } else {
                        $scope.item.assigneeValue = "";
                    }
                };

                $scope.querySearch = function (query) {
                    if (!query) return [];
                    return HttpUtils.post("flow/design/user/list", {search: query}, function (response) {
                        return response.data;
                    });
                };

                $scope.getSystemRoles = function () {
                    HttpUtils.get("flow/design/role/system/list/all", function (response) {
                        $scope.systemRoles = response.data;
                    });
                };

                $scope.getPreDefineLinks = function () {
                    HttpUtils.get("flow/design/link/list/all", function (response) {
                        $scope.preDefineLinks = response.data;
                    });
                };

                $scope.getProcessRoles = function () {
                    HttpUtils.get("flow/design/role/list/all", function (response) {
                        $scope.roles = response.data;
                    });
                };

                $scope.init = function () {
                    $scope.selectedItem = null;
                    $scope.searchText = null;
                    $scope.getSystemRoles();
                    $scope.getProcessRoles();
                    $scope.getPreDefineLinks();
                };

                $scope.init();
            }
        };
    });

    F2CProcess.directive("eventList", function ($mdDialog, HttpUtils, Notification, $document, Translator) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "web-public/fit2cloud/html/process/event-list.html" + '?_t=' + window.appversion,
            scope: {
                items: "=",
                modelId: "=",
                activityId: "=",
                step: "=",
                list: "&"
            },
            link: function ($scope) {
                $scope.operations = [
                    {key: "CANCEL", name: Translator.get("i18n_process_cancel")},
                    {key: "COMPLETE", name: Translator.get("i18n_process_complete")},
                    {key: "TERMINATE", name: Translator.get("i18n_process_terminal")}
                ];

                // 获取事件执行器列表
                $scope.loadingEvent = HttpUtils.get('flow/design/event/executor/list', function (response) {
                    $scope.executors = response.data;
                });

                $scope.add = function () {
                    $scope.item = {
                        modelId: $scope.modelId,
                        activityId: $scope.activityId,
                        step: $scope.step,
                        type: $scope.step >= 0 ? "TASK" : "PROCESS"
                    };
                    $scope.type = "add";
                    $scope.open();
                };

                $scope.edit = function (item) {
                    $scope.item = angular.copy(item);
                    $scope.type = "update";
                    $scope.open();
                };

                $scope.delete = function (item) {
                    Notification.confirm(Translator.get("i18n_confirm_perform_delete"), function () {
                        $scope.loadingEvent = HttpUtils.get('flow/design/event/delete/' + item.id, function () {
                            $scope.list();
                            Notification.success(Translator.get("i18n_menu_delete_success"));
                        }, function (response) {
                            Notification.danger(Translator.get("i18n_menu_delete_fail") + "，" + response.message);
                        });
                    });
                };

                $scope.open = function () {
                    $mdDialog.show({
                        templateUrl: 'web-public/fit2cloud/html/process/event-edit.html',
                        parent: angular.element($document[0].body),
                        scope: $scope,
                        preserveScope: true,
                        clickOutsideToClose: false
                    });
                };

                $scope.close = function () {
                    $mdDialog.cancel();
                };

                $scope.save = function () {
                    HttpUtils.post("flow/design/event/" + $scope.type, $scope.item, function () {
                        $scope.list();
                        $scope.close();
                        Notification.success(Translator.get("i18n_save_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_save_fail") + "，" + response.message);
                    });
                };
            }
        };
    });

    F2CProcess.directive("messageList", function ($mdDialog, HttpUtils, Notification, $document, Translator) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "web-public/fit2cloud/html/process/message-list.html" + '?_t=' + window.appversion,
            scope: {
                items: "=",
                modelId: "=",
                activityId: "=",
                step: "=",
                list: "&"
            },
            link: function ($scope) {
                $scope.selectedItem = null;
                $scope.searchText = null;

                $scope.taskOperations = [
                    {key: "PENDING", name: Translator.get("i18n_process_task_operation_pending")},
                    {key: "COMPLETE", name: Translator.get("i18n_process_task_operation_complete")}
                ];

                $scope.smsTypes = [
                    {key: "EMAIL", name: Translator.get("i18n_process_sms_type_email")},
                    {key: "ANNOUNCEMENT", name: Translator.get("i18n_process_sms_type_announcement")}
                ];

                $scope.processOperations = [
                    {key: "CANCEL", name: Translator.get("i18n_process_operation_cancel")},
                    {key: "COMPLETE", name: Translator.get("i18n_process_operation_complete")},
                    {key: "TERMINATE", name: Translator.get("i18n_process_operation_terminate")},
                    {key: "BUSINESS_COMPLETE", name: Translator.get("i18n_process_operation_business_complete")}
                ];

                $scope.receivers = [
                    {key: "CREATOR", name: Translator.get("i18n_process_model_link_handler_creator")},
                    {key: "ASSIGNEE", name: Translator.get("i18n_process_model_link_handler")},
                    {key: "OTHER", name: Translator.get("i18n_process_model_link_handler_user")}
                ];

                $scope.add = function () {
                    $scope.item = {
                        modelId: $scope.modelId,
                        activityId: $scope.activityId,
                        step: $scope.step,
                        processType: $scope.step >= 0 ? "TASK" : "PROCESS",
                        smsTypes: ["ANNOUNCEMENT"],
                        json: {
                            receivers: angular.copy($scope.receivers),
                            title: "",
                            content: "",
                            others: []
                        }
                    };
                    $scope.type = "add";
                    $scope.open();
                };

                $scope.edit = function (item) {
                    $scope.item = angular.copy(item);
                    $scope.item.smsTypes = $scope.item.smsType.split(",");
                    $scope.type = "update";
                    $scope.translateReceivers($scope.item);
                    $scope.open();
                };

                $scope.translateReceivers = function (item) {
                    if (angular.isUndefined(item) || item === null) {
                        return;
                    }

                    if (angular.isUndefined(item.json) || item.json === null) {
                        return;
                    }

                    if (angular.isArray(item.json.receivers)) {
                        angular.forEach(item.json.receivers, function (r) {
                            if (r.key === 'CREATOR') {
                                r.name = Translator.get("i18n_process_model_link_handler_creator");
                            }
                            if (r.key === 'ASSIGNEE') {
                                r.name = Translator.get("i18n_process_model_link_handler");
                            }
                            if (r.key === 'OTHER') {
                                r.name = Translator.get("i18n_process_model_link_handler_user");
                            }
                        });
                    }
                };

                $scope.open = function () {
                    $mdDialog.show({
                        templateUrl: 'web-public/fit2cloud/html/process/message-edit.html' + '?_t=' + window.appversion,
                        parent: angular.element($document[0].body),
                        scope: $scope,
                        preserveScope: true,
                        clickOutsideToClose: false
                    });
                };

                $scope.close = function () {
                    $mdDialog.cancel();
                };

                $scope.checkOther = function (receiver) {
                    if (receiver.key === "OTHER") {
                        $scope.other = receiver.checked;
                    }
                };

                $scope.transform = function (chip) {
                    if (angular.isObject(chip)) {
                        return chip.email;
                    }

                    return chip;
                };

                $scope.querySearch = function (query) {
                    if (!query) return [];
                    return HttpUtils.post("flow/design/user/list", {search: query}, function (response) {
                        return response.data;
                    });
                };

                $scope.delete = function (item) {
                    Notification.confirm(Translator.get("i18n_confirm_perform_delete"), function () {
                        $scope.loadingEvent = HttpUtils.get('flow/design/notification/delete/' + item.id, function () {
                            $scope.list();
                            Notification.success(Translator.get("i18n_menu_delete_success"));
                        }, function (response) {
                            Notification.danger(Translator.get("i18n_menu_delete_fail") + "，" + response.message);
                        });
                    });
                };

                $scope.save = function () {
                    $scope.item.template = angular.toJson($scope.item.json, 4);
                    $scope.item.smsType = $scope.item.smsTypes.join(",");
                    HttpUtils.post("flow/design/notification/" + $scope.type, $scope.item, function () {
                        $scope.list();
                        $scope.close();
                        Notification.success(Translator.get("i18n_save_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_save_fail") + "，" + response.message);
                    });
                };
            }
        };
    });

    F2CProcess.controller('RoleListCtrl', function ($scope, RoleManagement, Translator) {

        $scope.columns = [
            {value: Translator.get("i18n_process_role_id"), key: "roleKey", sort: false},
            {value: Translator.get("i18n_process_role_name"), key: "roleName", sort: false}
        ];

        RoleManagement.init($scope);

        $scope.edit = function (item) {
            RoleManagement.edit(item);
        };

        $scope.delete = function (item) {
            RoleManagement.delete(item);
        };

        $scope.list = function () {
            $scope.loadingLayer = RoleManagement.list();
        };

        $scope.list();
    });

    F2CProcess.controller('RoleEditCtrl', function ($scope, $state, $stateParams, HttpUtils, Notification, Translator) {

        $scope.init = function () {
            $scope.toggle = true;
            $scope.parent = $stateParams.parent ? $stateParams.parent : sessionStorage.getItem("parent") || "";
            $scope.item = angular.copy($stateParams.role);
            $scope.type = $stateParams.type;
            $scope.selected = [];
            $scope.users = [];
            if (!$scope.type) {
                $state.go($scope.parent);
                return;
            }
            if ($scope.type === 'update') {
                $scope.list();
            } else {
                $scope.done = true;
            }
        };

        $scope.list = function () {
            $scope.done = false;
            $scope.loadingLayer = HttpUtils.get("flow/design/user/list/" + $scope.item.roleKey, function (response) {
                $scope.selected = [];
                angular.forEach(response.data, function (user) {
                    $scope.selected.push(user);
                });
                $scope.done = true;
            });
        };

        $scope.save = function () {
            // 保存角色
            $scope.loadingLayer = HttpUtils.post("flow/design/role/" + $scope.type, $scope.item, function () {
                // 保存角色成员
                let userIds = [];
                angular.forEach($scope.selected, function (user) {
                    userIds.push(user.id);
                });
                $scope.loadingLayer = HttpUtils.post("flow/design/role/user/save?key=" + $scope.item.roleKey, userIds, function () {
                    Notification.success(Translator.get("i18n_save_success"));
                });
            });
        };

        $scope.init();
    });


    F2CProcess.service('LinkManagement', function ($state, HttpUtils, Notification, Translator) {
        let $scope;

        this.init = function (scope) {
            $scope = scope
        };

        this.add = function () {
            $scope.type = 'add';
            $scope.linkItem = {};
            $scope.formUrl = "web-public/fit2cloud/html/process/link-edit.html?_t=" + Math.random();
            $scope.toggleForm();
        };

        this.edit = function (item) {
            $scope.type = 'update';
            $scope.item = item;
            $scope.toggleForm();
        };

        this.submit = function () {
            $scope.loadingLayer = HttpUtils.post("flow/design/link/" + $scope.type, $scope.linkItem, function () {
                $scope.toggleForm();
                $scope.$broadcast('linkList');
                Notification.success(Translator.get("i18n_save_success"));
                delete $scope.type;
                delete $scope.linkItem;
            });
        };
    });


    F2CProcess.controller('LinkListCtrl', function ($scope, $rootScope, HttpUtils, Translator, Notification, $state, LinkManagement, $mdSidenav) {

        $scope.columns = [
            {value: Translator.get("i18n_process_link_id"), key: "link_key"},
            {value: Translator.get("i18n_process_link_name"), key: "link_alias"},
            {value: Translator.get("i18n_process_link_enable"), key: "enable"}
        ];

        $scope.edit = function (item) {
            $scope.$emit('formUrl', "web-public/fit2cloud/html/process/link-edit.html?_t=" + Math.random());
            $scope.$emit('type', 'update');
            $scope.$emit('linkItem', item);

        };

        $scope.submit = function () {
            $scope.loadingLayer = HttpUtils.post("flow/design/link/" + $scope.type, $scope.item, function () {
                $scope.listLink();
                $scope.toggleForm();
                Notification.success(Translator.get("i18n_save_success"));
                delete $scope.type;
                delete $scope.linkItem;
            });
        };

        $scope.deleteLink = function (item) {
            var desc;
            if(item.modelCount === 0){
                desc = Translator.get("i18n_confirm_perform_delete");
                Notification.confirm(desc, function () {
                    HttpUtils.get('flow/design/link/delete?key=' + item.linkId, function () {
                        $scope.listLink();
                        Notification.success(Translator.get("i18n_menu_delete_success"));
                    }, function (response) {
                        Notification.danger(Translator.get("i18n_menu_delete_fail") + "，" + response.message);
                    });
                });
            }else {
                desc = item.modelCount + Translator.get("i18n_flow_used_link") + item.modelNames + ", " + Translator.get("i18n_delete_after_reconfig");
                Notification.danger(desc);
                return;
            }
        };

        $scope.listLink = function (sortObj) {
            if (sortObj || $scope.sort) {
                $scope.sort = sortObj || $scope.sort;
            } else {
                $scope.sort = {sql: 'flow_link.create_time desc'}
            }

            HttpUtils.paging($scope, "flow/design/link/list", {sql: $scope.sort.sql}, function () {
                let linkKeys = $scope.items.map(function (value) {
                    return value.linkKey;
                });
                HttpUtils.post("flow/design/link/models", linkKeys, function (response) {
                    angular.forEach($scope.items, function (item) {
                        angular.forEach(response.data, function (value) {
                            if(item.linkKey === value.linkKey){
                                item.modelCount = value.modelCount;
                                item.modelNames = value.modelNames;
                            }
                        });
                    });
                });
            });
        };

        $scope.editValues = function(item){
            sessionStorage.setItem("parent", $state.current.name);
            $state.go("link_values", {link: item, parent: $state.current.name});
        };

        $scope.editLinkStatus = function(item){
            if(item.modelCount > 0 && !item.enable){
                Notification.confirm(item.modelCount + Translator.get("i18n_flow_used_link") + Translator.get("i18n_confirm_forbidden"), function () {
                    HttpUtils.post("flow/design/link/update", item, function () {
                        Notification.success(Translator.get('i18n_process_link_edit_success', '修改完成'));
                    });
                }, function () {
                    item.enable = !item.enable;
                });
            }else {
                HttpUtils.post("flow/design/link/update", item, function () {
                    Notification.success(Translator.get('i18n_process_link_edit_success', '修改完成'));
                });
            }
        };

        $scope.$on('linkList', $scope.listLink);

        $scope.listLink();
    });


    F2CProcess.controller('LinkValuesCtrl', function ($scope, $state, $stateParams, HttpUtils, Notification, Translator, $filter, $interval,) {

        $scope.columns = [
            {value: Translator.get("i18n_process_link_value"), key: "linkKey", sort: false},
            {value: Translator.get("i18n_process_link_value_alias"), key: "linkKey", sort: false},
            {value: Translator.get("i18n_process_link_value_priority"), key: "linkKey", sort: false},
            {value: Translator.get("i18n_process_model_link_handler_type"), key: "linkKey", sort: false},
            {value: Translator.get("i18n_process_model_link_handler"), key: "linkKey", sort: false}
        ];

        $scope.init = function () {
            $scope.getSystemRoles();
            $scope.getProcessRoles();
            $scope.toggle = true;
            $scope.parent = $stateParams.parent ? $stateParams.parent : sessionStorage.getItem("parent") || "";
            $scope.link = angular.copy($stateParams.link);
        };

        $scope.timer = $interval(function () {
            if($scope.systemRoles && $scope.roles){
                $scope.list();
                $interval.cancel($scope.timer);
            }
        }, 100, 100);

        $scope.list = function () {
            HttpUtils.paging($scope, "flow/design/link/value/list/" + $scope.link.linkKey, {}, function () {
                angular.forEach($scope.items, function (item) {
                    if(item.assigneeType === 'CREATOR'){
                        item.assigneeDesc = Translator.get("i18n_process_model_link_handler_creator");
                    }
                    if(item.assigneeType === 'SYSTEM_ROLE'){
                        angular.forEach($scope.systemRoles, function (systemRole) {
                            if (systemRole.id === item.assignee) {
                                item.assigneeDesc = systemRole.name;
                            }
                        });
                    }
                    if(item.assigneeType === 'PROCESS_ROLE'){
                        angular.forEach($scope.roles, function (role) {
                            if (role.roleKey === item.assignee) {
                                item.assigneeDesc = role.roleName;
                            }
                        });
                    }
                    if(item.assigneeType === 'VARIABLES'){
                        item.assigneeDesc = item.assignee;
                    }
                    if(item.assigneeType === 'USER'){
                        item.assigneeDesc = item.assignee;
                    }
                });
            });
        };

        $scope.delete = function (item) {
            $scope.loadingLayer = HttpUtils.get("flow/design/link/value/delete/" + item.id, function () {
                Notification.success(Translator.get("i18n_save_success"));
                $scope.list();
            });
        };

        $scope.edit = function (item) {
            $scope.selectedItem = null;
            $scope.searchText = null;
            $scope.type = 'update';
            $scope.item = item;
            if (item.assigneeType === 'USER') {
                $scope.item.assigneeValue = angular.fromJson(item.assigneeValue);
            }
            $scope.formUrl = "web-public/fit2cloud/html/process/link-value-edit.html?_t=" + Math.random();
            $scope.toggleForm();
        };

        $scope.wizard = {
            setting: {
                title: $filter('translator')('i18n_title', '标题'),
                subtitle: $filter('translator')('i18n_subtitle', '子标题'),
                closeText: $filter('translator')('i18n_cancel', '取消'),
                submitText: $filter('translator')('i18n_save', '保存'),
                nextText: $filter('translator')('i18n_next_step', '下一步'),
                prevText: $filter('translator')('i18n_prev_step', '上一步')
            },
            steps: [
                {
                    id: "1",
                    name: $filter('translator')('i18n_base_info', '基本信息'),
                    select: function () {

                    },
                    next: function () {
                        if (!$scope.item.linkValue) {
                            Notification.info($filter('translator')('i18n_process_link_value') + $filter('translator')('i18n_no_empty'));
                            return false;
                        }
                        if (!$scope.item.linkValueAlias) {
                            Notification.info($filter('translator')('i18n_process_link_value_alias', '描述') + $filter('translator')('i18n_no_empty', '不能为空')
                            );
                            return false;
                        }
                        if (!$scope.item.linkValuePriority ) {
                            Notification.info($filter('translator')('i18n_process_link_value_priority', '优先级') + $filter('translator')('i18n_no_empty', '不能为空')
                            );
                            return false;
                        }
                        if (!$scope.item.assigneeType ) {
                            Notification.info($filter('translator')('i18n_process_model_link_handler', '处理人') + $filter('translator')('i18n_no_empty', '不能为空')
                            );
                            return false;
                        }
                        if ($scope.item.assigneeType !== 'CREATOR' && !$scope.item.assigneeValue ) {
                            Notification.info($filter('translator')('i18n_process_model_link_handler', '处理人') + $filter('translator')('i18n_no_empty', '不能为空')
                            );
                            return false;
                        }
                        return true;
                    }
                },
                {
                    id: "2",
                    name: $filter('translator')('i18n_select_workspace', '选择工作空间'),
                    select: function () {
                        $scope.listProductGroupWorkspaceTree();
                    },
                    next: function () {
                        $scope.submit();
                        return true;
                    }
                }
            ],
            // 嵌入页面需要指定关闭方法
            close: function () {
                $scope.item = {};
                $scope.toggleForm(false);
                $scope.wizard.current = 0;
            }
        };

        $scope.listProductGroupWorkspaceTree = function() {
            $scope.loadingLayer = HttpUtils.get("flow/design/workspace/tree/" + $scope.item.id, function(response) {
                $scope.item.treeData = response.data;
            }, function (rep) {
                Notification.danger(rep.message);
            })
        };

        $scope.add = function () {
            $scope.selectedItem = null;
            $scope.searchText = null;
            $scope.type = 'add';
            $scope.item = {"linkKey": $scope.link.linkKey, permissionMode: "WHITELIST"};
            $scope.formUrl = "web-public/fit2cloud/html/process/link-value-add.html?_t=" + Math.random();
            $scope.toggleForm();
        };

        $scope.commit = false;

        $scope.noroot = {};

        $scope.submit = function () {
            convert($scope.item);
            $scope.commit = true;
            if($scope.noroot.getSelected) {
                $scope.item.treeNodes = $scope.noroot.getSelected();
            }
            $scope.loadingLayer = HttpUtils.post("flow/design/link/value/" + $scope.type, $scope.item, function () {
                $scope.commit = false;
                Notification.success(Translator.get("i18n_save_success"));
                $scope.toggleForm(false);
                $scope.wizard.current = 0;
                delete $scope.item;
                $scope.list();
            }, function (rep) {
                $scope.commit = false;
                Notification.danger(rep.message);
            });
        };

        $scope.editLinkValueScope = function(item) {
            $scope.item = angular.copy(item);
            $scope.formUrl = 'web-public/fit2cloud/html/process/link-value-workspace-edit.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };

        $scope.selectType = function (type) {
            if (type === 'USER') {
                $scope.item.assigneeValue = [];
            } else {
                $scope.item.assigneeValue = "";
            }
        };
        
        let convert = function (activity) {
            switch (activity.assigneeType) {
                case "CREATOR":
                    activity.assignee = "OWNER";
                    break;
                case "USER":
                    let ids = [];
                    angular.forEach(activity.assigneeValue, function (user) {
                        ids.push(user.id);
                    });
                    activity.assignee = ids.join(",");
                    activity.assigneeValue = angular.toJson(activity.assigneeValue);
                    break;
                case "SYSTEM_ROLE":
                    activity.assignee = activity.assigneeValue;
                    break;
                case "PROCESS_ROLE":
                    activity.assignee = activity.assigneeValue;
                    break;
                case "VARIABLES":
                    activity.assignee = activity.assigneeValue;
                    break;
                default:
                    break;
            }
        };

        $scope.selectType = function (type) {
            if (type === 'USER') {
                $scope.item.assigneeValue = [];
            } else {
                $scope.item.assigneeValue = "";
            }
        };

        $scope.querySearch = function (query) {
            if (!query) return [];
            return HttpUtils.post("flow/design/user/list", {search: query}, function (response) {
                return response.data;
            });
        };

        $scope.getSystemRoles = function () {
            HttpUtils.get("flow/design/role/system/list/all", function (response) {
                $scope.systemRoles = response.data;
            });
        };

        $scope.getProcessRoles = function () {
            HttpUtils.get("flow/design/role/list/all", function (response) {
                $scope.roles = response.data;
            });
        };

        $scope.closeToggleForm = function () {
            $scope.toggleForm();
            $scope.item = {};
            $scope.model = {};
        };

        $scope.init();
    });

    F2CProcess.controller('LinkValueEditWorkspaceController', function($scope, HttpUtils, Notification, $filter) {
        $scope.model = {
            id:             $scope.item.id,
            permissionMode: $scope.item.permissionMode
        };
        if($scope.model.permissionMode == undefined || $scope.model.permissionMode == null){
            $scope.model.permissionMode = "WHITELIST";
        }
        $scope.commit = false;

        $scope.noroot = {};

        $scope.submit = function() {
            $scope.commit = true;
            //如果是个空树getSelected属性将是undefinded
            if($scope.noroot.getSelected) {
                $scope.model.treeNodes = $scope.noroot.getSelected();
            }
            $scope.loadingLayer = HttpUtils.post("flow/design/workspace/tree/update/" + $scope.item.id , $scope.model, function() {
                $scope.commit = false;
                $scope.list();
                Notification.success($filter('translator')('i18n_opt_success', '操作成功'));
                $scope.closeToggleForm();
                delete $scope.item;
                delete $scope.model;
            }, function (rep) {
                $scope.commit = false;
                Notification.danger(rep.message);
            })
        };

        $scope.listProductGroupWorkspaceTree = function() {
            $scope.loadingLayer = HttpUtils.get("flow/design/workspace/tree/" + $scope.item.id, function(response) {
                $scope.model.treeData = response.data;
            }, function (rep) {
                Notification.danger(rep.message);
            })
        };

        $scope.listProductGroupWorkspaceTree();
    });

})();