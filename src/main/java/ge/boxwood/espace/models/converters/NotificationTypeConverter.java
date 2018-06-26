package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.NotificationType;

import javax.persistence.AttributeConverter;

public class NotificationTypeConverter implements AttributeConverter<NotificationType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(NotificationType status) {
        return status.getId();
    }

    @Override
    public NotificationType convertToEntityAttribute(Integer id) {
        return NotificationType.getFromId(id);
    }
}
