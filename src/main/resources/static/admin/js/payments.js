var paymentsVMModel = function () {
    var self = this;
    self.payments = ko.observableArray([]);
    self.getOrders = function () {
        request({
            url: '/api/admin/payment/'+getURLParameters("order"),
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.payments.removeAll();
                ko.utils.arrayPushAll(self.payments, data)
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
    var paymentsVM = new paymentsVMModel();
    paymentsVM.getOrders();
    ko.applyBindings(paymentsVM, $('#payment-page')[0]);

});

