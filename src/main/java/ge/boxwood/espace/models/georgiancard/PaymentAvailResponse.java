package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "payment-avail-response")
public class PaymentAvailResponse {

    @JacksonXmlProperty(localName = "result")
    public PaymentResult result = new PaymentResult();

    @JacksonXmlProperty(localName = "merchant-trx")
    public String merchantTrx;

    @JacksonXmlProperty(localName = "primaryTrxPcid")
    public String primaryTrxPcid;

    @JacksonXmlProperty(localName = "submerchant")
    public String subMerchant;

    @JacksonXmlProperty(localName = "card")
    public Card card = new Card();

    @JacksonXmlProperty(localName = "transaction-type")
    public String transactionType;

    @JacksonXmlProperty(localName = "purchase")
    public Purchase purchase = new Purchase();

    @JacksonXmlElementWrapper(localName = "order-params")
    @JacksonXmlProperty(localName = "param")
    public List<OrderParam> orderParams = new ArrayList<>();
}
