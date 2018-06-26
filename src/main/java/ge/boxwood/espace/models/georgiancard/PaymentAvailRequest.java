package ge.boxwood.espace.models.georgiancard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentAvailRequest {
    public String merchId;
    public String trxId;
    public String langCode;
    public List<OrderParam> params = new ArrayList<>();
    public Date ts;
}
