package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.*;
import ge.boxwood.espace.services.ChargerService;
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

    @GetMapping("/free")
    public ResponseEntity<?> getFreeChargers(){
        return ResponseEntity.ok(chargerService.freeChargers());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> addCharger(@RequestBody Charger charger){
        return ResponseEntity.ok(chargerService.create(charger));
    }

    @GetMapping("/byCategories")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getChargersByCategories(){
        return ResponseEntity.ok(chargerService.categories());
    }

    @GetMapping("/getByCode")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startCharging(@RequestParam(value = "code")String code) throws Exception {
        if(!code.isEmpty()){
            Charger charger = chargerService.getOneByCode(code);
            return ResponseEntity.ok(charger);
        }else{
            throw new RuntimeException("Code is empty!");
        }
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> preStart(@RequestBody Map<String, String> data) throws Exception {
        Long chargerId = !data.get("chargerId").isEmpty() && data.get("chargerId") != null ? Long.valueOf(data.get("chargerId")) : 0L;
        Long connectorId = !data.get("connectorId").isEmpty() && data.get("connectorId") != null ? Long.valueOf(data.get("connectorId")) : 0L;
        Long cardId = !data.get("cardId").isEmpty() && data.get("cardId") != null ? Long.valueOf(data.get("cardId")) : 0L;
        float targetPrice = !data.get("targetPrice").isEmpty() && data.get("targetPrice") != null ? Float.valueOf(data.get("targetPrice")) : 0f;

        HashMap dt = chargerService.preStart(chargerId, connectorId, cardId, targetPrice);
        return ResponseEntity.ok(dt);
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startChargingByCode(@RequestBody Map<String, String> data) throws Exception {
        Long chargerId = !data.get("chargerId").isEmpty() && data.get("chargerId") != null ? Long.valueOf(data.get("chargerId")) : 0L;
        Long connectorId = !data.get("connectorId").isEmpty() && data.get("connectorId") != null ? Long.valueOf(data.get("connectorId")) : 0L;
        String orderUUID = !data.get("orderUUID").isEmpty() && data.get("orderUUID") != null ? data.get("orderUUID").toString() : "";
        ChargerInfoDTO dto = chargerService.start(chargerId, connectorId, orderUUID);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> stopCharging(@RequestParam("chargerId") Long cid) throws Exception {
        ChargerInfoDTO chargerInfo = chargerService.stop(cid);
        return ResponseEntity.ok(chargerInfo);
    }

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> chargerInfo(@RequestParam("chargerId") Long cid) throws Exception {
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
        return ResponseEntity.ok(chargerService.getAll());
    }

    @PostMapping("/accident")
    public void accident(@PathVariable("cID") Long cID, @PathVariable("trID") Long trID){

    }

    @GetMapping("/closest")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getClosestChargers(@RequestParam("lat") double lat, @RequestParam("lng") double lng){
        return ResponseEntity.ok( chargerService.getClosestChargers(lat, lng) );
    }

    @PutMapping("/edit/{cID}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updateCharger(@PathVariable("cID") Long cid, @RequestBody Charger charger){
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
        chargerService.importChargers(chargers);
        return ResponseEntity.ok("DONE");
    }
}
