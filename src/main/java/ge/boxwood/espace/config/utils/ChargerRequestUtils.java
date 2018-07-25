package ge.boxwood.espace.config.utils;

import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.services.SettingsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Component
@DependsOn(value = { "settingsServiceImpl" })
public class ChargerRequestUtils {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private StepLoggerService stepLoggerService;
    private final String USER_AGENT = "Mozilla/5.0";
    private final String CHARSET = "UTF-8";
    private String SERVICE_URL;

    @PostConstruct
    public void init() {
        SERVICE_URL = settingsService.getByKey("chargerServiceUrl").getValue();
    }
    public JSONObject start(Long cid, Long conid) throws Exception {
        URL obj = new URL(SERVICE_URL+"/charger/start/"+cid+"/"+conid);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", CHARSET);

        HashMap params = new HashMap();
        params.put("URL", obj.toString());
        params.put("chargerId", cid);
        params.put("connectorId", conid);

        stepLoggerService.logStep("ChargerReqeustUtils", "start", params);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String json = response.toString();
        JSONObject jsonObj = new JSONObject(json);
        jsonObj.put("responseCode", con.getResponseCode());
        return jsonObj;
    }

    public JSONObject stop(Long cid, Long trid) throws Exception{
        URL obj = new URL(this.SERVICE_URL+"/charger/stop/"+cid+"/"+trid);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", CHARSET);

        HashMap params = new HashMap();
        params.put("URL", obj.toString());
        params.put("chargerId", cid);
        params.put("trId", trid);

        stepLoggerService.logStep("ChargerReqeustUtils", "stop", params);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String json = response.toString();
        JSONObject jsonObj = new JSONObject(json);
        jsonObj.put("responseCode", con.getResponseCode());
        return jsonObj;
    }

    public JSONObject info(Long cid) throws Exception {
        URL obj = new URL(this.SERVICE_URL+"/charger/info/"+cid);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", CHARSET);

        HashMap params = new HashMap();
        params.put("URL", obj.toString());
        params.put("chargerId", cid);

        stepLoggerService.logStep("ChargerReqeustUtils", "info", params);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String json = response.toString();
        JSONObject jsonObj = new JSONObject(json);
        jsonObj.put("responseCode", con.getResponseCode());
        return jsonObj;
    }

    public JSONObject transaction(Long trid) throws Exception {
        URL obj = new URL(this.SERVICE_URL+"/transaction/info/"+trid);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", CHARSET);

        HashMap params = new HashMap();
        params.put("URL", obj.toString());
        params.put("trId", trid);

        stepLoggerService.logStep("ChargerReqeustUtils", "transaction", params);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        String json = response.toString();
        JSONObject jsonObj = new JSONObject(json);
        jsonObj.put("responseCode",con.getResponseCode() );
        return jsonObj;
    }

    public JSONObject all() throws Exception {
        URL obj = new URL(this.SERVICE_URL+"/chargers");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", CHARSET);

        HashMap params = new HashMap();
        params.put("URL", obj.toString());

        stepLoggerService.logStep("ChargerReqeustUtils", "all", params);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String json = response.toString();
        JSONObject jsonObj = new JSONObject(json);
        jsonObj.put("responseCode", con.getResponseCode());
        return jsonObj;
    }
}
