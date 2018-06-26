package ge.boxwood.espace.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    ONLINE(1, "Online"),
    CREDITCARD(2, "CreditCard"),
    CASH(3, "Cash");

    int id;
    String name;

    PaymentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PaymentType getFromId(int id) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new RuntimeException("PaymentType with provided id[" + id + "] not found");
    }

    public static PaymentType getFromName(String name) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new RuntimeException("PaymentType with provided name[" + name + "] not found");
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}