var chargersVMModel = function () {
    var self = this;
    self.chargers = ko.observableArray([]);

    self.chargersModel = {
        rangeStart: ko.observable(),
        rangeEnd: ko.observable(),
        price: ko.observable(),
        name: ko.observable(),
    }

    self.getChargers = function () {
        request({
            url: '/api/admin/chargers',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.chargers.removeAll();
                ko.utils.arrayPushAll(self.chargers, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

    self.syncCharger = function (parent) {
        request({
            url: '/api/admin/chargers/info?chargerId='+parent.chargerId,
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function () {
                self.getChargers();
            }
        })
    }
}

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
    }
    var chargersVM = new chargersVMModel();
    chargersVM.getChargers();
    ko.applyBindings(chargersVM, $('#chargers-page')[0]);

});

