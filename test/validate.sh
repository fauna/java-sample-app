#! /bin/sh

ENDPOINT="http://localhost:8080"
ACCEPT="Accept: application/json"
CONTENT="Content-Type: application/json"

curl --silent -H "$ACCEPT" -H "$CONTENT" --retry-all-errors \
    --connect-timeout 5 --max-time 10 --retry 5 --retry-delay 10 --retry-max-time 60 \
    "$ENDPOINT/products?pageSize=1" > page_one.json
cat one.json

AFTER=`jq '.after | .token' page_one.json | xargs`

curl --silent -H "$ACCEPT" -H "$CONTENT" --silent \
    "$ENDPOINT/products?pageSize=1&afterToken=$AFTER" > page_two.json

jq '.data | .[] | .name' page_two.json

curl --silent -H "$ACCEPT" -H "$CONTENT" "$ENDPOINT/products/search?minPrice=1000&maxPrice=10000" > search.json

jq '.data | .[] | .name' search.json

NEW_PRODUCT="{ \"name\": \"Coolest toy\", \"description\": \"All the cool kids have one.\",
               \"category\": \"electronics\", \"price\": 9999, \"stock\": 99 }"

# curl --silent -H "$ACCEPT" -H "$CONTENT" -X POST "$ENDPOINT/products" -d "$NEW_PRODUCT"

