package ge.boxwood.espace.services.payments;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    String initiatePayment(String orderId, String trxId);
    String checkAvailable(Map<String, String> params);
    String registerPayment(Map<String, String> params, HttpServletRequest httpRequest);
}
