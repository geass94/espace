package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllByOrder(Order order);
}
