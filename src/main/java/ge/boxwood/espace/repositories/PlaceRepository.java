package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
