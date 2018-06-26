package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Partner;

import java.util.List;

public interface PartnerService {
    Partner create(Partner partner);
    Partner update(Partner partner, Long id);
    List<Partner> getAll();
    void delete(Long id);
}
