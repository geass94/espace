package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findAllByChargerIdAndChargerTrId(Long chargerId, String chargerTrId);
    List<Counter> findAllByChargerTrId(String chargerTrId);
    Counter findByChargerIdAndAndChargerTrId(Long chargerId, String chargerTrId);
    Counter findByChargerIdAndAndChargerTrIdAndConsumedPowerIsNot(Long chid, String trid, Double power);
}
