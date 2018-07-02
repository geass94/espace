package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Pricing findDistinctFirstByRangeStartIsGreaterThanEqualAndRangeEndIsLessThanEqual(Double rangeStart, Double rangeEnd);
}
