package ge.boxwood.espace.config.utils;

import ge.boxwood.espace.models.ChargerInfo;
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
    private final String SERVICE_URL = "http://chargers.e-space.ge:8080/ws";

    public ChargerInfo start(Long cid) throws Exception {
        URL obj = new URL(SERVICE_URL+"/charger/start/"+cid);
        ChargerInfo chargerInfo = new ChargerInfo();

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
        chargerInfo.setResponseCode(con.getResponseCode());
        chargerInfo.setChargerTransactionId(response.toString());
        return chargerInfo;
    }

    public ChargerInfo stop(Long cid, int trid) throws Exception{
        URL obj = new URL(SERVICE_URL+"/charger/stop/"+cid+"/"+trid);
        ChargerInfo chargerInfo = new ChargerInfo();

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

        chargerInfo.setResponseCode(con.getResponseCode());
        chargerInfo.setChargerTransactionId(response.toString());

        return chargerInfo;
    }

    public String info(Long cid) throws Exception {
//        URL obj = new URL(SERVICE_URL+"/charger/info/"+cid);
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
}
