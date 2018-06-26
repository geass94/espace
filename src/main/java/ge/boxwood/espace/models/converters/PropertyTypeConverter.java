package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.PropertyType;

import javax.persistence.AttributeConverter;

public class PropertyTypeConverter implements AttributeConverter<PropertyType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PropertyType status) {
        return status.getId();
    }

    @Override
    public PropertyType convertToEntityAttribute(Integer id) {
        return PropertyType.getFromId(id);
    }
}