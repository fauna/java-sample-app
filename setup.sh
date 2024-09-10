
EP="${FAUNA_ENDPOINT:-https://db.fauna.com}"

cat seed/categories.fql | curl -X POST -u "$FAUNA_SECRET": "$EP/query/1" -H 'Content-Type: application/json' -d@-
echo -e "\n\n"
cat seed/customers.fql | curl -X POST -u "$FAUNA_SECRET": "$EP/query/1" -H 'Content-Type: application/json' -d@-
echo -e "\n\n"
cat seed/products.fql | curl -X POST -u "$FAUNA_SECRET": "$EP/query/1" -H 'Content-Type: application/json' -d@-
echo -e "\n\n"
cat seed/orders.fql | curl -X POST -u "$FAUNA_SECRET": "$EP/query/1" -H 'Content-Type: application/json' -d@-
