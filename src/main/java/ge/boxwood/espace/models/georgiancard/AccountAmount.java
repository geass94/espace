package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "account-amount")
public class AccountAmount {
    @JacksonXmlProperty(localName = "id")
    public String id;
    @JacksonXmlProperty(localName = "amount")
    public BigDecimal amount;

    @JacksonXmlProperty(localName = "fee")
    public BigDecimal fee;

    @JacksonXmlProperty(localName = "currency")
    public String currencyCode = "981";

    @JacksonXmlProperty(localName = "exponent")
    public String exponent = "2";

}
