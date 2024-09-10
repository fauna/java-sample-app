package fauna.sample.controllers.orders;

import com.fauna.client.FaunaClient;
import com.fauna.query.builder.Query;
import com.fauna.response.QuerySuccess;
import com.fauna.types.Page;
import fauna.sample.controllers.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.fauna.query.builder.Query.fql;

@RestController
public class OrdersController {

    public static class OrderUpdate {
        private Order.Status status;
        private Integer payment;

        public Integer getPayment() {
            return payment;
        }

        public Order.Status getStatus() {
            return status;
        }
    }

    public static class OrderItem {
        private Product product;
        private Integer quantity;

        public Product getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }

    private final FaunaClient client;

    // A common response
    private final Query response = fql("""
    {
        id: order.id,
        payment: order.payment,
        createdAt: order.createdAt,
        status: order.status,
        total: order.total,
        items: order.items.toArray().map((item) => {
          product: {
            id: item.product.id,
            name: item.product.name,
            price: item.product.price,
            description: item.product.description,
            stock: item.product.stock,
            category: {
              id: item.product.category.id,
              name: item.product.category.name,
              description: item.product.category.description
            }
          },
          quantity: item.quantity
        }),
        customer: {
          id: order.customer.id,
          name: order.customer.name,
          email: order.customer.email,
          address: order.customer.address
        }
      }
      """);

    @Autowired
    public OrdersController(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/orders/{id}")
    Future<Order> get(@PathVariable String id) {
        var args = Map.of("id", id, "response", response);
        var q = fql("""
                let order: Any = Order.byId(${id})!
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return client.asyncQuery(q, Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/orders/{id}")
    Future<Order> update(@PathVariable String id, @RequestBody OrderUpdate req) {
        Map<String, Object> args = Map.of("id", id, "req", req, "response", response);

        Query query;
        if (req.status == Order.Status.processing) {
            // If the new order status is "processing" call the checkout UDF to process the checkout. The checkout
            // function definition can be found in 'server/schema/functions.fsl'. It is responsible
            // for validating that the order in a valid state to be processed and decrements the stock
            // of each product in the order. This ensures that the product stock is updated in the same transaction
            // as the order status.
            query = fql("""
                let req = ${req}
                let order: Any = checkout(${id}, req.status, req.payment)
                ${response}
                """, args);
        } else {
            // Define an FQL query to update the order. The query first retrieves the order by id
            // using the Order.byId function. If the order does not exist, Fauna will throw a document_not_found
            // error. We then use the validateOrderStatusTransition UDF to ensure that the order status transition
            // is valid. If the transition is not valid, the UDF will throw an abort error.
            query = fql("""
                let order: Any = Order.byId(${id})!
                let req = ${req}

                // Validate the order status transition if a status is provided.
                if (req.status != null) {
                  validateOrderStatusTransition(order!.status, req.status)
                }

                // If the order status is not "cart" and a payment is provided, throw an error.
                if (order!.status != "cart" && req.payment != null) {
                  abort("Can not update payment information after an order has been placed.")
                }

                // Update the order with the new status and payment information.
                order.update(req)

                // Return the order.
                ${response}
                """, args);
        }

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return client.asyncQuery(query, Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/customers/{id}/orders")
    Future<Page<Order>> getByCustomer(@PathVariable("id") String customerId, @RequestParam(required = false) String afterToken, @RequestParam(required = false) Integer pageSize) {
        Query query;
        if (afterToken != null) {
            query = fql("Set.paginate(${afterToken})", Map.of("afterToken", afterToken));
        } else {
            pageSize = pageSize != null ? pageSize : 10;

            // Define an FQL query to retrieve a page of orders for a given customer.
            // Get the Customer document by id, using the ! operator to assert that the document exists.
            // If the document does not exist, Fauna will throw a document_not_found error. We then
            // use the Order.byCustomer index to retrieve all orders for that customer and map over
            // the results to return only the fields we care about.
            var args = Map.of("customerId", customerId,"pageSize", pageSize,"response", response);
            query = fql("""
                let customer: Any = Customer.byId(${customerId})!
                Order.byCustomer(customer).pageSize(${pageSize}).map((order) => {
                  let order: Any = order

                  // Return the order.
                  ${response}
                })
                """, args);
        }

        // Connect to fauna using the client. The paginate method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return CompletableFuture.completedFuture(client.paginate(query, Order.class).next());
    }

    @Async
    @PostMapping("/customers/{id}/cart")
    Future<Order> createCart(@PathVariable("id") String customerId) {
        // Call our getOrCreateCart UDF to get the customer's cart. The function
        // definition can be found 'server/schema/functions.fsl'.
        Map<String, Object> args = Map.of("customerId", customerId, "response", response);
        Query query = fql("""
                let order: Any = getOrCreateCart(${customerId})

                // Return the cart.
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return client.asyncQuery(query, Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/customers/{id}/cart/item")
    Future<Order> addToCart(@PathVariable("id") String customerId, @RequestBody AddToCartRequest req) {

        // Call our createOrUpdateCartItem UDF to add an item to the customer's cart. The function
        // definition can be found 'server/schema/functions.fsl'.
        Map<String, Object> args = Map.of("customerId", customerId, "req", req, "response", response);
        Query query = fql("""
                let req = ${req}
                let order: Any = createOrUpdateCartItem(${customerId}, req.productName, req.quantity)

                // Return the cart as an OrderResponse object.
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return client.asyncQuery(query, Order.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/customers/{id}/cart")
    Future<Order> getCart(@PathVariable("id") String customerId) {

        // Get the customer's cart by id, using the ! operator to assert that the document exists.
        // If the document does not exist, Fauna will throw a document_not_found error.
        Map<String, Object> args = Map.of("customerId", customerId, "response", response);
        Query query = fql("""
                let order: Any = Customer.byId(${customerId})!.cart

                // Return the cart as an OrderResponse object.
                ${response}
                """, args);

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Order.class to specify that the query will return a single
        // item representing an Order.
        return client.asyncQuery(query, Order.class).thenApply(QuerySuccess::getData);
    }
}
