package fauna.sample.controllers.customers;

import com.fauna.client.FaunaClient;
import com.fauna.query.builder.Query;
import com.fauna.response.QuerySuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Future;

import static com.fauna.query.builder.Query.fql;

@RestController
public class Customers {

    private final FaunaClient client;

    private final Query response = fql("""
              customer {
                id,
                name,
                email,
                address
              }
            """);

    @Autowired
    public Customers(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/customers/{id}")
    Future<Customer> get(@PathVariable String id) {
        Map<String,Object> args = Map.of("id", id, "response", response);

        Query query = fql("""
                let customer: Any = Customer.byId(${id})!
                ${response}
                """, args);

        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers")
    Future<Customer> create(@RequestBody CustomerInfo req) {
        Map<String,Object> args = Map.of("req", req, "response", response);

        Query query = fql("""
                let customer: Any = Customer.create(${req})
                ${response}
                """, args);

        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}")
    Future<Customer> update(@PathVariable String id, @RequestBody CustomerInfo req) {
        Map<String,Object> args = Map.of("id", id, "req", req, "response", response);

        Query query = fql("""
                let customer: Any = Customer.byId(${id})!.update(${req})
                ${response}
                """, args);

        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }
}
