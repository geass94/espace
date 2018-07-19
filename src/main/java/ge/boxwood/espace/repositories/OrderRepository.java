package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByUserAndChargerAndStatus(User user, Charger charger, Status status);

    Order findByUserAndChargerTransactionIdAndStatus(User user, Long trid, Status status);

    Order findByUuid(String uuid);

    Order findByChargerTransactionId(Long trid);

    List<Order> findAllByUser(User user);
}
