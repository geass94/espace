function request(cfg) {

    cfg.data = cfg.data || {};

    if (cfg.loadingEl) $('.' + cfg.loadingEl).addClass('loading-mask');

    $.ajax({
        type: cfg.method || 'POST',
        url: cfg.url,
        dataType: cfg.dataType || 'json',
        contentType: cfg.contentType === undefined ? cfg.contentType || 'application/json' : cfg.contentType,
        data: cfg.preventDataConversion ? cfg.data : JSON.stringify(cfg.data || {}),
        processData: cfg.processData || false,
        xhr: cfg.xhr,
        async: cfg.async || false,
        success: function (r) {
            if (cfg.loadingEl) $('.' + cfg.loadingEl).removeClass('loading-mask');

            if (r.errorCode != 0) {
                this.error(r);
            } else {
                if (cfg.onSuccess) {
                    cfg.onSuccess(r.data);
                }
            }
        },
        error: function (r) {
            if (cfg.loadingEl) $('.' + cfg.loadingEl).removeClass('loading-mask');
            // if (!cfg.preventDefaultError) onError();
            if (cfg.onError) {
                cfg.onError(r)
            } else {
                alertMessage(r.message);
            }
        }
    })
}


var self = this;
self.storage = window.localStorage;

self.access_token = "";
self.refresh_token = ""

self.getToken = function () {

}

self.users = ko.observabelArray([]);
self.login = function (username, password) {
    request({
        url : "https://api.e-space.ge/auth/login",
        data : { username: username, password: password },
        processData: false,
        success: function (data) {
            self.refresh_token = data.refresh_token;
            self.access_token = data.access_token;
        }
    })
}


self.getUsers = function () {
    request({
        url : "https://api.e-space.ge/auth/login",
        method : "GET",
        processData: false,
        success: function (data) {
            self.refresh_token = data.refresh_token;
            self.access_token = data.access_token;
        }
    })
}