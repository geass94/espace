package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Charger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargerRepository extends JpaRepository<Charger, Long> {
    List<Charger> findAllByLatitudeAndLongitude(Double latitude, Double longitude);
    Charger findByChargerId(Long id);
    Long countAllByStatus(Integer status);
    Charger findByCode(String code);
}
