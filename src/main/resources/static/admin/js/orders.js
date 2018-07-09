var ordersVMModel = function () {
    var self = this;
    self.orders = ko.observableArray([]);
    self.getOrders = function () {
        request({
            url: '/api/admin/orders',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.orders.removeAll();
                ko.utils.arrayPushAll(self.orders, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

    self.confirmOrder = function (parent) {
        // request({
        //     url: '/api/admin/order/'+parent.id,
        //     method: 'DELETE',
        //     preventDataConversion: false,
        //     onSuccess: function () {
        //         self.getOrders();
        //     }
        // })
    }
}

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
    }
    var ordersVM = new ordersVMModel();
    ordersVM.getOrders();
    ko.applyBindings(ordersVM, $('#orders-page')[0]);

});

