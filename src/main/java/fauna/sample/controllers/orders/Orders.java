package fauna.sample.controllers.orders;

import com.fauna.client.FaunaClient;
import com.fauna.response.QuerySuccess;
import fauna.sample.controllers.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.Future;

import static com.fauna.query.builder.Query.fql;

@Controller
public class Orders {

    static class OrderUpdateRequest {
        public Order.Status status;
        public Integer payment;
    }

    static class AddItemRequest {
        public Product product;
        public Integer quantity;
    }

    private final FaunaClient client;

    @Autowired
    public Orders(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/orders/{id}")
    Future<Order> get(@PathVariable String id) {
        var r = client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
        return r;
    }

    @Async
    @PostMapping("/orders/{id}")
    Future<Order> update(@PathVariable String id, @RequestBody OrderUpdateRequest req) {
        return client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/customers/{id}/orders")
    Future<Order> getByCustomer(@PathVariable("id") String customerId) {
        return client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}/cart")
    Future<Order> createCart(@PathVariable("id") String customerId) {
        return client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}/cart/item")
    Future<Order> addToCart(@PathVariable("id") String customerId, @RequestBody AddItemRequest req) {
        return client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/customers/{id}/cart")
    Future<Order> getCart(@PathVariable("id") String customerId) {
        return client.asyncQuery(fql("{}"), Order.class).thenApply(QuerySuccess::getData);
    }
}
