package com.kafka.samples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaJavaConsumer {
	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(KafkaJavaConsumer.class.getName());

		// Create Consumer Properties
		Properties consumerProps = new Properties();
		consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			//specify the consumer group it belongs to
		consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-java-consumer-application");
			//it can be earliest/latest/none -> earliest: resets the offset to the earliest offset
		consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// Create Consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

		//subscribe to topic
		consumer.subscribe(Arrays.asList("twitter"));
		
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
