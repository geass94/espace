package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOneByUUID(String uuid) {
        return orderRepository.findByUuid(uuid);
    }

    @Override
    public void confirm(String uuid) {
        Order order = orderRepository.findByUuid(uuid);
        List<Payment> payments = paymentRepository.findAllByOrder(order);
        for (Payment payment: payments) {
            payment.setConfirmed(true);
            payment.setConfirmDate(new Date());
            paymentRepository.save(payment);
        }
        order.confirm();
        orderRepository.save(order);
    }
}
