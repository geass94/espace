package ge.boxwood.espace.controllers;


import ge.boxwood.espace.security.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/slave")
public class SlaveController {
    @Autowired
    private TokenHelper tokenHelper;
    @GetMapping("/start/{chID}/{conID}")
    public ResponseEntity<?> start(@PathVariable("chID")Long cID, @PathVariable("conID")Long conID){
        HashMap resp = new HashMap();
        resp.put("status", 0);
        resp.put("description", "");
        resp.put("data", 2451);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/transaction/info/{trID}")
    public ResponseEntity<?> transaction(@PathVariable("trID")Long trID){
        HashMap resp = new HashMap();
        HashMap data = new HashMap();
        data.put("id", 7);
        data.put("chargePointName", "espace-0009");
        data.put("chargePointCode", null);
        data.put("version", 9);
        data.put("uuidStart", "uuid:1ed8e7fd-642d-45d1-8313-c04b09cb2f47");
        data.put("uuidEnd", "uuid:f20aeaad-1576-48b6-ab85-021c16997585");
        data.put("connectorId", 1);
        data.put("transStart", 1537432959751L);
        data.put("transStop", 1537435811861L);
        data.put("meterStart", 2869777);
        data.put("meterStop", 2872589);
        resp.put("status", 0);
        resp.put("description", "");
        resp.put("data", data);
        return ResponseEntity.ok(resp);
    }
}
