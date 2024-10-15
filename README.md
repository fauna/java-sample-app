# Fauna Java sample app

This sample app shows how to use [Fauna](https://fauna.com) in a production
application.

The app uses Java 17 with Gradle 8.9 and the [Fauna v10 JVM
driver](https://github.com/fauna/fauna-jvm) to create HTTP API endpoints for an
e-commerce store. You can use the app's API endpoints to manage products,
customers, and orders for the store.

The app uses Fauna schemas and queries to:

- Read and write data with strong consistency.

- Define and handle relationships between resources, such as linking orders
  to products and customers.

- Validate data changes against business logic.

The app's source code includes comments that highlight Fauna best practices.


## Highlights

The sample app uses the following Fauna features:

- **[Document type
  enforcement](https://docs.fauna.com/fauna/current/learn/schema/#type-enforcement):**
  Collection schemas enforce a structure for the app's documents. Fauna rejects
  document writes that don't conform to the schema, ensuring data consistency.
  [Zero-downtime
  migrations](https://docs.fauna.com/fauna/current/learn/schema/#schema-migrations)
  let you safely change the schemas at any time.

- **[Relationships](https://docs.fauna.com/fauna/current/learn/query/relationships/):**
  Normalized references link documents across collections. The app's queries use
  [projection](https://docs.fauna.com/fauna/current/reference/fql/projection/)
  to dynamically retrieve linked documents, even when deeply nested. No complex
  joins, aggregations, or duplication needed.

- **[Computed
  fields](https://docs.fauna.com/fauna/current/learn/schema/#computed-fields):**
  Computed fields dynamically calculate their values at query time. For example,
  each customer's `orders` field uses a query to fetch a set of filtered orders.
  Similarly, each order's `total` is calculated at query time based on linked
  product prices and quantity.

- **[Constraints](https://docs.fauna.com/fauna/current/learn/schema/#unique-constraints):**
  The app uses constraints to ensure field values are valid. For example, the
  app uses unique constraints to ensure each customer has a unique email address
  and each product has a unique name. Similarly, check constraints ensure each
  customer has only one cart at a time and that product prices are not negative.

- **[User-defined functions
  (UDFs)](https://docs.fauna.com/fauna/current/learn/data-model/user-defined-functions/):**
  The app uses UDFs to store business logic as reusable queries. For example,
  the app uses a `checkout()` UDF to process order updates. `checkout()` calls
  another UDF, `validateOrderStatusTransition()`, to validate `status`
  transitions for orders.


## Requirements

To run the app, you'll need:

- A [Fauna account](https://dashboard.fauna.com/register). You can sign up for a
  free account at https://dashboard.fauna.com/register.

- Your preferred flavor of Java 17

- The [Fauna CLI](https://docs.fauna.com/fauna/current/tools/shell/). To install
  the CLI, run:

    ```sh
    npm install -g fauna-shell
    ```

You should also be familiar with basic Fauna CLI commands and usage. For an
overview, see the [Fauna CLI
docs](https://docs.fauna.com/fauna/current/tools/shell/).


## Setup

1. Clone the repo and navigate to the `java-sample-app` directory:

    ```sh
    git clone git@github.com:fauna/java-sample-app.git
    cd java-sample-app
    ```

   The repo includes a
   [`.fauna-project`](https://docs.fauna.com/fauna/current/tools/shell/#proj-config)
   file that contains defaults for the Fauna CLI. The file indicates:

    - `ECommerceJava` is the default database for the project.

    - The project stores Fauna Schema Language (FSL) files in the
      `schema` directory.

2. Log in to Fauna using the Fauna CLI:

    ```sh
    fauna cloud-login
    ```

  When prompted, enter:

    * **Endpoint name:** `cloud` (Press Enter)
    * **Email address:** The email address for your Fauna account.
    * **Password:** The password for your Fauna account.
    * **Which endpoint would you like to set as default?** The `cloud-*`
      endpoint for your preferred region group. For example, to use the US
      region group, use `cloud-us`.

   The command requires an email and password login. If you log in to the Fauna
   using GitHub or Netlify, you can enable email and password login using the
   [Forgot Password](https://dashboard.fauna.com/forgot-password) workflow.

3. Use the Fauna CLI to create the `ECommerceJava` database:

    ```sh
    fauna create-database --environment='' ECommerceJava
    ```

4. Create a
   [`.fauna-project`](https://docs.fauna.com/fauna/current/tools/shell/#proj-config)
   config file for the project:

   ```sh
   fauna project init
   ```

   When prompted, enter:

    * `schema` as the schema directory.
    * `dev` as the environment name.
    * The default endpoint.
    * `ECommerce` as the database.

5.  Push the `.fsl` files in the `schema` directory to the `ECommerceJava`
    database:

    ```sh
    fauna schema push
    ```

    When prompted, accept and stage the schema.

6.  Check the status of the staged schema:

    ```sh
    fauna schema status
    ```

7.  When the status is `ready`, commit the staged schema to the database:

    ```sh
    fauna schema commit
    ```

    The commit applies the staged schema to the database. The commit creates the
    collections and user-defined functions (UDFs) defined in the `.fsl` files of the
    `schema` directory.

8. Create a key with the `server` role for the `ECommerceJava` database:

    ```sh
    fauna create-key --environment='' ECommerceJava server
    ```

   Copy the returned `secret`. The app can use the key's secret to authenticate
   requests to the database.

## Add sample data

The app includes a setup script that adds sample documents to the
`ECommerceJava` database. From the root directory, run:

```sh
FAUNA_SECRET=<secret> ./setup.sh
```

You can view documents created by the script in the [Fauna
Dashboard](https://dashboard.fauna.com/).


## Run the app

The app runs an HTTP API server. From the root directory, run:

```sh
FAUNA_SECRET=<secret> ./gradlew bootRun
```

Once started, the local server is available at http://localhost:8080.


## HTTP API endpoints

The app's HTTP API endpoints are defined in `*Controller` files in the
`java.sample.controllers.*` modules.

An OpenAPI spec and Swagger UI docs for the endpoints are available at:

* OpenAPI spec: http://localhost:8080/v3/api-docs
* Swagger UI: http://localhost:8080/swagger-ui.html


### Make API requests

You can use the endpoints to make API requests that read and write data from
the `ECommerceJava` database.

For example, with the local server running in a separate terminal tab, run the
following curl request to the `POST /products` endpoint. The request creates a
`Product` collection document in the `ECommerceJava` database.

```sh
curl -v \
  http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "The Old Man and the Sea",
    "price": 899,
    "description": "A book by Ernest Hemingway",
    "stockQuantity": 10,
    "category": "books"
  }' | jq .
```


## Expand the app

You can further expand the app by adding fields and endpoints.

As an example, the following steps adds a computed `totalPurchaseAmt` field to
Customer documents and related API responses:

1. If you haven't already, add the sample data:

    ```sh
    FAUNA_SECRET=<secret> ./setup.sh
    ```

    If the app server is running, stop the server by pressing Ctrl+C.

2. In `schema/collections.fsl`, add the following `totalPurchaseAmt` computed
   field definition to the `Customer` collection:

    ```diff
    collection Customer {
      ...
      // Use a computed field to get the set of Orders for a customer.
      compute orders: Set<Order> = (customer => Order.byCustomer(customer))

    + // Use a computed field to calculate the customer's cumulative purchase total.
    + // The field sums purchase `total` values from the customer's linked Order documents.
    + compute totalPurchaseAmt: Number = (customer => customer.orders.fold(0, (sum, order) => {
    +   let order: Any = order
    +   sum + order.total
    + }))
      ...
    }
    ...
    ```

   Save `schema/collections.fsl`.

3.  Push the updated `.fsl` files in the `schema` directory to the `ECommerceJava`
    database to stage the changes:

    ```sh
    fauna schema push
    ```

    When prompted, accept and stage the schema.

4.  Check the status of the staged schema:
    ```sh
    fauna schema status
    ```

5.  When the status is `ready`, commit the staged schema changes to the
    database:

    ```sh
    fauna schema commit
    ```

6. In `CustomersController.java`, add the
   `totalPurchaseAmt` field to the `response` FQL template:

    ```diff
    // Project Customer document fields for consistent responses.
    private final Query response = fql("""
              customer {
                id,
                name,
                email,
    +           address,
    +           totalPurchaseAmt
              }
            """);
    `;
    ```

   Save `CustomersController.java`.

   Customer-related endpoints use this template to project Customer
   document fields in responses.

7. In `Customer.java`, add the
   `totalPurchaseAmt` field and a related getter
   to the `Customer` class:

    ```diff
    private String email;
    private Address address;
    + private int totalPurchaseAmt;
    +
    + public int getTotalPurchaseAmt() {
    +     return totalPurchaseAmt;
    + }

    public String getId() {
        return id;
    }
    ```

   Save `Customer.java`.

   Customer-related endpoints return responses that
   conform to the `Customer` class.

8. Start the app server:

    ```sh
    FAUNA_SECRET=<secret> ./gradlew bootRun
    ```

9.  With the local server running in a separate terminal tab, run the
    following curl request to the `POST /customers` endpoint:

    ```sh
    curl -v http://localhost:8080/customers/999 | jq .
    ```

    The response includes the computed `totalPurchaseAmt` field:

    ```json
    {
      "id": "999",
      "name": "Valued Customer",
      "email": "fake@fauna.com",
      "address": {
        "street": "Herengracht",
        "city": "Amsterdam",
        "state": "North Holland",
        "postalCode": "1015BT",
        "country": "Netherlands"
      },
      "totalPurchaseAmt": 36000
    }
    ```
