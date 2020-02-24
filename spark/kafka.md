Create the input topic
  kafka/bin/kafka-topics.sh  --create --zookeeper localhost:2181 \
          --replication-factor 1 --partitions 1 \
          --topic streams-plaintext-input
Created topic "streams-plaintext-input".
Create the output topic
  kafka/bin/kafka-topics.sh --create \ --zookeeper localhost:2181 \
          --replication-factor 1 \ --partitions 1 \
          --topic streams-wordcount-output
List Topics
./bin/kafka-topics.sh --zookeeper 192.168.1.200:2181 --list
streams-plaintext-input
Streams-wordcount-output
Pipe Input Files
cat /data/file-input.txt | ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
./bin/kafka-console-producer.sh --broker-list 192.168.1.200:9092 --topic rtr-spark-topic
< data/access_log.txt
Consume and Output to terminal
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --topic streams-wordcount-output \
        --from-beginning 
