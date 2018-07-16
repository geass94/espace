package ge.boxwood.espace.services.gc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface GCPaymentService {
    String initiatePayment(String orderId, String trxId);
    String checkAvailable(Map<String, String> params);
    String registerPayment(Map<String, String> params, HttpServletRequest httpRequest);
    String makeRefund(String paymentUUID, Float targetPrice, Float currentPrice, String trxId, String prnn);
}
