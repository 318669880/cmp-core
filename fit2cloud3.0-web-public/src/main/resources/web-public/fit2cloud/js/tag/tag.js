/**
 *
 */
(function () {
    let F2CTag = angular.module('f2c.tag', []);

    F2CTag.run(function (MenuRouter, Translator) {
        let TAG_MENUS = [
            {
                title: Translator.get("i18n_label_value_list"),
                name: "tag-values-edit",
                url: "/tag/values",
                params: {
                    modelId: "",
                    parent: "",
                    type: "",
                    tag: null,
                    tagParam: null
                },
                templateUrl: "web-public/fit2cloud/html/tag/tag-value-list.html" + '?_t=' + window.appversion
            }
        ];
        MenuRouter.addStates(TAG_MENUS);
    });

    F2CTag.controller('TagController', function ($scope, $mdDialog, $document, $state, $stateParams, HttpUtils, FilterSearch, Notification, AuthService, $filter) {
        $scope.tagParam = $stateParams.tagParam ? $stateParams.tagParam : angular.fromJson(sessionStorage.getItem("tagParam")) || {};
        sessionStorage.removeItem("tagParam");
        const translator = $filter("translator");

        $scope.conditions = [
            {key: "tagKey", name: translator('i18n_label', '标签'), directive: "filter-contains"},
            {key: "tagAlias", name: translator('i18n_label_alias', '别名'), directive: "filter-contains"}
        ];

        // 用于传入后台的参数
        $scope.filters = $scope.tagParam.filters || [];
        // 全选按钮，添加到$scope.columns
        $scope.first = {
            default: true,
            sort: false,
            type: "checkbox",
            checkValue: false,
            change: function (checked) {
                $scope.items.forEach(function (item) {
                    item.enable = checked;
                });
            },
            width: "40px"
        };

        $scope.showDetail = function (item) {
            $scope.detail = item;
        };

        $scope.columns = [
            {value: translator('i18n_label', '标签'), key: "tag_key", width: "30%"},
            {value: translator('i18n_label_alias', '别名'), key: "tag_alias"},
            // {value: "类别", key: "tag_type"},
            {value: translator('i18n_label_enable', "是否启用"), key: "enable"},// 不想排序的列，用sort: false
            {value: translator('i18n_label_required', "是否必选"), key: "required"}
        ];
        if (AuthService.hasPermissions("TAG:READ+UPGRADE,TAG:READ+DELETE,TAG:READ+TAG_VALUE:READ")) {
            $scope.columns.push({value: "", default: true, sort: false});
        }

        $scope.editTagForm = function (item) {
            $scope.item = item;
            $scope.formUrl = 'web-public/fit2cloud/html/tag/tag-edit.html' + '?_t=' + window.appversion;
            $scope.toggleForm();
        };

        $scope.editTagSubmit = function (item) {
            var message = item.tagId ? translator('i18n_label_edit_success', '修改完成') : translator('i18n_label_add_success', '新增完成');
            var url = item.tagId ? 'tag/update' : 'tag/add';
            item.tagType = 'FIT2CLOUD'; // 默认的type
            HttpUtils.post(url, item, function () {
                $scope.toggleForm();
                $scope.list();
                Notification.success(message);
            });
        };

        $scope.editTagStatus = function (item) {
            HttpUtils.post("tag/update", item, function () {
                Notification.success(translator('i18n_label_edit_success', '修改完成'));
            });
        };

        $scope.deleteTag = function (item) {
            Notification.confirm(translator('i18n_label_delete_confirm', '确认删除此标签?'), function () {
                // 确认删除
                HttpUtils.post('tag/delete/' + item.tagId, null, function (response) {
                    $scope.list();
                    Notification.success(translator('i18n_label_delete_success', '删除成功'));
                });
            });
        };

        $scope.editValues = function (item) {
            $state.go("tag-values-edit", {
                tag: item,
                tagParam: {pagination: $scope.pagination, filters: $scope.filters}
            });
        };

        // 保证从标签值页面返回的时候分页正常显示
        $scope.pagination = $scope.tagParam.pagination || {
            page: 1,
            limit: 10,
            limits: [10, 20, 50, 100]
        };

        $scope.list = function (sortObj, page, limit) {
            var condition = FilterSearch.convert($scope.filters);
            if (sortObj) {
                $scope.sort = sortObj;
            } else {
                $scope.sort = {sql: 'scope asc'};
            }
            // 保留排序条件，用于分页
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }

            HttpUtils.paging($scope, 'tag/list', condition)
        };

        $scope.list();
    });

    F2CTag.controller('TagValuesController', function ($scope, $mdDialog, $state, $stateParams, $document, HttpUtils, FilterSearch, Notification, AuthService, $filter) {
        $scope.tag = $stateParams.tag ? $stateParams.tag : angular.fromJson(sessionStorage.getItem("tag"));
        sessionStorage.setItem("tag", angular.toJson($scope.tag));

        $scope.tagParam = $stateParams.tagParam ? $stateParams.tagParam : angular.fromJson(sessionStorage.getItem("tagParam"));
        sessionStorage.setItem("tagParam", angular.toJson($scope.tagParam));

        const translator = $filter("translator");


        $scope.conditions = [
            {key: "tagValue", name: translator('i18n_label_value', "值"), directive: "filter-contains"},
            {key: "tagValueAlias", name: translator('i18n_label_alias', "别名"), directive: "filter-contains"}
        ];

        // 用于传入后台的参数
        $scope.filters = [{
            key: "tagKey",
            name: translator('i18n_label', "标签"),
            value: $scope.tag.tagKey,
            default: true,
            operator: "="
        }];
        // 全选按钮，添加到$scope.columns
        $scope.first = {
            default: true,
            sort: false,
            type: "checkbox",
            checkValue: false,
            change: function (checked) {
                $scope.items.forEach(function (item) {
                    item.enable = checked;
                });
            },
            width: "40px"
        };

        $scope.goTags = function () {
            $state.go('tag')
        };

        $scope.deleteTagValue = function (item) {
            Notification.confirm(translator('i18n_label_value_delete_confirm', '确认删除此标签值?'), function () {
                // 确认删除
                HttpUtils.post('tag/value/delete/' + item.id, null, function (response) {
                    Notification.success(translator('i18n_label_delete_success', '删除成功'));
                    $scope.list();
                });
            });
        };

        $scope.columns = [
            {value: translator('i18n_label', "标签"), key: "tag_key", sort: false},
            {value: translator('i18n_label_value', "值"), key: "tag_value"},
            {value: translator('i18n_label_alias', "别名"), key: "tag_value_alias"}
        ];
        if (AuthService.hasPermissions("TAG:READ+TAG_VALUE:READ+EDIT,TAG:READ+TAG_VALUE:READ+DELETE")) {
            $scope.columns.push({value: "", default: true, sort: false});
        }
        $scope.editTagValueForm = function (item) {
            item.tagKey = $scope.tag.tagKey;
            item.tagId = $scope.tag.tagId;
            $scope.item = item.id ? item : {tagKey: $scope.tag.tagKey, tagId: $scope.tag.tagId};
            $scope.formUrl = 'web-public/fit2cloud/html/tag/tag-value-edit.html' + '?_t=' + window.appversion;
            $scope.toggleForm();
        };

        $scope.editTagValueSubmit = function (item) {
            var message = item.id ? translator('i18n_label_edit_success', '修改完成') : translator('i18n_label_add_success', '新增完成');
            var url = item.id ? 'tag/value/update' : 'tag/value/add';
            HttpUtils.post(url, item, function () {
                $scope.list();
                Notification.success(message);
                $scope.toggleForm();
            });
        };

        $scope.import = function () {
            $scope.formUrl = 'web-public/fit2cloud/html/tag/tag-value-import.html' + '?_t=' + Math.random();
            $scope.toggleForm();
        };

        $scope.uploadFile = function (file, isClear) {
            if (!file) {
                Notification.info('请选择文件');
                return;
            }

            var formData = new FormData();
            formData.append("tagId", $scope.tag.tagId);
            formData.append("tagKey", $scope.tag.tagKey);
            formData.append("isClear", !!isClear);
            formData.append('file', file[0]);
            HttpUtils.post('tag/value/import', formData, function (response) {
                Notification.success(translator('i18n_label_import_success', '导入成功，导入总数:') + response.data);
                $scope.toggleForm();
                $scope.list();
            }, function (response) {
                Notification.danger(translator('i18n_label_import_failed', '导入失败. ') + response.message);
            }, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            });
        };

        $scope.list = function (sortObj, page, limit) {
            var condition = FilterSearch.convert($scope.filters);
            condition.tagId = $scope.tag.tagId;
            if (sortObj) {
                $scope.sort = sortObj;
            }
            // 保留排序条件，用于分页
            if ($scope.sort) {
                condition.sort = $scope.sort.sql;
            }
            HttpUtils.paging($scope, 'tag/value/list', condition)
        };

        $scope.list();
    });
})();

