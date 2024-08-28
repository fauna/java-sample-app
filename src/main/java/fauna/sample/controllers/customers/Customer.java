package fauna.sample.controllers.customers;

import com.fauna.annotation.FaunaField;
import fauna.sample.controllers.orders.Order;

import java.util.List;

public class Customer {
    public static class Address {
        public String street;
        public String city;
        public String state;
        @FaunaField(name = "postal_code")
        public String postalCode;
        public String country;

    }

    public String id;
    public String name;
    public String email;
    public Order cart;
    public List<Order> orders;
    public Address address;
}
