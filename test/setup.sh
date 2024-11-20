#! /bin/sh

DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

cp ./test/local-project .fauna-project

echo "Copied .fauna-project"

/usr/local/bin/fauna endpoint add local -y --set-default --url "$LOCAL_ENDPOINT" --secret "$SECRET"
echo "Added local endpoint"

fauna create-database "$DB_NAME"

echo "Created database"
