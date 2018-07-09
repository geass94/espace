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
    console.log(window.localStorage.getItem("loggedIn"))
    var ordersVM = new ordersVMModel();
    ordersVM.getOrders();
    ko.applyBindings(ordersVM, $('#orders-page')[0]);
});

if ( window.localStorage.getItem("loggedIn") === false ){
    window.location.href = "/admin/index.html";
}