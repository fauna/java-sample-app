#! /bin/sh

ENDPOINT="http://localhost:8080"

curl -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer secret" \
    --connect-timeout 5 --max-time 10 --retry 5 --retry-delay 1 --retry-max-time 60 \
    --retry-all-errors "$ENDPOINT/products?pageSize=1"


