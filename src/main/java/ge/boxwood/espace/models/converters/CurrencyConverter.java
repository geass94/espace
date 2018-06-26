package ge.boxwood.espace.models.converters;

import ge.boxwood.espace.models.enums.Currency;

import javax.persistence.AttributeConverter;

public class CurrencyConverter implements AttributeConverter<Currency, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Currency currency) {
        return currency != null ? currency.getId() : null;
    }

    @Override
    public Currency convertToEntityAttribute(Integer id) {
        return id != null ? Currency.getFromId(id) : null;
    }
}
