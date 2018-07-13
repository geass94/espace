package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    public List<Payment> getAllByOrder(Order order) {
        return paymentRepository.findAllByOrder(order);
    }

    @Override
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment update(Payment payment) {
        return paymentRepository.save(payment);
    }
}
