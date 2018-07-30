var logsVMModel = function () {
    var self = this;
    self.logs = ko.observableArray([]);

    self.logModel = {
        rangeStart: ko.observable(),
        rangeEnd: ko.observable(),
        price: ko.observable(),
        name: ko.observable(),
    }

    self.getlogs = function () {
        request({
            url: '/api/admin/logs',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.logs.removeAll();
                ko.utils.arrayPushAll(self.logs, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

}

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
    }
    var logsVM = new logsVMModel();
    logsVM.getlogs();
    ko.applyBindings(logsVM, $('#logs-page')[0]);

});

