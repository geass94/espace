package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.*;
import ge.boxwood.espace.services.ChargerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startCharging(@RequestParam(value = "chargerId")Long cid, @RequestParam(value = "connectorId")Long conid, @RequestParam(value = "cardId", required = false, defaultValue = "0")Long cardid) throws Exception {
        cardid = 0L;
        cardid = Long.valueOf(cardid);
        ChargerInfoDTO chargerInfo = chargerService.start(cid, conid, cardid);
        return ResponseEntity.ok(chargerInfo);
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startChargingByCode(@RequestBody Map<String, String> data) throws Exception {
        String code = data.get("code");
        String cardId = data.get("cardId");
        Charger charger = chargerService.getOneByCode(code);
        if (charger.getConnectors().size() > 1) {
            return ResponseEntity.ok(charger);
        }
        else {
            Long conId = charger.getConnectors().get(0).getConnectorId();
            ChargerInfoDTO chargerInfo = chargerService.start(charger.getChargerId(), conId, Long.valueOf(cardId));
            return ResponseEntity.ok(chargerInfo);
        }

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
