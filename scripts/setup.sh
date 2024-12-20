#! /bin/sh

DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

touch .fauna-project

fauna endpoint add local -y --set-default --url "$LOCAL_ENDPOINT" --secret "$SECRET"
echo "Added local endpoint"

fauna create-database "$DB_NAME"
echo "Created database $DB_NAME"

fauna environment add --name local --endpoint local --database $DB_NAME -y
fauna environment select local
fauna eval "Key.create({ role: 'server' }).secret" | xargs > .fauna_key

fauna schema push -y --active --dir=schema

fauna eval --file seed/categories.fql
fauna eval --file seed/customers.fql
fauna eval --file seed/products.fql
fauna eval --file seed/orders.fql
