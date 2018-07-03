package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectorRepository extends JpaRepository<Connector, Long> {
    List<Connector> findAllByCharger(Charger charger);
    Connector findByChargerAndConnectorId(Charger charger, Long id);
}
