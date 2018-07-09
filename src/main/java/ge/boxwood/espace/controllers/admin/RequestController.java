package ge.boxwood.espace.controllers.admin;

import ge.boxwood.espace.models.Pricing;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.services.CounterService;
import ge.boxwood.espace.services.OrderService;
import ge.boxwood.espace.services.PricingService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class RequestController {
    @Autowired
    private UserService userService;
    @Autowired
    private PricingService pricingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CounterService counterService;
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id")Long id){
        userService.delete(id);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id")Long id, @RequestBody User user){
        User user1 = userService.update(user, id);
        return ResponseEntity.ok(user1);
    }


    @GetMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> pricingList(){
        return ResponseEntity.ok(pricingService.getAll());
    }

    @PutMapping("/pricing/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePricing(@PathVariable("id")Long id, @RequestBody Pricing pricing){
        Pricing pricing1 = pricingService.update(pricing, id);
        return ResponseEntity.ok(pricing1);
    }

    @PostMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPricing(@RequestBody Pricing pricing){
        Pricing pricing1 = pricingService.create(pricing);
        return ResponseEntity.ok(pricing1);
    }

    @DeleteMapping("/pricing/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePricing(@PathVariable("id")Long id){
        pricingService.delete(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrders(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/counter/{trid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCoutnersByTrId(@PathVariable("trid")String trid){
        return ResponseEntity.ok(counterService.getAllCounterByTrId(trid));
    }
}
