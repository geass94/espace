package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    List<Authority> findAllByIdIn(List<Long> authIds);
}
