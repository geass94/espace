package ge.boxwood.espace.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings extends BaseStatusEntity {
    @Column(name = "setting_name")
    private String name;
    @Column(name = "setting_key")
    private String key;
    @Column(name = "setting_value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
