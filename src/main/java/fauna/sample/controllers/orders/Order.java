package fauna.sample.controllers.orders;

import com.fauna.annotation.FaunaField;
import fauna.sample.controllers.customers.Customer;
import fauna.sample.controllers.products.Product;

import java.time.Instant;

public class Order {

    public enum Status {
        Cart,
        Processing,
        Shipped,
        Delivered
    }

    public static class Item {
        public String id;
        public Order order;
        public Product product;
        public Integer quantity;
    }

    public String id;
    @FaunaField(name = "created_at")
    public Instant createdAt;
    public Customer customer;
    public Item items;
    public Status status;
    public Integer total;
}
