package ge.boxwood.espace.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings extends BaseStatusEntity {
    String name;
    String key;
    String value;

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
