package ge.boxwood.espace.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.ChargerInfo;
import ge.boxwood.espace.services.ChargerService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


@Component
public class ChargerRequestUtils {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String SERVICE_URL = "http://localhost:8443/slave";

    public JSONObject start(Long cid, Long conid) throws Exception {
        URL obj = new URL(SERVICE_URL+"/start/"+cid+"/"+conid);

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

    public JSONObject stop(Long cid, int trid) throws Exception{
        URL obj = new URL(SERVICE_URL+"/stop/"+cid+"/"+trid);

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

    public String info(Long cid) throws Exception {
        URL obj = new URL("http://localhost:8082/apiV1/start");
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

        return response.toString();
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
}
