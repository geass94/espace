package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Settings;
import ge.boxwood.espace.models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Settings findByKeyAndStatus(String key, Status status);
    List<Settings> findAllByStatus(Status status);
}
