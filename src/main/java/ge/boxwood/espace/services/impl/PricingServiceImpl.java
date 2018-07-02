package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Pricing;
import ge.boxwood.espace.repositories.PricingRepository;
import ge.boxwood.espace.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
public class PricingServiceImpl implements PricingService {
    @Autowired
    private PricingRepository pricingRepository;
    @Autowired
    private EntityManager em;
    @Override
    public Float getPriceForChargingPower(Double chargingPower) {
        Query q = em.createNativeQuery("select * from espace.pricing_list AS p where p.range_start<= :power AND p.range_end>= :power ORDER BY id DESC LIMIT 1;", Pricing.class);
        q.setParameter("power", chargingPower);
        Pricing p = (Pricing) q.getSingleResult();
        return p.getPrice();
    }
}
