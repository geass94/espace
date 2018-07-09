var loginModel = function () {
    var self = this;
    self.username = ko.observable();
    self.password = ko.observable()

    self.login = function () {
        console.log(self.username())
        request({
            url : "https://api.e-space.ge/auth/login",
            data : { username: self.username, password: self.password },
            processData: false,
            success: function (data) {
                storage.setItem("accessToken", data.access_token)
                storage.setItem("refreshToken", data.refresh_token)
            }
        })
    }
}


$(document).ready(function () {
    var loginVM = new loginModel();
    ko.applyBindings(loginVM, $('#auth-page')[0]);
});