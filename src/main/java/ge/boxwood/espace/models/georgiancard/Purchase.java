package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "purchase")
public class Purchase {
    @JacksonXmlProperty(localName = "shortDesc")
    public String shortDesc;

    @JacksonXmlProperty(localName = "longDesc")
    public String longDesc;

    @JacksonXmlProperty(localName = "account-amount")
    public AccountAmount accountAmount = new AccountAmount();

}
