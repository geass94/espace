package ge.boxwood.espace.ErrorStalker.Repository;

import ge.boxwood.espace.ErrorStalker.Model.StepLogger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepLoggerRepo extends JpaRepository<StepLogger, Long> {
}
