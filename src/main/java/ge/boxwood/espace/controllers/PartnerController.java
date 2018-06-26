package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.Partner;
import ge.boxwood.espace.services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partner")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Partner partner){
        return ResponseEntity.ok(partnerService.create(partner));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@RequestBody Partner partner, @PathVariable("id") Long id){

        return ResponseEntity.ok(partnerService.update(partner, id));
    }
    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(partnerService.getAll());
    }
}
