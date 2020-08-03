/**
 * FIT2CLOUD Menu Frame
 */

let TestApp = angular.module('TestApp', ['f2c.common', 'f2c.process']);

// 测试专用
let MENUS_TEST = {
    name: "自服务",
    icon: "shopping_cart",
    menus: [
        {
            title: "仪表盘",
            icon: "dashboard",
            name: "dashboard",
            url: "/dashboard",
            templateUrl: "web-public/test/demo/dashboard.html" + '?_t=' + window.appversion
        }, {
            title: "示例1",
            icon: "assignment",
            children: [
                {
                    title: "Select",
                    name: "select",
                    url: "/select",
                    templateUrl: "web-public/test/demo/select.html" + '?_t=' + window.appversion
                }, {
                    title: "翻译",
                    name: "translate",
                    url: "/translate",
                    default: true,
                    templateUrl: "web-public/test/demo/translate.html" + '?_t=' + window.appversion
                }, {
                    title: "Grid",
                    name: "grid",
                    url: "/grid",
                    templateUrl: "web-public/test/demo/grid.html" + '?_t=' + window.appversion
                }, {
                    title: "表格",
                    name: "table",
                    url: "/table",
                    templateUrl: "web-public/test/demo/table.html" + '?_t=' + window.appversion
                }, {
                    title: "Stepper",
                    name: "stepper",
                    url: "/stepper",
                    templateUrl: "web-public/test/demo/stepper.html" + '?_t=' + window.appversion
                }, {
                    title: "按钮",
                    name: "button",
                    url: "/button",
                    templateUrl: "web-public/test/demo/buttons.html" + '?_t=' + window.appversion
                }, {
                    title: "树",
                    name: "tree",
                    url: "/tree",
                    templateUrl: "web-public/test/demo/tree.html" + '?_t=' + window.appversion
                }, {
                    title: "Notification",
                    name: "notify",
                    url: "/notify",
                    templateUrl: "web-public/test/demo/notification.html" + '?_t=' + window.appversion
                }, {
                    title: "choose",
                    name: "choose",
                    url: "/choose",
                    templateUrl: "web-public/test/demo/choose.html" + '?_t=' + window.appversion
                }, {
                    title: "拖拽",
                    name: "drag",
                    url: "/drag",
                    templateUrl: "web-public/test/demo/drag.html" + '?_t=' + window.appversion
                }, {
                    title: "流程管理",
                    name: "flow",
                    url: "/flow",
                    templateUrl: "web-public/fit2cloud/html/process/process-management.html" + '?_t=' + window.appversion
                }, {
                    title: "文件",
                    name: "file",
                    url: "/file",
                    templateUrl: "web-public/test/demo/file.html" + '?_t=' + window.appversion
                }, {
                    title: "主题颜色",
                    name: "color",
                    url: "/color",
                    templateUrl: "web-public/test/demo/color.html" + '?_t=' + window.appversion
                }, {
                    title: "material-webfont",
                    name: "material",
                    url: "/material",
                    templateUrl: "web-public/test/demo/material.html" + '?_t=' + window.appversion
                },
                {
                    title: "db-demo",
                    name: "db",
                    url: "/db",
                    templateUrl: "web-public/test/demo/demo.html" + '?_t=' + window.appversion
                }
            ]
        }
    ]
};

let HIDDEN_MENU = [{
    title: "表单",
    name: "form",
    url: "/form",
    templateUrl: "web-public/test/demo/form.html" + '?_t=' + window.appversion
}
];

TestApp.controller('TestCtrl', function ($scope, MenuRouter, Translator) {
    $scope.module = MENUS_TEST;
    // 自定义路由，不显示在菜单内
    MenuRouter.addStates(HIDDEN_MENU);
    // Translator.setLang("en_US");
});

TestApp.controller('TranslateCtrl', function ($scope, Translator, $translate) {
    $scope.singleKey = "exception";
    $scope.multipleKey = '订单号：{{{confirm}}}，交易商品：{{exception}}，初步估价：{{键_1}}元';

    let test = [{"id": "ddd", "name": Translator.get('confirm')}];
    console.log(test);

    console.log(Translator.get('confirm'));
    console.log(Translator.gets($scope.multipleKey));
    console.log($translate.instant('confirm'));
});

