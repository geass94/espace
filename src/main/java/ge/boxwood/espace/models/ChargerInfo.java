package ge.boxwood.espace.models;

public class ChargerInfo {
    private Charger charger;
    private Order order;
    private String chargerTransactionId;
    private Integer responseCode;

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
}
