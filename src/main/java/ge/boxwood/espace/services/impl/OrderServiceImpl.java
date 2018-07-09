package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOneByUUID(String uuid) {
        return orderRepository.findByUuid(uuid);
    }
}
