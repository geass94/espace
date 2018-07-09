package ge.boxwood.espace.config.utils;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ChargerRequestUtils {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String SERVICE_URL = "http://devel.ge:9090/es-services/mobile/ws";
//        private final String SERVICE_URL = "http://localhost:8443/slave";
//    private final String SERVICE_URL = "https://api.e-space.ge/slave";
    public JSONObject start(Long cid, Long conid) throws Exception {
        URL obj = new URL(SERVICE_URL+"/charger/start/"+cid+"/"+conid);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");

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
        URL obj = new URL(SERVICE_URL+"/charger/stop/"+cid+"/"+trid);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");
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
        URL obj = new URL(SERVICE_URL+"/charger/info/"+cid);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");

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
        URL obj = new URL(SERVICE_URL+"/transaction/info/"+trid);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");

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
        URL obj = new URL(SERVICE_URL+"/chargers");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");

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
