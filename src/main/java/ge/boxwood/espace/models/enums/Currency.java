package ge.boxwood.espace.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    GEL(1, "GEL"),
    USD(2, "USD"),
    RUB(3, "RUB");

    private int id;
    private String name;

    Currency(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Currency getFromId(int id) {
        for (Currency role : Currency.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new RuntimeException("Currency with provided id[" + id + "] not found");
    }

    public static Currency getFromName(String name) {
        for (Currency role : Currency.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new RuntimeException("Currency with provided name[" + name + "] not found");
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
