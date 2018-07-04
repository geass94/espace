package ge.boxwood.espace.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ge.boxwood.espace.models.converters.PaymentTypeConverter;
import ge.boxwood.espace.models.enums.PaymentType;
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
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private long id;
    @Column
    private String uuid;
    @Column
    private Date createDate;
    @Column
    @JsonIgnore
    private Date lastModifyDate;
    @Column
    @JsonIgnore
    private Date confirmDate;
    @Column
    private boolean confirmed;
    @Column
    @JsonIgnore
    private boolean refunded;
    @Column
    @JsonIgnore
    private boolean active;
    @Column
    private String comment;
    @Column
    private int status;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;
    @Column
    private boolean cashPayment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charger_id", nullable = true)
    private Charger charger;
    @JsonIgnore
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
        this.active = true;
        this.confirmed = false;
        this.refunded = false;
        this.createDate = new Date();
        this.lastModifyDate = new Date();
        this.uuid = UUID.randomUUID().toString();
        this.payments = new ArrayList<>();
        this.comment = "";
        this.cashPayment = true;
    }

    public Order() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

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


    @JsonIgnore
    public float getPayementsMade() {
        return (float) this.payments.stream()
                .filter(payment -> payment.isActive() && payment
                        .isConfirmed()).mapToDouble(value ->
                        (double) value.getPrice()).sum();
    }

    public void confirm() {
        this.confirmDate = new Date();
        this.confirmed = true;
    }

    public long getUserId() {
        return user.getId();
    }

    public boolean isCanBePaid() {
        Date date = new Date();
        long nowLong = date.getTime();
        Date createDate = new Date(new DateTime(this.createDate).getMillis());
        long cLong = createDate.getTime();
        long dif = nowLong - cLong;
        return (dif < 1000 * 60 * 60) && !this.confirmed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(boolean cashPayment) {
        this.cashPayment = cashPayment;
    }

    public String getPhone() {
        return this.user.getPhoneNumber();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    public Long getChargerTransactionId() {
        return chargerTransactionId;
    }

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
