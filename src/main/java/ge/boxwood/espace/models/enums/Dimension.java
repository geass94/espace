package ge.boxwood.espace.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Dimension {
    METER(1, "METER"),
    KG(2, "KG"),
    KVM(3, "KVM"),
    LTR(4, "LTR"),
    UNIT(5, "UNIT");

    private int id;
    private String name;

    Dimension(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Dimension getFromId(int id) {
        for (Dimension role : Dimension.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new RuntimeException("Dimension with provided id[" + id + "] not found");
    }

    public static Dimension getFromName(String name) {
        for (Dimension role : Dimension.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new RuntimeException("Dimension with provided name[" + name + "] not found");
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
