package ge.boxwood.espace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
