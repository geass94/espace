package ge.boxwood.espace.controllers;

import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.services.gc.GCPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class PaymentController {
    @Autowired
    private GCPaymentService GCPaymentService;
    @Autowired
    private StepLoggerService stepLoggerService;

    @GetMapping(path = "/payment/initiatePayment")
    public void initiatePayment(@RequestParam String orderId, HttpServletResponse response) throws IOException {
        HashMap params = new HashMap();
        params.put("paymentUUID", orderId);

        stepLoggerService.logStep("PaymentController", "initiatePayment", params);
        response.sendRedirect(GCPaymentService.initiatePayment(orderId, ""));
    }

    @GetMapping(path = "/payments/check", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String checkAvailable(@RequestParam Map<String, String> params) {
        stepLoggerService.logStep("PaymentController", "checkAvailable", (HashMap) params);
        return GCPaymentService.checkAvailable(params);
    }

    @GetMapping(path = "/payments/register", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String registerPayment(@RequestParam Map<String, String> params, HttpServletRequest httpRequest) {
        stepLoggerService.logStep("PaymentController", "registerPayment", (HashMap) params);
        return GCPaymentService.registerPayment(params, httpRequest);
    }
}