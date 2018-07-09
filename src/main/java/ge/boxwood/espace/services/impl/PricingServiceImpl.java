package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Pricing;
import ge.boxwood.espace.repositories.PricingRepository;
import ge.boxwood.espace.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingServiceImpl implements PricingService {
    @Autowired
    private PricingRepository pricingRepository;
    @Override
    public Float getPriceForChargingPower(Double chargingPower) {
        Pricing pricing = pricingRepository.findDistinctFirstByRangeStartIsLessThanEqualAndRangeEndIsGreaterThanEqual(chargingPower, chargingPower);
        return pricing.getPrice();
    }

    @Override
    public List<Pricing> getAll() {
        return pricingRepository.findAll();
    }
}
