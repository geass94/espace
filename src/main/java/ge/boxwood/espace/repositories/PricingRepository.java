package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    @Query("select p from Pricing p where p.rangeStart<= ?1 AND p.rangeEnd>= ?1 ORDER BY id DESC")
    Pricing findPriceWithinRanges(double start);
}
