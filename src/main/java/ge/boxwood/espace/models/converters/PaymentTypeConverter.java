package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.PaymentType;

import javax.persistence.AttributeConverter;

public class PaymentTypeConverter implements AttributeConverter<PaymentType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PaymentType type) {
        return type.getId();
    }

    @Override
    public PaymentType convertToEntityAttribute(Integer id) {
        return PaymentType.getFromId(id);
    }
}