package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.services.payments.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class PaymentController {
    @Autowired
    private PaymentRepository paymentsRepo;

    @Autowired
    private PaymentService paymentService;

    @GetMapping(path = "/payment/initiatePayment")
    public void initiatePayment(@RequestParam String orderId, HttpServletResponse response) throws IOException {
        response.sendRedirect(paymentService.initiatePayment(orderId, ""));
    }

    @GetMapping(path = "/payments/check", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String checkAvailable(@RequestParam Map<String, String> params) {
        return paymentService.checkAvailable(params);
    }

    @GetMapping(path = "/payments/register", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String registerPayment(@RequestParam Map<String, String> params, HttpServletRequest httpRequest) {
        return paymentService.registerPayment(params, httpRequest);
    }


    @RequestMapping("/confirmpayment")
    @ResponseBody
    public boolean confirmPayment(@RequestParam(value = "token", required = true, defaultValue = "") String token,
                                  @RequestParam(value = "id", required = true, defaultValue = "") String id) {
        Payment payment = paymentsRepo.findByUuid(id);
        payment.confirm();
        paymentsRepo.save(payment);
        return true;
    }
}