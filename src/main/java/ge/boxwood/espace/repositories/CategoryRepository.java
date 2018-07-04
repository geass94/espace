package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
