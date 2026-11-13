
package online.store.services;

import online.store.model.Order;
import online.store.repositories.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersService {

    private final OrderRepository orderRepository;

    // Constructor Dependency Injection
    public OrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Save multiple orders in the Orders table
    @Transactional
    public void placeOrders(Iterable<Order> orders) {

        orderRepository.saveAll(orders);
    }
}
