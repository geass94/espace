package ge.boxwood.espace.models.georgiancard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "param")
public class OrderParam {
    @JacksonXmlProperty(localName = "name")
    public String name;

    @JacksonXmlProperty(localName = "value")
    public String value;
}
