#! /bin/sh

DB_NAME="ECommerceJava"
LOCAL_ENDPOINT="http://localhost:8443/"
SECRET="secret"

echo "Copied .fauna-project"

fauna endpoint add local -y --set-default --url "$LOCAL_ENDPOINT" --secret "$SECRET"

echo "Added local endpoint"

fauna create-database "$DB_NAME"

fauna environment add --name local --endpoint local --database $DB_NAME -y
fauna environment select local
fauna eval "Key.create({ role: 'server' }).secret" | xargs > .fauna_key

fauna schema push -y

OUTPUT="";
while [ `echo $OUTPUT | grep -c "Staged status: ready"` = 0 ]; do
  OUTPUT=`fauna schema status`;
done

fauna schema commit -y

