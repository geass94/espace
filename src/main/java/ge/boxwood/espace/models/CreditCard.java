package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "credit_cards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "trx_id"})
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class CreditCard extends BaseStatusAuditEntity {

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "card_name")
    private String cardName;

    @JsonIgnore
    @OneToMany(mappedBy = "creditCard", fetch = FetchType.EAGER)
    private Set<Payment> payments;

    @Column
    private String maskedPan;

    @Column
    private String cardHolder;

    @JsonIgnore
    @Column(name = "trx_id")
    private String trxId;

    @Column
    private String expDate;

    @Column
    private Integer orderIndex;

    @JsonIgnore
    @Column
    private String cardRef;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCardRef() {
        return cardRef;
    }

    public void setCardRef(String cardRef) {
        this.cardRef = cardRef;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}