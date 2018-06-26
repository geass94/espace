package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.Place;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.services.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/place")
public class PlaceController {
    private PlaceService placeService;
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(placeService.getAll());
    }

    @GetMapping("/closest")
    public ResponseEntity<?> getClosest(@RequestParam("lat") double lat, @RequestParam("lng") double lng){
        return ResponseEntity.ok(placeService.getClosestPlaces(lat, lng));
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> addPlace(@RequestBody Place place){
        place.setStatus(Status.ACTIVE);
        return ResponseEntity.ok(placeService.create(place));
    }
}
