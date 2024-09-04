package fauna.sample.controllers.orders;

import com.fauna.annotation.FaunaId;
import fauna.sample.controllers.customers.Customer;
import fauna.sample.controllers.products.Product;

import java.time.Instant;
import java.util.List;

public class Order {

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getTotal() {
        return total;
    }

    public enum Status {
        cart,
        processing,
        shipped,
        delivered
    }

    public static class Item {
        @FaunaId
        private String id;
        private Order order;
        private Product product;
        private Integer quantity;

        public String getId() {
            return id;
        }

        public Order getOrder() {
            return order;
        }

        public Product getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }

    @FaunaId
    private String id;
    private Instant createdAt;
    private Customer customer;
    private List<Item> items;
    private Status status;
    private Integer total;
}
