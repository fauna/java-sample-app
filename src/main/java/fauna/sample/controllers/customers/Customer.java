package fauna.sample.controllers.customers;

import com.fauna.annotation.FaunaId;
import fauna.sample.controllers.orders.Order;

import java.util.List;

public class Customer extends CustomerInfo {
    @FaunaId
    private String id;
    private Order cart;
    private List<Order> orders;

    public String getId() {
        return id;
    }

    public Order getCart() {
        return cart;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
