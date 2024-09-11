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

        private Product product;
        private Integer quantity;

        public Product getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }

    // Add @FaunaId attribute to indicate that it's an ID on a document. This tells
    // the codec library to ignore this during encoding.
    //
    // To use client-generated IDs, use @FaunaId( isClientGenerate = true), which will
    // tell the codec library to encode it if it is not null.
    @FaunaId
    private String id;
    private Instant createdAt;
    private Customer customer;
    private List<Item> items;
    private Status status;
    private Integer total;
}
