package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.InventoryCategory;

import javax.persistence.AttributeConverter;

public class InventoryCategoryConverter implements AttributeConverter<InventoryCategory, Integer> {
    @Override
    public Integer convertToDatabaseColumn(InventoryCategory status) {
        return status.getId();
    }

    @Override
    public InventoryCategory convertToEntityAttribute(Integer id) {
        return InventoryCategory.getFromId(id);
    }

}
