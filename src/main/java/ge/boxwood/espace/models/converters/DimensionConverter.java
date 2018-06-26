package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.Dimension;

import javax.persistence.AttributeConverter;

public class DimensionConverter implements AttributeConverter<Dimension, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Dimension dimension) {
        return dimension != null ? dimension.getId() : null;
    }

    @Override
    public Dimension convertToEntityAttribute(Integer id) {
        return id != null ? Dimension.getFromId(id) : null;
    }
}
