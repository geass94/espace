package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByUuid(String uuid);
    Payment findByOrderAndConfirmed(Order order, boolean confirmed);
    List<Payment> findByTransaction(String trans_id);
    List<Payment> findAllByOrder(Order order);
}