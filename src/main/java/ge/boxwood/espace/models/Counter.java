package ge.boxwood.espace.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "coutner")
@Table
public class Counter extends BaseEntity {
    @Column
    private Long chargerId;
    @Column
    private Double chargePower;
    @Column
    private Long chargeTime;
    @Column
    private String chargerTrId;
    @Column
    private Float currentPrice;
    @Column
    private Long consumedPower;
    @Column
    private Long lastUpdate;
    @Column
    private Long startTime;
    @Column
    private Float pricing;

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

    public Float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getConsumedPower() {
        return consumedPower;
    }

    public void setConsumedPower(Long consumedPower) {
        this.consumedPower = consumedPower;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Float getPricing() {
        return pricing;
    }

    public void setPricing(Float pricing) {
        this.pricing = pricing;
    }
}
