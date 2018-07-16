package ge.boxwood.espace.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ge.boxwood.espace.models.enums.Status;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "Payment")
public class Payment extends BaseEntity {
    @Column
    private String uuid;
    @JsonIgnore
    @Column
    private String trxId;
    @JsonIgnore
    @Column
    private String prrn;
    @Column
    private float price;
    @Column
    private Date date;
    @Column
    private Date confirmDate;
    @Column
    private boolean confirmed;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;
    @ManyToOne
    @JoinColumn(name = "orderId")
    @JsonIgnore
    private Order order;
    @Column
    private boolean active;


    public Payment(float price, Order order) {
        this.price = price;
        this.order = order;
        this.confirmed = false;
        this.active = true;
        this.uuid = UUID.randomUUID().toString();
        this.date = new Date();
    }

    public Payment() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public void confirm() {
        this.confirmed = true;
        this.confirmDate = new Date();
        this.order.confirm();
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getPrrn() {
        return prrn;
    }

    public void setPrrn(String prrn) {
        this.prrn = prrn;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}