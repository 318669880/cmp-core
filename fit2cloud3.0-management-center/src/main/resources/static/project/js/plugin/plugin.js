ProjectApp.controller('PluginListController', function ($scope, $mdDialog, $document, $mdBottomSheet, HttpUtils, FilterSearch, Translator) {
    $scope.conditions = [
        {key: "name", name: Translator.get("i18n_plugin_name"), directive: "filter-contains"},
        {key: "description", name: Translator.get("i18n_plugin_desc"), directive: "filter-contains"},
        {
            key: "pluginType",
            name: Translator.get("i18n_plugin_type"),
            directive: "filter-select",
            selects: [
                {value: "infrastructure", label: Translator.get("i18n_infrastructure")},
                {value: "container", label: Translator.get("i18n_container_cloud")}
            ]
        }
    ];

    // 用于传入后台的参数
    $scope.filters = [];

    $scope.columns = [
        {value: Translator.get("i18n_plugin_name"), key: "name"},
        {value: Translator.get("i18n_plugin_desc"), key: "description", sort: false},
        {value: Translator.get("i18n_plugin_type"), key: "plugin_type"},
        {value: Translator.get("i18n_plugin_version"), key: "version", sort: false},
        {value: Translator.get("i18n_plugin_update_time"), key: "update_time", sort: false},
        {value: Translator.get("i18n_plugin_platform_version"), key: "plugin_platform", checked: false, sort: false}
    ];

    $scope.list = function (sortObj) {
        var condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, 'plugin/list', condition)

    };

    $scope.document = function(item){
        if(item.documentUrl){
            window.open(item.documentUrl);
        }
    };

    $scope.list();
});


