## Create the input topic
  kafka/bin/kafka-topics.sh  --create --zookeeper localhost:2181 \
          --replication-factor 1 --partitions 1 \
          --topic rupesh-input
- Created topic "rupesh-input".
- Create the output topic if feeding back to kafka

## List Topics
./bin/kafka-topics.sh --zookeeper 192.168.1.200:2181 --list  

## Pipe Input Files
cat /data/file-input.txt | ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic rupesh-input

./bin/kafka-console-producer.sh --broker-list 192.168.1.200:9092 --topic rupesh-input < data/access_log.txt  

## Consume and Output to terminal
./bin/kafka-console-consumer.sh --bootstrap-server 192.168.1.200:9092 \
        --topic rupesh-input \
        --from-beginning 
