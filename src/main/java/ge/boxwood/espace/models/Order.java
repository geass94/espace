package ge.boxwood.espace.models;


import com.fasterxml.jackson.annotation.*;
import ge.boxwood.espace.models.converters.PaymentTypeConverter;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.models.enums.Status;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "orders")
public class Order extends BaseStatusAuditEntity {
    @Column
    private String uuid;
    @Column
    private Date confirmDate;
    @Column
    private boolean confirmed;
    @Column
    @JsonIgnore
    private boolean refunded;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charger_id", nullable = true)
    private Charger charger;
    @Column
    private Long chargerTransactionId;
    @Column
    @Convert(converter = PaymentTypeConverter.class)
    private PaymentType paymentType;
    @Column
    private float price;
    private float targetPrice;

    public Order(User user) {
        this.user = user;
        this.status = Status.ORDERED;
        this.confirmed = false;
        this.refunded = false;
        this.uuid = UUID.randomUUID().toString();
        this.payments = new ArrayList<>();
    }

    public Order() {}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty
    public Date getConfirmDate() {
        return confirmDate;
    }
    @JsonIgnore
    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    @JsonProperty
    public User getUser() {
        return user;
    }
    @JsonIgnore
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty
    public List<Payment> getPayments() {
        return payments;
    }
    @JsonIgnore
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void confirm() {
        this.confirmDate = new Date();
        this.confirmed = true;
    }

    public long getUserId() {
        return user.getId();
    }

    public String getPhone() {
        return this.user.getPhoneNumber();
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }
    @JsonProperty
    public Long getChargerTransactionId() {
        return chargerTransactionId;
    }
    @JsonIgnore
    public void setChargerTransactionId(Long chargerTransactionId) {
        this.chargerTransactionId = chargerTransactionId;
    }

    public float getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(float targetPrice) {
        this.targetPrice = targetPrice;
    }
}
