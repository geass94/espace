package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Pricing;
import ge.boxwood.espace.models.enums.Status;
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
        Pricing pricing = pricingRepository.findDistinctFirstByRangeStartIsLessThanEqualAndRangeEndIsGreaterThanEqualAndStatus(chargingPower, chargingPower, Status.ACTIVE);
        return pricing.getPrice();
    }

    @Override
    public List<Pricing> getAll() {
        return pricingRepository.findAllByStatus(Status.ACTIVE);
    }

    @Override
    public Pricing update(Pricing pricing, Long id) {
        Pricing original = pricingRepository.findOne(id);
        if ( pricing.getPrice() != null && pricing.getPrice() != original.getPrice() ){
            original.setPrice( pricing.getPrice() );
        }

        if( pricing.getRangeStart() != null && pricing.getRangeStart() != original.getRangeStart() ){
            original.setRangeStart( pricing.getRangeStart() );
        }

        if( pricing.getRangeEnd() != null && pricing.getRangeEnd() != original.getRangeEnd() ){
            original.setRangeEnd( pricing.getRangeEnd() );
        }

        if( pricing.getName() != null && !pricing.getName().isEmpty() && pricing.getName() != original.getName() ){
            original.setName( pricing.getName() );
        }

        original = pricingRepository.save(original);

        return original;
    }

    @Override
    public Pricing create(Pricing pricing) {
        pricing.setStatus(Status.ACTIVE);
        return pricingRepository.save(pricing);
    }

    @Override
    public void delete(Long id) {
        Pricing pricing = pricingRepository.findOne(id);
        pricing.setStatus(Status.DELETED);
        pricingRepository.save(pricing);
    }
}
