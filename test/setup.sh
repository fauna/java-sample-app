#! /bin/sh

DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

cp ./test/local-project .fauna-project

echo "Copied .fauna-project"

fauna endpoint add local --url "$LOCAL_ENDPOINT" --secret "$SECRET"
echo "Added local endpoint"
fauna endpoint select local
echo "Selected local endpoint"

fauna create-database "$DB_NAME"