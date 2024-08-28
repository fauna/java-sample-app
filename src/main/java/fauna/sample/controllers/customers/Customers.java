package fauna.sample.controllers.customers;

import com.fauna.client.FaunaClient;
import com.fauna.response.QuerySuccess;
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
public class Customers {

    static class CustomerRequest {
        public String name;
        public String email;
        public Customer.Address address;
    }

    private final FaunaClient client;

    @Autowired
    public Customers(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/customers")
    Future<Customer> get(@RequestParam String id) {
        return client.asyncQuery(fql("{}"), Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers")
    Future<Customer> create(@RequestBody CustomerRequest customer) {
        return client.asyncQuery(fql("{}"), Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}")
    Future<Customer> update(@PathVariable String id, @RequestBody CustomerRequest customer) {
        return client.asyncQuery(fql("{}"), Customer.class).thenApply(QuerySuccess::getData);
    }
}
