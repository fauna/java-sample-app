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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static com.fauna.query.builder.Query.fql;

@RestController
public class ProductsController {

    private final FaunaClient client;

    private static final Logger logger = Logger.getLogger(ProductsController.class.getName());

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
            // Decode the afterToken (equivalent to decodeURIComponent) without needing a try-catch block
            String decodedAfterToken = URLDecoder.decode(afterToken, StandardCharsets.UTF_8);

            // Log the decoded afterToken
            logger.info("Decoded afterToken: " + decodedAfterToken);

            // Use the decoded afterToken in your query
            query = fql("Set.paginate(${afterToken})", Map.of("afterToken", decodedAfterToken));
        } else {

            // Define an FQL query fragment that will return a page of products. This query
            // fragment will either return all products sorted by category or all products in a specific
            // category depending on whether the category query parameter is provided. This will later
            // be embedded in a larger query.
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

            // Define the main query. This query will return a page of products using the query fragment
            // defined above.
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

        // Connect to fauna using the client. The paginate method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Product.class to specify that the query will return a single
        // item representing a Product.
        return CompletableFuture.completedFuture(client.paginate(query, Product.class).next());
    }

    @Async
    @PostMapping("/products")
    Future<Product> create(@RequestBody ProductRequest req) {
        Map<String,Object> args = Map.of("req", req);

        // Using the abort function we can throw an error if a condition is not met. In this case,
        // we check if the category exists before creating the product. If the category does not exist,
        // fauna will throw an AbortError which we can handle in our catch block.
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

        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Product.class to specify that the query will return a single
        // item representing a Product.
        return client.asyncQuery(query, Product.class).thenApply(QuerySuccess::getData);
    }

    @Async
    @PostMapping("/products/{id}")
    Future<Product> update(@PathVariable String id, @RequestBody ProductRequest req) {
        Map<String, Object> args = Map.of("req", req, "id", id);

        Query query = fql("""
                          let input = ${req};

                          // Get the product by id, using the ! operator to assert that the product exists.
                          // If it does not exist Fauna will throw a document_not_found error.
                          let product: Any = Product.byId(${id})!

                          // Get the category by name. We can use .first() here because we know that the category
                          // name is unique.
                          let category = Category.byName(input.category ?? "").first()

                          // If a category was provided and it does not exist, abort the transaction.
                          if (input.category != null && category == null) abort("Category does not exist.")

                          let fields = { name: input.name, price: input.price, stock: input.stock, description: input.description }

                          if (category != null) {
                            // If a category was provided, update the product with the new category document as well as
                            // any other fields that were provided.
                            product!.update(Object.assign(fields, { category: category }))
                          } else {
                            // If no category was provided, update the product with the fields that were provided.
                            product!.update(fields)
                          }

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


        // Connect to fauna using the client. The query method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Product.class to specify that the query will return a single
        // item representing a Product.
        return client.asyncQuery(query, Product.class).thenApply(QuerySuccess::getData);
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
                .pageSize(${pageSize}).map(product => {
                    let product: Any = product
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
                  })
                """, args);
        }

        // Connect to fauna using the client. The paginate method accepts an FQL query
        // as a parameter as well as an optional return type. In this case, we are
        // using the Product.class to specify that the query will return a single
        // item representing a Product.
        return CompletableFuture.completedFuture(client.paginate(query, Product.class).next());
    }
}
