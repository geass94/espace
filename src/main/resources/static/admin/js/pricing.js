var pricingsVMModel = function () {
    var self = this;
    self.pricings = ko.observableArray([]);

    self.pricingModel = {
        rangeStart: ko.observable(),
        rangeEnd: ko.observable(),
        price: ko.observable(),
        name: ko.observable(),
    }

    self.getPricings = function () {
        request({
            url: '/api/admin/pricing',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.pricings.removeAll();
                ko.utils.arrayPushAll(self.pricings, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

    self.updatePricing = function (parent) {
        request({
            url: '/api/admin/pricing/'+parent.id,
            method: 'PUT',
            data: ko.toJS( parent ),
            preventDataConversion: false,
            onSuccess: function () {
                self.getPricings();
            }
        })
    }

    self.createPricing = function () {
        request({
            url: '/api/admin/pricing',
            method: 'POST',
            data: ko.toJS( self.pricingModel ),
            preventDataConversion: false,
            onSuccess: function () {
                self.getPricings();
            }
        })
    }

    self.deletePricing = function (parent) {
        console.log(parent)
        request({
            url: '/api/admin/pricing/'+parent.id,
            method: 'DELETE',
            preventDataConversion: false,
            onSuccess: function () {
                self.getPricings();
            }
        })
    }
}

$(document).ready(function () {
    console.log(window.localStorage.getItem("loggedIn"))
    var pricingsVM = new pricingsVMModel();
    pricingsVM.getPricings();
    ko.applyBindings(pricingsVM, $('#pricings-page')[0]);
});

if ( window.localStorage.getItem("loggedIn") === false ){
    window.location.href = "/admin/index.html";
}