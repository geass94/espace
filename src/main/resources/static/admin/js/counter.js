var countersVMModel = function () {
    var self = this;
    self.counters = ko.observableArray([]);
    self.getOrders = function () {
        request({
            url: '/api/admin/counter/'+getURLParameters("trid"),
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.counters.removeAll();
                ko.utils.arrayPushAll(self.counters, data)
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
    var countersVM = new countersVMModel();
    countersVM.getOrders();
    ko.applyBindings(countersVM, $('#counter-page')[0]);

});

