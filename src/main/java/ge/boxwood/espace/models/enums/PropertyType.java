package ge.boxwood.espace.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyType {
    APPARTMENT(1, "APPARTMENT"),
    HOUSE(2, "HOUSE"),
    ROOM(3, "ROOM"),
    OFFICE(4, "OFFICE"),
    COMMERCIAL(5, "COMMERCIAL");

    private int id;
    private String name;

    PropertyType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PropertyType getFromId(int id) {
        for (PropertyType type : PropertyType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new RuntimeException("Property type with provided id[" + id + "] not found");
    }

    public static PropertyType getFromName(String name) {
        for (PropertyType type : PropertyType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new RuntimeException("Property type with provided name[" + name + "] not found");
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
