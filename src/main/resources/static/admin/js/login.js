var loginModel = function () {
    var self = this;
    self.username = ko.observable();
    self.password = ko.observable()
    self.active = ko.observable();
    self.login = function () {
        console.log(self.username())
        request({
            url : "/auth/login",
            data : { username: self.username(), password: self.password() },
            preventDataConversion: false,
            onSuccess: function (data) {
                console.log(data)
                window.localStorage.setItem("accessToken", data.access_token);
                window.localStorage.setItem("refreshToken", data.refresh_token);
                window.localStorage.setItem("active", data.userActivated);
                window.localStorage.setItem("loggedIn", true);
                window.location.reload();
            }
        })
    }
}

$(document).ready(function () {
    if ( window.localStorage.getItem("loggedIn") !== "false" && window.localStorage.getItem("loggedIn") != null && typeof window.localStorage.getItem("loggedIn") != "undefined"){
        window.location.href = "/admin/pricing.html";
    }

    var loginVM = new loginModel();
    ko.applyBindings(loginVM, $('#auth-page')[0]);

});

