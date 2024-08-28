package fauna.sample.controllers.products;

import com.fauna.client.FaunaClient;
import com.fauna.response.QuerySuccess;
import com.fauna.types.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.fauna.query.builder.Query.fql;

@Controller
public class Products {

    static class SearchRequest {
        public Integer minPrice;
        public Integer maxPrice;
        public Integer pageSize;
        public String nextToken;
    }

    static class ProductRequest {
        public String name;
        public String description;
        public Integer price;
        public Integer stock;
        public Product.Category category;
    }

    private final FaunaClient client;

    @Autowired
    public Products(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/products")
    Future<Page<Product>> paginate(
            @RequestParam String category,
            @RequestParam String nextToken,
            @RequestParam String pageSize) {
        return CompletableFuture.completedFuture(client.paginate(fql("[]"), Product.class).next());
    }

    @Async
    @PostMapping("/products")
    Future<Product> create(@RequestBody Product product) {
        return client.asyncQuery(fql("{}"), Product.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/products/{id}")
    Future<Product> create(@PathVariable String id, @RequestBody ProductRequest req) {
        return client.asyncQuery(fql("{}"), Product.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/products/search")
    Future<Page<Product>> search(@RequestBody SearchRequest req) {
        return CompletableFuture.completedFuture(client.paginate(fql("{}"), Product.class).next());
    }
}
