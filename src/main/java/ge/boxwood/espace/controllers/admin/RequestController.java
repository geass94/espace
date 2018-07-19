package ge.boxwood.espace.controllers.admin;

import ge.boxwood.espace.models.*;
import ge.boxwood.espace.services.*;
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
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ChargerService chargerService;
    @Autowired
    private SettingsService settingsService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id")Long id){
        userService.destroy(id);
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

    @GetMapping("/payment/{order}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrderPayments(@PathVariable("order")String order){
        Order order1 = orderService.getOneByUUID(order);
        return ResponseEntity.ok(paymentService.getAllByOrder(order1));
    }

    @GetMapping("/chargers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getChargers(){
        return ResponseEntity.ok(chargerService.getAll());
    }

    @GetMapping("/chargers/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> chargerInfo(@RequestParam("chargerId") Long cid) throws Exception {
        Charger chargerInfo = chargerService.info(cid);
        if(chargerInfo != null){
            return ResponseEntity.ok(chargerInfo);
        }
        else{
            throw new RuntimeException("Something went wrong");
        }
    }

    @PostMapping("/chargers/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refreshChargers(){
        chargerService.refreshChargers();
        return ResponseEntity.ok(true);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> settings(){
        return ResponseEntity.ok(settingsService.getAll());
    }

    @PutMapping("/settings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSettings(@PathVariable("id")Long id, @RequestBody Settings settings){
        return ResponseEntity.ok(settingsService.update(id, settings));
    }
}
