package ge.boxwood.espace.controllers;

import ge.boxwood.espace.config.utils.ChargerRequestUtils;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.PlaceService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/charger")
public class ChargerController {
    @Autowired
    private ChargerService chargerService;
    @Autowired
    private PlaceService placeService;

    @GetMapping("/free")
    public ResponseEntity<?> getFreeChargers(){
        return ResponseEntity.ok(chargerService.freeChargers());
    }

    @GetMapping("/start/{chargerID}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> startCharging(@PathVariable("chargerID") Long cid) throws Exception {
        ChargerInfo chargerInfo = chargerService.start(cid);
        return ResponseEntity.ok(chargerInfo);
    }

    @GetMapping("/stop/{chargerID}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> stopCharging(@PathVariable("chargerID") Long cid) throws Exception {
        ChargerInfo chargerInfo = chargerService.stop(cid);
        return ResponseEntity.ok(chargerInfo);
    }

    @GetMapping("/info/{chargerID}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> chargerInfo(@PathVariable("chargerID") Long cid) throws Exception {
        Charger chargerInfo = chargerService.info(cid);
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

    @GetMapping("/closest/by-charger")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getClosestChargers(@RequestParam("lat") double lat, @RequestParam("lng") double lng){
        return ResponseEntity.ok( chargerService.getClosestChargers(lat, lng) );
    }

    @GetMapping("/closest/by-place")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getClosestChargersByPlace(@RequestParam("lat") double lat, @RequestParam("lng") double lng){
        List<Place> placeList = placeService.getClosestPlaces(lat, lng);
        List<Charger> chargerList = chargerService.getAllChargersByClosestPlaces(placeList);
        return ResponseEntity.ok( chargerList );
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
}
