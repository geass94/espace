package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    @Query(value = "select p from pricing_list AS p where p.range_start<= :val AND p.range_end>= :val ORDER BY id DESC LIMIT 1;", nativeQuery = true)
    Pricing findPriceWithinRanges(@Param("val") double val);
}
