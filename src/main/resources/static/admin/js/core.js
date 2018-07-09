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
            cfg.onSuccess(r);
        },
        error: function (r) {
            cfg.onError(r)
        },
        headers: {
            'Authorization':'Bearer '+window.localStorage.getItem("accessToken"),
            'Content-Type':'application/json'
        },
    })
}

function getURLParameters(paramName)
{
    var sURL = window.document.URL.toString();
    if (sURL.indexOf("?") > 0)
    {
        var arrParams = sURL.split("?");
        var arrURLParams = arrParams[1].split("&");
        var arrParamNames = new Array(arrURLParams.length);
        var arrParamValues = new Array(arrURLParams.length);

        var i = 0;
        for (i = 0; i<arrURLParams.length; i++)
        {
            var sParam =  arrURLParams[i].split("=");
            arrParamNames[i] = sParam[0];
            if (sParam[1] != "")
                arrParamValues[i] = unescape(sParam[1]);
            else
                arrParamValues[i] = "No Value";
        }

        for (i=0; i<arrURLParams.length; i++)
        {
            if (arrParamNames[i] == paramName)
            {
                //alert("Parameter:" + arrParamValues[i]);
                return arrParamValues[i];
            }
        }
        return "No Parameters Found";
    }
}

function logout() {
    window.localStorage.setItem("accessToken", "");
    window.localStorage.setItem("refreshToken", "");
    window.localStorage.setItem("active", "");
    window.localStorage.setItem("loggedIn", false);
    window.location.href = "/admin/index.html";
}