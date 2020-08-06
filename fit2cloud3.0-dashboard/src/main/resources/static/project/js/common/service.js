ProjectApp.service('operationArr', function () {
    this.removeByValue = function (arr, val) {
        for (let i = 0; i < arr.length; i++) {
            if (arr[i] === val) {
                arr.splice(i, 1);
                break;
            }
        }
    };
});

ProjectApp.service('DashBoardHttpUtils', function ($scope, HttpUtils) {

    this.dashboardRequest = function (data, success) {
        if (data.method === 'GET') {
            HttpUtils.get(data.requestUrl, function (response) {
                success(response);
            });
        } else {
            HttpUtils.post(data.requestUrl, data.param, function (response) {
                success(response);
            });
        }
    }
});