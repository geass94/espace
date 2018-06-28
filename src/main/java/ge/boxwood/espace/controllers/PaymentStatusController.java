package ge.boxwood.espace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentStatusController {
    @GetMapping("/checkoutComplete")
    public ModelAndView getCheckoutComplete(){
        return new ModelAndView("checkoutComplete");
    }

    @GetMapping("/checkoutFailed")
    public ModelAndView getCheckoutFailed(){
        return new ModelAndView("checkoutFailed");
    }
}
