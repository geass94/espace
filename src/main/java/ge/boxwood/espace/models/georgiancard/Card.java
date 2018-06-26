package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "card")
public class Card {
    @JacksonXmlProperty(localName = "ref")
    public String ref;

    @JacksonXmlProperty(localName = "present")
    public String present;
}
