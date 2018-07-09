var usersVMModel = function () {
    var self = this;
    self.users = ko.observableArray([]);
    self.getUsers = function () {
        request({
            url: '/api/admin/users',
            method: 'GET',
            preventDataConversion: true,
            onSuccess: function (data) {
                self.users.removeAll();
                ko.utils.arrayPushAll(self.users, data)
            },
            onError: function (data) {
                console.log(data)
            }
        });
    }

    self.deleteUser = function (parent) {
        request({
            url: '/api/admin/user/'+parent.id,
            method: 'DELETE',
            preventDataConversion: false,
            onSuccess: function () {
                self.getUsers();
            }
        })
    }

    self.updateUser = function (parent) {
        request({
            url: '/api/admin/user/'+parent.id,
            method: 'PUT',
            data: ko.toJS( parent ),
            preventDataConversion: false,
            onSuccess: function () {
                self.getUsers();
            }
        })
    }
}

$(document).ready(function () {
    console.log( window.localStorage.getItem("loggedIn") )
    if ( window.localStorage.getItem("loggedIn") === "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/index.html";
        alert()
    }
    var usersVM = new usersVMModel();
    usersVM.getUsers();
    ko.applyBindings(usersVM, $('#users-page')[0]);

});

