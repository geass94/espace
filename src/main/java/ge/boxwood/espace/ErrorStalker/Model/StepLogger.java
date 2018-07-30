package ge.boxwood.espace.ErrorStalker.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ge.boxwood.espace.models.BaseEntity;
import ge.boxwood.espace.models.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;

@Entity
@Table(name = "step_logger")
public class StepLogger extends BaseEntity {
    @Column
    private String entryPoint;
    @Column
    private String method;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private HashMap params;
    @Column
    private Date timestamp;

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap getParams() {
        return params;
    }

    public void setParams(HashMap params) {
        this.params = params;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
