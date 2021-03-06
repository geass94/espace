package ge.boxwood.espace.controllers;


import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.repositories.CategoryRepository;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.PricingService;
import ge.boxwood.espace.services.SettingsService;
import ge.boxwood.espace.services.smsservice.GeoSms.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping(path = "/slave", produces={ MediaType.APPLICATION_JSON_UTF8_VALUE })
public class SlaveController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PricingService pricingService;
    @Autowired
    private SettingsService settingsService;
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
        data.put("id", 26);
        data.put("latitude", 43.12312);
        data.put("longitude", 43.53211);
        data.put("status", 0);
        data.put("type", 0);
        data.put("description", "test device");
        data.put("code", 0026);
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
        Random r = new Random();
        int koef = r.nextInt((50 - 2) + 1) + 2;
        HashMap resp = new HashMap();
        HashMap data = new HashMap();
        data.put("id", 2411);
        data.put("chargePointName", "espace-0009");
        data.put("chargePointCode", 0026);
        data.put("version", 9);
        data.put("uuidStart", "uuid:1ed8e7fd-642d-45d1-8313-c04b09cb2f47");
        data.put("uuidEnd", null);
        data.put("connectorId", 1);
        data.put("transStart", 1537432959751L);
        data.put("transStop", null);
        data.put("meterStart", 2869777);
        data.put("meterStop", null);
        data.put("kiloWattHour", koef);
        data.put("consumed", null);
        data.put("chargingTime", null);
        resp.put("status", 0);
        resp.put("description", "");
        resp.put("data", data);
        return ResponseEntity.ok(resp);
    }


    @GetMapping("/categories")
    public ResponseEntity<?> categories(){
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/pricing")
    public ResponseEntity<?> pricing(){
        return ResponseEntity.ok(pricingService.getAll());
    }

    @GetMapping("/info")
    public ResponseEntity<?> info(){
        HashMap resp = new HashMap();
        resp.put("addressLine1", settingsService.getByKey("addressLine1").getValue());
        resp.put("addressLine2", settingsService.getByKey("addressLine2").getValue());
        resp.put("phoneNumber", settingsService.getByKey("phoneNumber").getValue());
        resp.put("email", settingsService.getByKey("email").getValue());
        resp.put("website", settingsService.getByKey("website").getValue());
        return ResponseEntity.ok(resp);
    }

}
