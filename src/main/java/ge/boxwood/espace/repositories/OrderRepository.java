package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByChargerAndConfirmed(Charger charger, boolean confirmed);

    Order findByChargerAndUserAndConfirmed(Charger charger, User user, boolean confirmed);

    Order findByChargerAndChargerTransactionIdAndUser(Charger charger, Long chargerTransactionId, User user);

    Order findByChargerAndChargerTransactionIdAndConfirmed(Charger charger, Long chargerTransactionId, boolean confirmed);

    Order findByUserAndChargerTransactionIdAndConfirmed(User user, Long trid, boolean confirmed);
}
