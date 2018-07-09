package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Pricing;
import ge.boxwood.espace.models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Pricing findDistinctFirstByRangeStartIsLessThanEqualAndRangeEndIsGreaterThanEqualAndStatus(double start, double end, Status status);
    List<Pricing> findAllByStatus(Status status);
}