TestApp.controller('TableCtrl', function ($scope, $mdDialog, $mdBottomSheet, FilterSearch, Notification, HttpUtils, Loading) {

    // 定义搜索条件
    $scope.conditions = [
        {
            key: "priority",
            name: "分页显示",
            directive: "filter-select-virtual", // 使用哪个指令
            selects: [],
            // 测试select类型条件的搜索框
            search: true
        },
        {
            key: "priority",
            name: "优先级[有查询，可多选]",
            directive: "filter-select-multiple", // 使用哪个指令
            selects: [
                {value: 1, label: "选项选项选项选项选项选项选项选项选项选项选项选项选项选项选项选项选项选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ],
            // 测试select类型条件的搜索框
            search: true
        }, {
            key: "priority",
            name: "优先级[有查询]",
            directive: "filter-select", // 使用哪个指令
            selects: [
                {value: 1, label: "选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ],
            // 测试select类型条件的搜索框
            search: true
        }, {
            key: "priority",
            name: "优先级[无查询]",
            directive: "filter-select-operators",
            operator: "NOT IN",
            selects: [
                {value: 1, label: "选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ]
        },
        {key: "no", name: "工单编号", directive: "filter-input"},
        //查询虚机的条件
        {key: "instanceName", name: "实例名", directive: "filter-contains"},
        {key: "created", name: "创建日期", directive: "filter-date", directiveUnit: "second"},//directiveUnit: "second"返回时间戳为秒
        {key: "os", name: "操作系统", directive: "filter-contains"},
        {key: "localIp", name: "内网IP", directive: "filter-contains"},
        //增加一个异步字典转换的例子，将请求内容转换为value,label格式
        {
            key: "ajax",
            name: "异步字典",
            directive: "filter-select-ajax",
            url: "demo/status",
            convert: {value: "id", label: "name"}
        }

    ];

    for (let i = 1; i <= 5020; i++) {
        $scope.conditions[0].selects.push({value: i, label: "选项 " + i});
    }

    // 用于传入后台的参数
    $scope.filters = [
        // 设置默认条件default:true(默认条件不会被删掉)，
        {key: "status", name: "主机状态", value: "Running", label: "运行中", default: true, operator: "="},
        {key: "status", name: "主机状态", value: "Running", default: true, operator: "="},
        {key: "status", name: "主机状态", value: "Running", operator: "="},
        // 可以设置是否显示(display:false不显示，不加display或者display:true则显示)
        {key: "status", name: "主机状态", value: "Running", default: true, display: false}
    ];

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

    $scope.columns = [
        $scope.first,
        {value: "姓名", key: "name", width: "30%"},
        {value: "创建日期", key: "created"},
        {value: "来源", key: "source"},
        {value: "邮箱", key: "email", sort: false},// 不想排序的列，用sort: false
        {value: "", default: true}
    ];

    $scope.items = [
        {name: 'demo1', created: '2018-05-14', source: 'fit2cloud', email: 'demo1@fit2cloud.com'},
        {name: 'demo2', created: '2018-05-14', source: 'fit2cloud', email: 'demo2@fit2cloud.com'},
        {name: 'demo3', created: '2018-05-14', source: 'fit2cloud', email: 'demo3@fit2cloud.com'},
        {name: 'demo4', created: '2018-05-14', source: 'fit2cloud', email: 'demo4@fit2cloud.com'}
    ];

    $scope.showDetail = function (item) {
        // 点击2次关闭
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
            return;
        }
        $scope.selected = item.$$hashKey;
        $scope.detail = item;
        $scope.showInformation();
    };

    $scope.closeInformation = function () {
        $scope.selected = "";
        $scope.toggleInfoForm(false);
    };

    $scope.showInformation = function () {
        $scope.infoUrl = 'web-public/test/demo/information.html' + '?_t=' + window.appversion;
        $scope.toggleInfoForm(true);
    };

    $scope.create = function () {
        // $scope.formUrl用于side-form
        $scope.formUrl = 'web-public/test/demo/form.html' + '?_t=' + window.appversion;
        // toggleForm由side-form指令生成
        $scope.toggleForm();
    };

    $scope.save = function () {
        Notification.show("保存成功", function () {
            $scope.toggleForm();
        });
    };

    $scope.edit = function (item) {
        $scope.item = item;
        $scope.formUrl = 'web-public/test/demo/form.html' + '?_t=' + window.appversion;
        $scope.toggleForm();
    };

    $scope.openDialog = function (item, event) {
        $scope.item = item;
        $mdDialog.show({
            templateUrl: 'web-public/test/demo/dialog-form.html',
            parent: angular.element(document.body),
            scope: $scope,
            preserveScope: true,
            targetEvent: event,
            clickOutsideToClose: false
        }).then(function (answer) {
            $scope.status = 'You said the information was "' + answer + '".';
        }, function () {
            $scope.status = 'You cancelled the dialog.';
        });
    };

    $scope.closeDialog = function () {
        $mdDialog.cancel();
    };

    $scope.ok = function () {
        console.log("ok");
        $scope.closeDialog();
    };

    $scope.list = function (sortObj) {
        let condition = FilterSearch.convert($scope.filters);
        console.log(condition);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }

        Loading.add(HttpUtils.get("demo/test1/5000", function (response) {
            console.log(response);
            return response.data;
        }));
        Loading.add(HttpUtils.get("demo/test1/1000", function (response) {
            console.log(response);
            return response.data;
        }));
        // 多个查询用这种方式
        $scope.loadingLayer = Loading.load();

        // 同时获取全部结果
        $scope.loadingLayer.then(function (values) {
            console.log(values);
        });

        // 单个查询跟以前一样
        $scope.loadingLayer2 = HttpUtils.get("demo/test1/5", function (response) {
            console.log(response);
        })
    };

    // 分页DEMO
    // HttpUtils.paging($scope, "demo/list", {}, function (response) {
    //     console.log("callback function", response);
    // });

    $scope.help = function () {
        $scope.msg = "Bottom Sheep Demo";
        $mdBottomSheet.show({
            templateUrl: 'web-public/test/demo/bottom-sheet.html',
            scope: $scope,
            preserveScope: true
        }).then(function (clickedItem) {
            $scope.msg = clickedItem['name'] + ' clicked!';
        }).catch(function (error) {
            console.log(error)
            // User clicked outside or hit escape
        });
    }

});

TestApp.controller('MetricController', function ($scope) {
    let now = new Date().getTime();

    $scope.url = 'http://localhost:8080/server/metric/query';
    $scope.request = {
        startTime: now - 240 * 3600 * 1000,
        endTime: now,
        metricDataQueries: [
            {
                resourceId: '6d8f69b3-0355-4276-a624-4f57af9d0d85',
                resourceName: 'test',
                resourceType: 'VIRTUALMACHINE',
                stat: 'average',
                metric: 'CpuUsage'
            },
            {
                resourceId: '6d8f69b3-0355-4276-a624-4f57af9d0d85',
                resourceName: 'test',
                resourceType: 'VIRTUALMACHINE',
                stat: 'average',
                metric: 'CpuUsageInMhz'
            },
            {
                resourceId: '9430a5ff-00a3-4000-8b04-f3d8bdc7fadb',
                resourceName: 'test',
                resourceType: 'HOSTSYSTEM',
                stat: 'average',
                metric: 'HostCpuInMHZ'
            },
            {
                resourceId: '6d8f69b3-0355-4276-a624-4f57af9d0d85',
                resourceName: 'test',
                resourceType: 'VIRTUALMACHINE',
                stat: 'average',
                metric: 'MemoryUsage'
            },
            {
                resourceId: '6d8f69b3-0355-4276-a624-4f57af9d0d85',
                resourceName: 'test',
                resourceType: 'VIRTUALMACHINE',
                stat: 'average',
                metric: 'MemoryUsageInMB'
            },
            {
                resourceId: '9430a5ff-00a3-4000-8b04-f3d8bdc7fadb',
                resourceName: 'test',
                resourceType: 'HOSTSYSTEM',
                stat: 'average',
                metric: 'HostMemoryInMB'
            }
        ]
    }
});

TestApp.controller('WizardController', function ($scope, HttpUtils, Notification) {
    // 定义搜索条件
    $scope.conditions = [
        {
            key: "priority",
            name: "优先级[有查询，可多选]",
            directive: "filter-select-multiple", // 使用哪个指令
            selects: [
                {value: 1, label: "选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ],
            // 测试select类型条件的搜索框
            search: true
        }, {
            key: "priority",
            name: "优先级[有查询]",
            directive: "filter-select", // 使用哪个指令
            selects: [
                {value: 1, label: "选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ],
            // 测试select类型条件的搜索框
            search: true
        }, {
            key: "priority",
            name: "优先级[无查询]",
            directive: "filter-select",
            selects: [
                {value: 1, label: "选项1"},
                {value: 2, label: "选项2"},
                {value: 3, label: "选项3"},
                {value: 4, label: "选项4"},
                {value: 5, label: "选项5"},
                {value: 6, label: "选项6"},
                {value: 7, label: "选项7"},
                {value: 9, label: "其他"}
            ]
        },
        {key: "no", name: "工单编号", directive: "filter-input"},
        //查询虚机的条件
        {key: "instanceName", name: "实例名", directive: "filter-contains"},
        {key: "created", name: "创建日期", directive: "filter-date", directiveUnit: "second"},//directiveUnit: "second"返回时间戳为秒
        {key: "os", name: "操作系统", directive: "filter-contains"},
        {key: "localIp", name: "内网IP", directive: "filter-contains"},
        //增加一个异步字典转换的例子，将请求内容转换为value,label格式
        {
            key: "ajax",
            name: "异步字典",
            directive: "filter-select-ajax",
            url: "demo/status",
            convert: {value: "id", label: "name"}
        }

    ];

    // 用于传入后台的参数
    $scope.filters = [
        // 设置默认条件default:true(默认条件不会被删掉)，
        {key: "status", name: "主机状态", value: "Running", label: "运行中", default: true, operator: "="},
        {key: "status", name: "主机状态", value: "Running", default: true, operator: "="},
        {key: "status", name: "主机状态", value: "Running", operator: "="},
        // 可以设置是否显示(display:false不显示，不加display或者display:true则显示)
        {key: "status", name: "主机状态", value: "Running", default: true, display: false}
    ];

    $scope.columns = [
        {value: "姓名", key: "name", width: "30%"},
        {value: "创建日期", key: "created"},
        {value: "来源", key: "source"},
        {value: "邮箱", key: "email", sort: false},// 不想排序的列，用sort: false
        {value: "", default: true}
    ];

    $scope.items = [
        {name: 'demo1', created: '2018-05-14', source: 'fit2cloud', email: 'demo1@fit2cloud.com'},
        {name: 'demo2', created: '2018-05-14', source: 'fit2cloud', email: 'demo2@fit2cloud.com'},
        {name: 'demo3', created: '2018-05-14', source: 'fit2cloud', email: 'demo3@fit2cloud.com'},
        {name: 'demo4', created: '2018-05-14', source: 'fit2cloud', email: 'demo4@fit2cloud.com'}
    ];
    // 可用方法$scope.wizard.isLast()，$scope.wizard.isFirst()，$scope.wizard.isSelected()，$scope.wizard.continue()
    $scope.wizard = {
        setting: {
            title: "标题",
            subtitle: "子标题",
            closeText: "取消",
            submitText: "保存",
            nextText: "下一步",
            prevText: "上一步",
            buttons: [  // 去掉buttons，则显示submit按钮
                {
                    text: "自定义按钮",
                    class: "md-raised md-accent md-hue-2",
                    click: function () {
                        Notification.info("自定义按钮 click");
                    },
                    show: function () {
                        return $scope.wizard.isLast() || $scope.wizard.current === 2;
                    },
                    disabled: function () {
                        return $scope.wizard.current === 2;
                    }
                }
            ]
        },
        // 按顺序显示,id必须唯一并需要与页面中的id一致，select为分步初始化方法，next为下一步方法(最后一步时作为提交方法)
        steps: [
            {
                id: "1",
                name: "云帐号",
                select: function () {
                    console.log("第一步select")
                }
            }, {
                id: "2",
                name: "基础设置",
                select: function () {
                    console.log("第二步select")
                },
                next: function () {
                    console.log("第二步Next", $scope.wizard);
                    // 返回true则自动下一步
                    return true;
                }
            }, {
                id: "3",
                name: "异步验证",
                select: function () {
                    console.log("第三步select")
                },
                next: function () {
                    $scope.loadingLayer = HttpUtils.get("demo/test1/2000", function (response) {
                        console.log("第三步异步验证通过,验证时间：" + response.data);
                        $scope.wizard.continue();
                    });
                    // 返回false，则不会自动进行下一步
                    return false;
                }
            }, {
                id: "4",
                name: "权限设置",
                select: function () {
                    console.log("第四步select");
                },
                next: function () {
                    Notification.confirm("确定保存？", function () {
                        Notification.info("确定保存");
                    }, function () {
                        Notification.info("取消");
                    })
                }
            }
        ],
        // 嵌入页面需要指定关闭方法
        close: function () {
            $scope.show = false;
        }
    };

    $scope.pass = false;
    $scope.check = function () {
        $scope.pass = !$scope.pass;
    };

    $scope.show = true;

    $scope.open = function () {
        $scope.show = true;
    };
});

TestApp.controller('TreeController', function ($scope) {
    $scope.option = {
        select: "folder" //file, folder, all
    };
    $scope.node = {
        name: "一级",
        collapsed: false,
        children: [
            {
                name: "二级-1",
                children: [
                    {
                        name: "三级-1"
                    }, {
                        name: "三级-2"
                    }, {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }, {
                        name: "三级-5"
                    }, {
                        name: "三级-6"
                    }, {
                        name: "三级-7"
                    }, {
                        name: "三级-8"
                    }, {
                        name: "三级-9"
                    }, {
                        name: "三级-10"
                    }, {
                        name: "三级-11",
                        children: [
                            {
                                name: "四级-1"
                            }, {
                                name: "四级-2"
                            }, {
                                name: "四级-3"
                            }, {
                                name: "四级-4"
                            }, {
                                name: "四级-5"
                            }, {
                                name: "四级-6"
                            }, {
                                name: "四级-7"
                            }, {
                                name: "四级-8"
                            }, {
                                name: "四级-9"
                            }, {
                                name: "四级-9"
                            }, {
                                name: "四级-9"
                            }, {
                                name: "四级-9"
                            }, {
                                name: "四级-9"
                            }
                        ]
                    }
                ]
            }, {
                name: "二级-2"
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }, {
                name: "二级-3",
                children: [
                    {
                        name: "三级-3"
                    }, {
                        name: "三级-4"
                    }
                ]
            }
        ]
    };

    // 也可以用数组
    $scope.nodes = [
        {
            name: "一级-1",
            children: [
                {
                    name: "二级-1"
                }, {
                    name: "二级-2",
                    children: [
                        {
                            name: "三级-1"
                        }, {
                            name: "三级-2"
                        }, {
                            name: "三级-3"
                        }, {
                            name: "三级-4"
                        }, {
                            name: "三级-5"
                        }, {
                            name: "三级-6"
                        }, {
                            name: "三级-7"
                        }, {
                            name: "三级-8"
                        }, {
                            name: "三级-9"
                        }, {
                            name: "三级-10"
                        }, {
                            name: "三级-11"
                        }
                    ]
                }
            ]
        }, {
            name: "一级-2",
            children: []
        }, {
            name: "一级-3",
            children: [
                {
                    name: "二级-3"
                }, {
                    name: "二级-4"
                }
            ]
        }
    ];

    // 自动生成$scope.api.getSelected();
    $scope.radio = {
        selected: "",
        onChange: function (node) {
            console.log(node)
        }
    };

    // 自动生成$scope.api.getSelected();
    $scope.root = {
        onChange: function (node) {
            if (node.name === "三级-3") {
                let levelTwo = $scope.root.getNode("name", "二级-1");
                if (node.checked) {
                    $scope.root.toggle(levelTwo, true);
                    $scope.root.disable(levelTwo, true);
                } else {
                    $scope.root.toggle(levelTwo, false);
                    $scope.root.disable(levelTwo, false);
                }
            }
        }
    };

    $scope.noroot = {};

    $scope.getSelected = function () {
        console.log("带root", JSON.stringify($scope.root.getSelected(), 4));
        console.log("不带root", JSON.stringify($scope.noroot.getSelected(), 4));
    }
});

TestApp.controller('NotificationCtrl', function ($scope, Notification) {

    $scope.count = 1;
    $scope.show = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.show(msg);
    };

    $scope.info = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.info(msg);
    };

    $scope.success = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.success(msg);
    };

    $scope.warn = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.warn(msg);
    };

    $scope.danger = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.danger(msg);
    };

    $scope.confirm = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.confirm(msg, function () {
            // success do something;
        });
    };

    $scope.alert = function () {
        let msg = "消息通知" + $scope.count++;
        Notification.alert(msg);
    };

    $scope.prompt = function () {
        Notification.prompt({title: "标题", text: "文本"}, function (result) {
            console.log(result)
        });
    };
});

TestApp.controller('ChooseCtrl', function ($scope, HttpUtils) {
    $scope.items = [
        {id: 1, name: "长长长长长长长长长长长长长长长长长长长长长长长长长长长看不见"},
        {id: 2, name: "222"},
        {id: 3, name: "333"},
        {id: 4, name: "444"},
        {id: 5, name: "555"},
        {id: 6, name: "666"},
        {id: 7, name: "777"},
        {id: 8, name: "888"}
    ];

    $scope.loadingLayer = HttpUtils.get("demo/test1/2000", function () {
        $scope.selected = [3, 4];
        $scope.done = true;
    });

    $scope.selected2 = [
        {id: 1, name: "长长长长长长长长长长长长长长长长长长长长长长长长长长长看不见"},
        {id: 4, name: "444"}
    ];
});

TestApp.controller('DragCtrl', function ($scope) {
    $scope.items = [
        {id: 1, name: "111"},
        {id: 2, name: "222"},
        {id: 3, name: "333"},
        {id: 4, name: "444"},
        {id: 5, name: "555"},
        {id: 6, name: "666"},
        {id: 7, name: "777"},
        {id: 8, name: "888"}
    ];

    $scope.items2 = [
        {id: 1, name: "111"},
        {id: 2, name: "222"},
        {id: 3, name: "333"},
        {id: 4, name: "444"},
        {id: 5, name: "555"},
        {id: 6, name: "666"},
        {id: 7, name: "777"},
        {id: 8, name: "888"}
    ];
});

TestApp.controller('DemoController', function ($scope) {
    $scope.userIds = [];
    $scope.userIds.push("dongbin@fit2cloud.com");
    $scope.userIds.push("db@fit2cloud.com");
    $scope.userIds.push("db@fit2cloud");
});

TestApp.controller('SelectCtrl', function ($scope, $filter) {
    $scope.items = [];
    $scope.map = {};
    let i, max = 5000;
    for (i = 1; i <= max; i++) {
        $scope.items.push({
            id: i,
            name: "选项 ------------------------------------------------------------------------------------------------------------------" + i
        });
    }

    $scope.obj = {};
    $scope.obj.result1 = [330, 2];
    $scope.obj.result2 = [30, 31];
    $scope.obj.result3 = 223;

    $scope.change1 = function (result1) {
        console.log(result1);
    };

    $scope.change2 = function (result2) {
        console.log(result2);
    };


    // $scope.virtual = {
    //     name: "test", // 控件名，用于必填校验, 每个控件名不能相同
    //     value: "id", // 选项Value
    //     label: "name",  // 选项Label
    //     require: true, // 是否必填
    //     placeholder: "虚拟下拉测试", // 标题
    //     onChange: function (selected) { // change事件
    //         console.log(selected);
    //     },
    //     items: $scope.items, // 候选项
    //     results: [330, 2], // 结果
    // };

    $scope.addItems = function () {
        max += 1000;
        for (i; i <= max; i++) {
            $scope.items.push({
                id: i,
                name: "选项 ------------------------------------------------------------------------------------------------------------------" + i
            });
        }
    };

    $scope.removeItems = function () {
        $scope.items = $scope.items.splice(0, 10);
    };

    $scope.querySearch = function (searchText) {
        return $scope.items;
    };

});