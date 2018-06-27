package ge.boxwood.espace.models;

public class ChargerInfo {
    private Charger charger;
    private Order order;
    private String chargerTransactionId;
    private Integer responseCode;
    private Long transStart;
    private Long transStop;
    private Long meterStart;
    private Long meterStop;

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getChargerTransactionId() {
        return chargerTransactionId;
    }

    public void setChargerTransactionId(String chargerTransactionId) {
        this.chargerTransactionId = chargerTransactionId;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Long getTransStart() {
        return transStart;
    }

    public void setTransStart(Long transStart) {
        this.transStart = transStart;
    }

    public Long getTransStop() {
        return transStop;
    }

    public void setTransStop(Long transStop) {
        this.transStop = transStop;
    }

    public Long getMeterStart() {
        return meterStart;
    }

    public void setMeterStart(Long meterStart) {
        this.meterStart = meterStart;
    }

    public Long getMeterStop() {
        return meterStop;
    }

    public void setMeterStop(Long meterStop) {
        this.meterStop = meterStop;
    }
}
