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
public class CustomersController {

    private final FaunaClient client;

    // A query snippet performing a projection on the variable customer.
    // We will reuse this with query composition to obtain a consistent
    // result shape regardless of the underlying data model.
    private final Query response = fql("""
              customer {
                id,
                name,
                email,
                address
              }
            """);

    @Autowired
    public CustomersController(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/customers/{id}")
    Future<Customer> get(@PathVariable String id) {
        // Build a map of args that match with the variables in the query. We pass
        // a string variable, `id`, and another Query `response` as our args.
        Map<String,Object> args = Map.of("id", id, "response", response);

        // Get the Customer document by `id`, using the ! operator to assert that the document exists.
        // If the document does not exist, Fauna will throw a document_not_found error which in turn
        // will throw a NullDocumentException.
        //
        // Use projection to only return the fields you need.
        Query query = fql("""
                let customer: Any = Customer.byId(${id})!
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Customer.class to specify that the query will return a single
        // item representing a Customer.
        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers")
    Future<Customer> create(@RequestBody CustomerInfo info) {
        // Build a map of args that match with the variables in the query. We pass
        // a CustomerInfo variable, `info`, and another Query `response` as our args.
        Map<String,Object> args = Map.of("info", info, "response", response);

        // Create a new Customer document with the provided fields.
        // Use projection to only return the fields you need.
        Query query = fql("""
                let customer: Any = Customer.create(${info})
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Customer.class to specify that the query will return a single
        // item representing a Customer.
        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}")
    Future<Customer> update(@PathVariable String id, @RequestBody CustomerInfo info) {
        // Build a map of args that match with the variables in the query. We pass
        // a CustomerInfo variable, `info`, and another Query `response` as our args.
        Map<String,Object> args = Map.of("id", id, "info", info, "response", response);

        // Get the Customer document by `id`, using the ! operator to assert that the document exists.
        // If the document does not exist, Fauna will throw a document_not_found error which in turn
        // will throw a NullDocumentException.
        //
        // All unannotated fields and fields annotated with @FaunaField will be serialized, including
        // those with `null` values. When an update is made to a field with a null value, that value of
        // that field is unset on the document. Partial updates must be made with a dedicated class,
        // anonymous class, or Map.
        //
        // Use projection to only return the fields you need.
        Query query = fql("""
                let customer: Any = Customer.byId(${id})!.update(${info})
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Customer.class to specify that the query will return a single
        // item representing a Customer.
        return client.asyncQuery(query, Customer.class).thenApply(QuerySuccess::getData);
    }
}
