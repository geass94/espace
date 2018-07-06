package ge.boxwood.espace.services;

import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.User;

import java.util.List;

public interface CreditCardService {
    CreditCard create(CreditCard creditCard);

    CreditCard update(CreditCard creditCard, Long id);

    CreditCard getOne(Long id);
    CreditCard findByUserAndTrxId(User user, String trxId);

    List<CreditCard> getByUser(User user);

    CreditCard findByUserAndMaskedPan(User user, String maskedPan);

    void delete(Long id);
}