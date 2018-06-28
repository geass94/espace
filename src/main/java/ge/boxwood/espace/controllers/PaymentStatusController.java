package ge.boxwood.espace.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentStatusController {
    @GetMapping("/checkoutComplete")
    public String getCheckoutComplete(){
        return "checkoutComplete";
    }

    @GetMapping("/checkoutFailed")
    public String getCheckoutFailed(){
        return "checkoutFailed";
    }
}
