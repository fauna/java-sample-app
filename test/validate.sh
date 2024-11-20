#! /bin/sh

ENDPOINT="http://localhost:8080"

curl -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer secret" "$ENDPOINT/products?pageSize=1"


