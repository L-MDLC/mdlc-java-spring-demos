#!/bin/bash
set -e

echo "Waiting for MongoDB to be reachable..."
until curl -s mongo-replica-set:27017 > /dev/null; do
  echo "MongoDB not reachable yet. Waiting..."
  sleep 5
done

echo "Waiting for replica set initialization..."
sleep 5

echo "Installing MongoDB connector..."
confluent-hub install --no-prompt mongodb/kafka-connect-mongodb:latest

echo "Starting Kafka Connect..."
/etc/confluent/docker/run &

echo "Waiting for Kafka Connect to be ready..."
until curl -s -f http://localhost:8083/connectors > /dev/null; do
  echo "Kafka Connect not ready yet. Waiting..."
  sleep 5
done

echo "Configuring MongoDB source connector..."
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "mongodb-source",
  "config": {
    "connector.class": "com.mongodb.kafka.connect.MongoSourceConnector",
	  "connection.uri": "mongodb://mongo-replica-set:27017",
    "database": "testDB",
    "collection": "testCollection",
    "pipeline": "[]",
    "topic.prefix": "mongodb",
	  "topic.creation.enable": "true",
	  "topic.creation.default.replication.factor": 1,
    "topic.creation.default.partitions": 1,
    "topic.creation.default.cleanup.policy": "compact",
    "poll.max.batch.size": 1000,
    "poll.await.time.ms": 5000,
    "publish.full.document.only": "true",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "transforms": "business",
    "transforms.business.type": "edu.lmdlc.demo.kafka.connect.transforms.SMTDataTransformer"
  }
}'

echo "Configuring File sink connector..."
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "local-file-sink",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSinkConnector",
    "topics": "mongodb.testDB.testCollection",
    "file": "/data/mongodb-changes.txt",
    "tasks.max": "1",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false"
  }
}'

echo "<!> Kafka-Connect SETUP DONE ! <!>"

# Keep container running
tail -f /dev/null
