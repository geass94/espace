package ge.boxwood.espace.models.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum InventoryCategory {
    FURNITURE(1, "FURNITURE"), // ავეჯი
    EQUIPMENT(2, "EQUIPMENT"), // ტექნიკა
    AUXINVENTORY(3, "AUXINVENTORY"), // დამხმარე ინვენტარი
    CONSUMABLES(4, "CONSUMABLES"); // სახარჯი მასალა


    private int id;
    private String name;

    InventoryCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static InventoryCategory getFromId(int id) {
        for (InventoryCategory type : InventoryCategory.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new RuntimeException("Property type with provided id[" + id + "] not found");
    }

    public static InventoryCategory getFromName(String name) {
        for (InventoryCategory type : InventoryCategory.values()) {
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
