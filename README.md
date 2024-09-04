### Quick Start

1. Clone this repo
2. Install fauna shell `npm install -g fauna-shell`
3. Log in to Fauna `fauna cloud-login`
4. Create a database `fauna create-database --environment='' ECommerceJava`
5. Push the schema `fauna schema push`
2. Create a secret `fauna create-key --environment='' ECommerceJava server`
3. Seed some data `FAUNA_SECRET=fnAFqe30pHAAQmCrf0ccbGM_rvy3EOGekowo-S2J ./setup.sh`
4. Start the service `FAUNA_SECRET=fnAFqe30pHAAQmCrf0ccbGM_rvy3EOGekowo-S2J ./gradlew bootRun`
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
* Explore the rest of the APIs to see what you can do
* Maybe try to implement some error handling
* Find bugs!