DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

cp ./test/local-project .fauna-project

fauna endpoint add local --url "$LOCAL_ENDPOINT" --secret "$SECRET"
fauna endpoint select local

fauna create-database "$DB_NAME"
