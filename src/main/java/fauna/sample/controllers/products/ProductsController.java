package fauna.sample.controllers.products;

import com.fauna.client.FaunaClient;
import com.fauna.query.builder.Query;
import com.fauna.response.QuerySuccess;
import com.fauna.types.Page;
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
public class ProductsController {

    public static class ProductRequest {
        private String name;
        private String description;
        private Integer price;
        private Integer stock;
        private Product.Category category;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Integer getPrice() {
            return price;
        }

        public Integer getStock() {
            return stock;
        }

        public Product.Category getCategory() {
            return category;
        }
    }

    private final FaunaClient client;

    @Autowired
    public ProductsController(FaunaClient client) {
        this.client = client;
    }

    @Async
    @GetMapping("/products")
    Future<Page<Product>> paginate(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String afterToken,
            @RequestParam(required = false) Integer pageSize) {

        Query query;
        pageSize = pageSize != null ? pageSize : 10;

        if (afterToken != null) {
            query = fql("Set.paginate(${afterToken})", Map.of("afterToken", afterToken));
        } else {
            Query subQuery;
            if (category == null)
                subQuery = fql(
                        "Product.sortedByCategory().pageSize(${pageSize})",
                        Map.of("pageSize", pageSize));
            else {
                subQuery = fql(
                        "Product.byCategory(Category.byName(${category}).first()).pageSize(${pageSize})",
                        Map.of("category", category, "pageSize", pageSize));
            }

            query = fql("""
                    // Return only the fields we want to display to the user
                    // by mapping over the data returned by the index and returning a
                    // new object with the desired fields.
                    ${subQuery}
                      .map(product => {
                         let product: Any = product
                         let category: Any = product.category
                         {
                           id: product.id,
                           name: product.name,
                           price: product.price,
                           description: product.description,
                           stock: product.stock,
                           category: { id: category.id, name: category.name, description: category.description },
                         }
                       })
                    """, Map.of("subQuery", subQuery));
        }

        return CompletableFuture.completedFuture(client.paginate(query, Product.class).next());
    }

    @Async
    @PostMapping("/products")
    Future<Product> create(@RequestBody CreateProductRequest req) {
        // TODO: Validate product
        Map<String,Object> args = Map.of("req", req);

        Query query = fql("""
                  let input = ${req};
                  
                  // Get the category by name. We can use .first() here because we know that the category
                  // name is unique.
                  let category = Category.byName(input.category).first()

                  // If the category does not exist, abort the transaction.
                  if (category == null) abort("Category does not exist.")

                  // Create the product with the given values.
                  let args = { name: input.name, price: input.price, stock: input.stock, description: input.description, category: category }
                  let product: Any = Product.create(args)

                  // Use projection to only return the fields you need.
                  product {
                    id,
                    name,
                    price,
                    description,
                    stock,
                    category {
                      id,
                      name,
                      description
                    }
                  }
                  """, args);

        return client.asyncQuery(query, Product.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/products/{id}")
    Future<Product> create(@PathVariable String id, @RequestBody ProductRequest req) {
        return client.asyncQuery(fql("{}"), Product.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @GetMapping("/products/search")
    Future<Page<Product>> search(@RequestParam(required = false) Integer minPrice,
                                 @RequestParam(required = false) Integer maxPrice,
                                 @RequestParam(required = false) String afterToken,
                                 @RequestParam(required = false) Integer pageSize) {
        minPrice = minPrice != null ? minPrice : 0;
        maxPrice = maxPrice != null ? maxPrice : 10000;
        pageSize = pageSize != null ? pageSize : 10;

        Query query;
        if (afterToken != null) {
            query = fql("Set.paginate(${afterToken})", Map.of("afterToken", afterToken));
        } else {
            // This is an example of a covered query.  A covered query is a query where all fields
            // returned are indexed fields. In this case, we are querying the Product collection
            // for products with a price between minPrice and maxPrice. We are also limiting the
            // number of results returned to the limit parameter. The query is covered because
            // all fields returned are indexed fields. In this case, the fields returned are
            // `name`, `description`, `price`, and `stock` are all indexed fields.
            // Covered queries are typically faster and less expensive than uncovered queries,
            // which require document reads.
            // Learn more about covered queries here: https://docs.fauna.com/fauna/current/learn/data_model/indexes#covered-queries
            Map<String, Object> args = Map.of("minPrice", minPrice,"maxPrice", maxPrice, "pageSize", pageSize);
            query = fql("""
                Product.sortedByPriceLowToHigh({ from: ${minPrice}, to: ${maxPrice}})
                .pageSize(${pageSize}) {
                  id,
                  name,
                  price,
                  description,
                  stock,
                  category {
                    id,
                    name,
                    description
                  }
                }
                """, args);
        }

        return CompletableFuture.completedFuture(client.paginate(query, Product.class).next());
    }
}
