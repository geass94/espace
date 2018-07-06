package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    List<CreditCard> findAllByUserAndStatus(User user,Status status);
    CreditCard findByUserAndTrxIdAndStatus(User user, String trxId, Status status);
    CreditCard findByUserAndMaskedPanAndStatus(User user, String maskedPin, Status status);
    List<CreditCard> findAllByOrderIndexAndUser(Integer orderIndex, User user);
}
