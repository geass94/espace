package ge.boxwood.espace.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    MAIL(1,"MAIL"),
    SMS(2,"SMS"),
    PUSH(3,"PUSHNOTIFICATION"),
    RECOVERY(4, "RECOVERY");

    private int id;
    private String name;

    NotificationType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static NotificationType getFromId(int id) {
        for (NotificationType role : NotificationType.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new RuntimeException("Notification with provided id[" + id + "] not found");
    }

    public static NotificationType getFromName(String name) {
        for (NotificationType role : NotificationType.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new RuntimeException("Notification with provided name[" + name + "] not found");
    }

    public int getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
