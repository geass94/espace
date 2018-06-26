package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.CreditCardRepository;
import ge.boxwood.espace.services.CreditCardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardServiceImpl implements CreditCardService {
    private final CreditCardRepository creditCardRepo;


    public CreditCardServiceImpl(CreditCardRepository creditCardRepo) {
        this.creditCardRepo = creditCardRepo;
    }

    @Override
    public CreditCard create(CreditCard creditCard) {
        creditCard.setStatus(Status.ACTIVE);
        return creditCardRepo.save(creditCard);
    }

    @Override
    public CreditCard update(CreditCard creditCard) {
        return creditCardRepo.save(creditCard);
    }

    @Override
    public CreditCard getOne(Long id) {
        return creditCardRepo.findOne(id);
    }

    @Override
    public CreditCard findByUserAndTrxId(User user, String trxId) {
        return creditCardRepo.findByUserAndTrxIdAndStatus(user,trxId,Status.ACTIVE);
    }

    @Override
    public List<CreditCard> getByUser(User user){
        return creditCardRepo.findAllByUserAndStatus(user,Status.ACTIVE);
    }

    @Override
    public CreditCard findByUserAndMaskedPan(User user, String maskedPan) {
        return creditCardRepo.findByUserAndMaskedPanAndStatus(user,maskedPan, Status.ACTIVE);
    }

    @Override
    public void delete(Long id) {
        CreditCard creditCard = getOne(id);
        creditCard.setStatus(Status.DELETED);
        creditCard.setTrxId(null);

        creditCardRepo.save(creditCard);
    }
}