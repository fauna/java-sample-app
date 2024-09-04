### Quick Start

1. Clone this repo
2. Create a Fauna secret in a database of your choice
3. Setup the schema: `FAUNA_SECRET=<secret> ./setup.sh`
4. Start the service: `FAUNA_SECRET=<secret> ./gradlew bootRun`
5. Start exploring

##### Get a customer:
```
curl "http://localhost:8080/customers/999" | jq .
```

##### Create a new cart:
```
curl -X POST -H 'Content-Type: application/json' "http://localhost:8080/customers/999/cart" | jq.
```

##### List products: 
```
curl "http://localhost:8080/products?pageSize=2" | jq .
```

### Now what?
* And explore the rest of the APIs to see what you can do
* Maybe try to implement some error handling
* Explore/bash!