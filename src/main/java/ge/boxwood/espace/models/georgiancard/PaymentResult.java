package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PaymentResult {
    @JacksonXmlProperty(localName = "code")
    public Long code;

    @JacksonXmlProperty(localName = "desc")
    public String desc;
}
