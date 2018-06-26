package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Partner;
import ge.boxwood.espace.models.PartnerFile;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.PartnerRepository;
import ge.boxwood.espace.services.PartnerFileService;
import ge.boxwood.espace.services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PartnerServiceImpl implements PartnerService {
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private PartnerFileService partnerFileService;
    @Override
    public Partner create(Partner partner) {
        partner.setStatus(Status.ACTIVE);
        partner = partnerRepository.save(partner);
        if (partner.getImage() != null && !partner.getImage().isEmpty()) {
            PartnerFile partnerFile = partnerFileService.create(partner.getImage(), partner);
            partner.setImageId(partnerFile.getId());
        }
        partnerRepository.saveAndFlush(partner);
        return partner;
    }

    @Override
    public Partner update(Partner partner, Long id) {
        partner.setUpdatedAt(new Date());
        partner.setUpdatedBy(1L);
        partner.setId(id);
        if (partner.getImage() != null && !partner.getImage().isEmpty()) {
            partnerFileService.create(partner.getImage(), partner);
        }
        return partnerRepository.save(partner);
    }

    @Override
    public List<Partner> getAll() {
        return partnerRepository.findAll();
    }

    @Override
    public void delete(Long id) {

    }
}
