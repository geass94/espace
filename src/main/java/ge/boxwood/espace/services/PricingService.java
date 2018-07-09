package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Pricing;

import java.util.List;

public interface PricingService {
    Float getPriceForChargingPower(Double chargingPower);
    List<Pricing> getAll();
    Pricing update(Pricing pricing, Long id);
    Pricing create(Pricing pricing);
    void delete(Long id);
}
