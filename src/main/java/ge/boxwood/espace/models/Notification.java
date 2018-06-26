package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.boxwood.espace.models.converters.NotificationTypeConverter;
import ge.boxwood.espace.models.enums.NotificationType;

import javax.persistence.*;
import java.util.Date;

@Table(name = "notifications")
@Entity
public class Notification extends BaseStatusEntity {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "activation_code")
    private String activationcode;

    @Column(name = "used")
    private boolean used;

    @Column(name = "expiresin")
    private Date expiresin;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "type_id")
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType notificationType;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActivationcode() {
        return activationcode;
    }

    public void setActivationcode(String activationcode) {
        this.activationcode = activationcode;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Date getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(Date expiresin) {
        this.expiresin = expiresin;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    @PrePersist
    public void saveCreateDate(){
        this.createDate = new Date();
    }

}