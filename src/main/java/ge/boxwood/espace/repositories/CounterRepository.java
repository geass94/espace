package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findAllByChargerIdAndAndChargerTrId(Long chargerId, Long chargerTrId);
    Counter findByChargerIdAndAndChargerTrId(Long chargerId, Long chargerTrId);
    Counter findByChargerIdAndAndChargerTrIdAndConsumedPowerIsNot(Long chid, Long trid, Double power);
}
