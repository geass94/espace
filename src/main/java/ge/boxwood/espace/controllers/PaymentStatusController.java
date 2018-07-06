package ge.boxwood.espace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PaymentStatusController {
    @RequestMapping(path = "/checkoutComplete", method = GET)
    public String completed(){
        return "completed.html";
    }

    @RequestMapping(path = "/checkoutFailed", method = GET)
    public String failed(){
        return "failed.html";
    }
}
