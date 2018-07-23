package ge.boxwood.espace.ErrorStalker.Model;

import ge.boxwood.espace.models.BaseEntity;
import ge.boxwood.espace.models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashMap;

@Entity
@Table(name = "step_logger")
public class StepLogger extends BaseEntity {
    @Column
    private String entryPoint;
    @Column
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
}
