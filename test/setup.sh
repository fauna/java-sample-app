#! /bin/sh

DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

cp ./test/local-project .fauna-project
echo "Copied .fauna-project"

fauna endpoint add local -y --set-default --url "$LOCAL_ENDPOINT" --secret "$SECRET"
echo "Added local endpoint"

fauna create-database "$DB_NAME"
echo "Created database $DB_NAME"

fauna environment add --name local --endpoint local --database $DB_NAME -y
fauna environment select local
fauna eval "Key.create({ role: 'server' }).secret" | xargs > .fauna_key

fauna schema push -y --active

fauna import --collection Category --path seed/categories.json
fauna import --collection Customer --path seed/customers.json
fauna eval --file seed/products.fql
fauna eval --file seed/orders.fql
