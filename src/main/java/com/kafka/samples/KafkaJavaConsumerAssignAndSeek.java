package com.kafka.samples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaJavaConsumerAssignAndSeek {
	public static void main(String[] args) {

		/* 
		 * Assign and Seek option is to read from a specific partition of a topic and to seek (read) from specific offset
		 * 
		 * No Group has to be specified! And No need to subscribe to topic
		 * 
		 */
			
		
		Logger logger = LoggerFactory.getLogger(KafkaJavaConsumerAssignAndSeek.class.getName());

		// Create Consumer Properties
		Properties consumerProps = new Properties();
		consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			//it can be earliest/latest/none -> earliest: resets the offset to the earliest offset
		consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// Create Consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

		//create a topic partition		
		String topic = "first_topic";		
		int partitionNumber = 0;		
		TopicPartition partitionToReadFrom = new TopicPartition(topic, partitionNumber);
		consumer.assign(Arrays.asList(partitionToReadFrom));		
		
		// seek - offset to read from
		long offsetToReadFrom = 15L;
		consumer.seek(partitionToReadFrom, offsetToReadFrom);
		
		while (true) {
			// Poll for Consumer records
			ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {

				logger.info("key: " + consumerRecord.key() + ", value: " + consumerRecord.value());
				logger.info("partition: " + consumerRecord.partition() + ", offset: " + consumerRecord.offset());

			}
		}
	}
}
