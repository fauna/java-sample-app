package fauna.sample.controllers.customers;

import com.fauna.annotation.FaunaField;
import com.fauna.annotation.FaunaId;
import fauna.sample.controllers.orders.Order;

import java.util.List;

public class Customer extends CustomerInfo {
    // Add @FaunaId attribute to indicate that it's an ID on a document. This tells
    // the codec library to ignore this during encoding.
    //
    // To use client-generated IDs, use @FaunaId( isClientGenerate = true), which will
    // tell the codec library to encode it if it is not null.
    @FaunaId
    private String id;

    // Fauna object and document fields will be matched to class fields by default, but
    // you can override the name with @FaunaField( name = "my_name" )
    @FaunaField( name = "cart")
    private Order orderCart;
    private List<Order> orders;

    public String getId() {
        return id;
    }

    public Order getOrderCart() {
        return orderCart;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
