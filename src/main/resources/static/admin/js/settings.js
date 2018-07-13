var settingsVMModel = function () {
    var self = this;
    self.settings = ko.observableArray([]);

    self.settingModel = {
        rangeStart: ko.observable(),
        rangeEnd: ko.observable(),
        price: ko.observable(),
        name: ko.observable(),
    }

    self.getSettings = function () {
        request({
            url: '/api/admin/settings',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.settings.removeAll();
                ko.utils.arrayPushAll(self.settings, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

    self.updateSetting = function (parent) {
        request({
            url: '/api/admin/settings/'+parent.id,
            method: 'PUT',
            data: ko.toJS( parent ),
            preventDataConversion: false,
            onSuccess: function () {
                self.getSettings();
            }
        })
    }
    
}

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
    }
    var settingsVM = new settingsVMModel();
    settingsVM.getSettings();
    ko.applyBindings(settingsVM, $('#settings-page')[0]);

});

