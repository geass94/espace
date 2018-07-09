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
    console.log(window.localStorage.getItem("loggedIn"))
    var countersVM = new countersVMModel();
    countersVM.getOrders();
    ko.applyBindings(countersVM, $('#counter-page')[0]);
});

if ( window.localStorage.getItem("loggedIn") === false ){
    window.location.href = "/admin/index.html";
}