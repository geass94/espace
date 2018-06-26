package ge.boxwood.espace.models.georgiancard;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RegisterPaymentRequest {
    public String merchId;
    public String trxId;
    public String merchantTrx;
    public String resultCode;
    public BigDecimal amount;
    public String accountId;
    public String pRrn;
    public Date pTransmissionDateTime;
    public List<OrderParam> initStageParams;
    public List<OrderParam> firstStageParams;
    public Date ts;
    public String signature;
    public String pCardHolder;
    public String pAuthCode;
    public String pMaskedPan;
    public String pIsFullyAuthenticated;
    public String p3DSPAResTXcavv;
    public String p3DSPAResTXeci;
    public String p3DSPAResPurchasexid;
    public String pStorageCardRef;
    public String pStorageCardExpDt;
    public String pStorageCardRecurrent;
    public String pStorageCardRegistered;
    public String extResultCode;
}
