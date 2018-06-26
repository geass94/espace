package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Partner;
import ge.boxwood.espace.models.PartnerFile;
import ge.boxwood.espace.models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerFileRepository extends JpaRepository<PartnerFile, Long> {
    List<PartnerFile> findAllByPartner(Partner partner);
}
