package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAll();
    Order getOneByUUID(String uuid);
}
