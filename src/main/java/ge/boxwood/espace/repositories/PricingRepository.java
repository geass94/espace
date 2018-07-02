package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    @Query(value = "select * from espace.pricing_list AS p where p.range_start<= ?1 AND p.range_end>= ?1 ORDER BY id DESC LIMIT 1;", nativeQuery = true)
    Pricing findPriceWithinRanges(double start);
}
