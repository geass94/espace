package ge.boxwood.espace.models;

import org.jetbrains.annotations.Contract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "settings")
public class Settings extends BaseStatusEntity {
    @Column(name = "setting_name")
    private String name;
    @NotNull
    @Column(name = "setting_key", nullable = false)
    private String key;
    @NotNull
    @Column(name = "setting_value", nullable = false)
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        if(key != null)
            return key;
        else
            return "";
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        if(value != null)
            return value;
        else
            return "";
    }

    public void setValue(String value) {
        this.value = value;
    }
}
