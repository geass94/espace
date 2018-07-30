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

ko.bindingHandlers['keyvalue'] = {
    makeTemplateValueAccessor: function(valueAccessor) {
        return function() {
            var values = valueAccessor();
            var array = [];
            for (var key in values)
                array.push({key: key, value: values[key]});
            return array;
        };
    },
    'init': function(element, valueAccessor, allBindings, viewModel, bindingContext) {
        return ko.bindingHandlers['foreach']['init'](element, ko.bindingHandlers['keyvalue'].makeTemplateValueAccessor(valueAccessor));
    },
    'update': function(element, valueAccessor, allBindings, viewModel, bindingContext) {
        return ko.bindingHandlers['foreach']['update'](element, ko.bindingHandlers['keyvalue'].makeTemplateValueAccessor(valueAccessor), allBindings, viewModel, bindingContext);
    }
};

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
    }
    var logsVM = new logsVMModel();
    logsVM.getlogs();
    ko.applyBindings(logsVM, $('#logs-page')[0]);

});

