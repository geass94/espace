package ge.boxwood.espace.controllers;

import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/charger")
public class ChargerController {
    @Autowired
    private ChargerService chargerService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private StepLoggerService stepLoggerService;

    @GetMapping("/free")
    public ResponseEntity<?> getFreeChargers(){
        HashMap params = new HashMap();
        stepLoggerService.logStep("ChargerController", "getFreeChargers", params);
        return ResponseEntity.ok(chargerService.freeChargers());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> addCharger(@RequestBody Charger charger){
        HashMap params = new HashMap();
        params.put("charger", charger);
        stepLoggerService.logStep("ChargerController","addCharger", params);
        return ResponseEntity.ok(chargerService.create(charger));
    }

    @GetMapping("/byCategories")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getChargersByCategories(){
        HashMap params = new HashMap();
        stepLoggerService.logStep("ChargerController",  "getChargersByCategories", params);
        return ResponseEntity.ok(chargerService.categories());
    }

    @GetMapping("/getByCode")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getChargerByCode(@RequestParam(value = "code")String code) throws Exception {
        HashMap params = new HashMap();
        params.put("code", code);
        stepLoggerService.logStep("ChargerController", "getChargerByCode", params);
        if(!code.isEmpty()){
            Charger charger = chargerService.getOneByCode(code);
            return ResponseEntity.ok(charger);
        }else{
            throw new RuntimeException("Code is empty!");
        }
    }

    @PostMapping("/preStart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> preStart(@RequestBody Map<String, String> data) throws Exception {
        Long chargerId = !data.get("chargerId").isEmpty() && !data.get("chargerId").equals(null) ? Long.valueOf(data.get("chargerId")) : 0L;
        Long connectorId = !data.get("connectorId").isEmpty() && !data.get("connectorId").equals(null) ? Long.valueOf(data.get("connectorId")) : 0L;
        Long cardId = !data.get("cardId").isEmpty() && !data.get("cardId").equals(null) ? Long.valueOf(data.get("cardId")) : 0L;
        float targetPrice = !data.get("targetPrice").isEmpty() && !data.get("targetPrice").equals(null) ? Float.valueOf(data.get("targetPrice")) : 0f;
        HashMap params = new HashMap();
        params.put("chargerId", chargerId);
        params.put("connectorId", connectorId);
        params.put("cardId", cardId);
        params.put("targetPrice", targetPrice);
        stepLoggerService.logStep("ChargerController", "preStart", params);
        HashMap dt = chargerService.preStart(chargerId, connectorId, cardId, targetPrice);
        return ResponseEntity.ok(dt);
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startCharging(@RequestBody Map<String, String> data) throws Exception {
        Long chargerId = !data.get("chargerId").isEmpty() && !data.get("chargerId").equals(null) ? Long.valueOf(data.get("chargerId")) : 0L;
        Long connectorId = !data.get("connectorId").isEmpty() && !data.get("connectorId").equals(null) ? Long.valueOf(data.get("connectorId")) : 0L;
        String paymentUUID = !data.get("paymentUUID").isEmpty() && !data.get("paymentUUID").equals(null) ? data.get("paymentUUID").toString() : "";
        HashMap params = new HashMap();
        params.put("chargerId", chargerId);
        params.put("connectorId", connectorId);
        params.put("paymentUUID", paymentUUID);
        stepLoggerService.logStep("ChargerController", "startCharging", params);
        ChargerInfoDTO dto = chargerService.start(chargerId, connectorId, paymentUUID);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> stopCharging(@RequestParam("chargerId") Long cid) throws Exception {
        HashMap params = new HashMap();
        params.put("chargerId", cid);
        stepLoggerService.logStep("ChargerController", "stopCharging", params);
        return ResponseEntity.ok(chargerService.stop(cid));
    }

    @GetMapping("/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity finishCharging(@RequestParam("trId")Long trId){
        HashMap params = new HashMap();
        params.put("trId", trId);
        stepLoggerService.logStep("ChargerController", "finishCharging", params);
        return ResponseEntity.ok(chargerService.finish(trId));
    }

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> chargerInfo(@RequestParam("chargerId") Long cid) throws Exception {
        HashMap params = new HashMap();
        params.put("chargerId", cid);
        stepLoggerService.logStep("ChargerController", "chargerInfo", params);
        Charger chargerInfo = chargerService.info(cid);
        if(chargerInfo != null){
            return ResponseEntity.ok(chargerInfo);
        }
        else{
            throw new RuntimeException("Something went wrong");
        }
    }

    @GetMapping("/transaction")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> chargerTransactionInfo(@RequestParam("trId") Long trid) throws Exception {
        HashMap params = new HashMap();
        params.put("trId", trid);
        stepLoggerService.logStep("ChargerController", "chargerTransactionInfo", params);
        ChargerInfoDTO chargerInfo = chargerService.transaction(trid);
        if(chargerInfo != null){
            return ResponseEntity.ok(chargerInfo);
        }
        else{
            throw new RuntimeException("Something went wrong");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAll(){
        HashMap params = new HashMap();
        stepLoggerService.logStep("ChargerController", "getAll", params);
        return ResponseEntity.ok(chargerService.getAll());
    }

    @PostMapping("/accident")
    public void accident(@PathVariable("cID") Long cID, @PathVariable("trID") Long trID){

    }

    @GetMapping("/closest")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getClosestChargers(@RequestParam("lat") double lat, @RequestParam("lng") double lng){
        HashMap params = new HashMap();
        params.put("latitude", lat);
        params.put("longitude", lng);
        stepLoggerService.logStep("ChargerController", "getClosestChargers", params);
        return ResponseEntity.ok( chargerService.getClosestChargers(lat, lng) );
    }

    @PutMapping("/edit/{cID}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateCharger(@PathVariable("cID") Long cid, @RequestBody Charger charger){
        HashMap params = new HashMap();
        params.put("chargerId", cid);
        params.put("charger", charger);
        stepLoggerService.logStep("ChargerController", "updateCharger", params);
        Charger charger1 = chargerService.update(charger, cid);
        if (charger1 != null)
        {
            return ResponseEntity.ok(charger1);
        }
        else
        {
            throw new RuntimeException("error while updating");
        }
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> importChargers(@RequestBody List<Charger> chargers){
        HashMap params = new HashMap();
        params.put("chargers", chargers);
        stepLoggerService.logStep("ChargerController", "importCargers", params);
        chargerService.importChargers(chargers);
        return ResponseEntity.ok("DONE");
    }
}
