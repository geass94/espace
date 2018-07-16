package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Order;

import java.util.List;

public interface OrderService {
    Order create(Order order);
    Order update(Order order);
    List<Order> getAll();
    List<Order> getAllByUser();
    Order getOneByUUID(String uuid);

}
