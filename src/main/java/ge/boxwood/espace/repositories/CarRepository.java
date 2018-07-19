package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
