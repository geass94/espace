package ge.boxwood.espace.controllers;


import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.smsservice.GeoSms.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/slave")
public class SlaveController {
    @Autowired
    private TokenHelper tokenHelper;
    @GetMapping("/charger/start/{chID}/{conID}")
    public ResponseEntity<?> start(@PathVariable("chID")Long cID, @PathVariable("conID")Long conID){
        HashMap start = new HashMap();
        start.put("status", 0);
        start.put("description", "");
        start.put("data", 2411);
        return ResponseEntity.ok(start);
    }

    @GetMapping("/charger/stop/{chID}/{trID}")
    public ResponseEntity<?> stop(@PathVariable("chID") Long cid, @PathVariable("trID") Long trid){
        HashMap stop = new HashMap();
        stop.put("status", 1);
        stop.put("description", "");
        stop.put("data", 2411);
        return ResponseEntity.ok(stop);
    }

    @GetMapping("/charger/info/{chID}")
    public ResponseEntity<?> info(@PathVariable("chID") Long cid){
        HashMap info = new HashMap();
        info.put("status", 0);
        info.put("description", "");
        HashMap data = new HashMap();
        data.put("id", 60);
        data.put("latitude", 43.12312);
        data.put("longitude", 43.53211);
        data.put("status", 0);
        data.put("type", 0);
        data.put("description", "test device");
        data.put("code", 4322);
        HashMap conns = new HashMap();
        ArrayList arr = new ArrayList();
        conns.put("type", "ChadeMo");
        conns.put("id", 1);
        arr.add(conns);
        data.put("connectors", arr);
        info.put("data", data);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/transaction/info/{trID}")
    public ResponseEntity<?> transaction(@PathVariable("trID")Long trID){
        HashMap resp = new HashMap();
        HashMap data = new HashMap();
        data.put("id", 2411);
        data.put("chargePointName", "espace-0009");
        data.put("chargePointCode", null);
        data.put("version", 9);
        data.put("uuidStart", "uuid:1ed8e7fd-642d-45d1-8313-c04b09cb2f47");
        data.put("uuidEnd", "");
        data.put("connectorId", 1);
        data.put("transStart", 1537432959751L);
        data.put("transStop", 1537435811861L);
        data.put("meterStart", 2869777);
        data.put("meterStop", 2872589);
        data.put("kiloWattHour", 25.8);
        data.put("consumed", 32532L);
        data.put("chargingTime", 2852110);
        resp.put("status", 0);
        resp.put("description", "");
        resp.put("data", data);
        return ResponseEntity.ok(resp);
    }


}
