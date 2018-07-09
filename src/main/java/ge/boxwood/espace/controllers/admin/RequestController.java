package ge.boxwood.espace.controllers.admin;

import ge.boxwood.espace.services.PricingService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RequestController {
    @Autowired
    private UserService userService;
    @Autowired
    private PricingService pricingService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> pricingList(){
        return ResponseEntity.ok(pricingService.getAll());
    }


}
