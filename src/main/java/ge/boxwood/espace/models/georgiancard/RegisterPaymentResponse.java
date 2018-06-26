package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "register-payment-response")
public class RegisterPaymentResponse {
    @JacksonXmlProperty(localName = "result")
    public PaymentResult result = new PaymentResult();
}
