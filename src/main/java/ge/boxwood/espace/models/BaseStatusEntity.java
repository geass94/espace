package ge.boxwood.espace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.boxwood.espace.models.converters.StatusConverter;
import ge.boxwood.espace.models.enums.Status;

import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class BaseStatusEntity extends BaseEntity {

    @NotNull
    @Convert(converter = StatusConverter.class)
    protected Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonIgnore
    public boolean isDeleted() {
        return Status.DELETED == status;
    }

    @JsonIgnore
    public boolean isActive() {
        return Status.ACTIVE == status;
    }

}
