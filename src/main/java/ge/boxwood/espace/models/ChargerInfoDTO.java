package ge.boxwood.espace.models;

public class ChargerInfoDTO {
    private Long chargerId;
    private Double chargePower;
    private Long chargeTime;
    private String chargerTrId;
    private String paymentUUID;
    private Float currentPrice = 0f;
    private Integer chargerStatus;
    private Integer chargerType;
    private Boolean chargingFinished;
    private Boolean chargerPulledOut;
    private Long consumedPower;

    public Boolean getChargerPulledOut() {
        return chargerPulledOut;
    }

    public void setChargerPulledOut(Boolean chargerPulledOut) {
        this.chargerPulledOut = chargerPulledOut;
    }

    public Long getChargerId() {
        return chargerId;
    }

    public void setChargerId(Long chargerId) {
        this.chargerId = chargerId;
    }

    public Double getChargePower() {
        return chargePower;
    }

    public void setChargePower(Double chargePower) {
        this.chargePower = chargePower;
    }

    public Long getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(Long chargeTime) {
        this.chargeTime = chargeTime;
    }

    public String getChargerTrId() {
        return chargerTrId;
    }

    public void setChargerTrId(String chargerTrId) {
        this.chargerTrId = chargerTrId;
    }

    public String getPaymentUUID() {
        return paymentUUID;
    }

    public void setPaymentUUID(String paymentUUID) {
        this.paymentUUID = paymentUUID;
    }

    public Float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getChargerStatus() {
        return chargerStatus;
    }

    public void setChargerStatus(Integer chargerStatus) {
        this.chargerStatus = chargerStatus;
    }

    public Integer getChargerType() {
        return chargerType;
    }

    public void setChargerType(Integer chargerType) {
        this.chargerType = chargerType;
    }

    public Boolean getChargingFinished() {
        return chargingFinished;
    }

    public void setChargingFinished(Boolean chargingFinished) {
        this.chargingFinished = chargingFinished;
    }

    public Long getConsumedPower() {
        return consumedPower;
    }

    public void setConsumedPower(Long consumedPower) {
        this.consumedPower = consumedPower;
    }
}
