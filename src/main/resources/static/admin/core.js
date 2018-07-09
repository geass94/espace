var storage = window.localStorage;
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
                alert(r.message);
            }
        },
        headers: {
            'Authorization':'Bearer '+storage.getItem("accessToken"),
            'Content-Type':'application/json'
        },
    })
}
