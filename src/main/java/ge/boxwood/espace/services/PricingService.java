package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Pricing;

import java.util.List;

public interface PricingService {
    Float getPriceForChargingPower(Double chargingPower);
    List<Pricing> getAll();
}
