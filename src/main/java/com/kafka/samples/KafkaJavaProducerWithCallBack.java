package com.kafka.samples;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaJavaProducerWithCallBack {

	public static void main(String[] args) {
		
		final Logger logger = LoggerFactory.getLogger(KafkaJavaProducerWithCallBack.class);
		
		// create producer properties
		Properties producerProps = new Properties();
		producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProps);

		for (int i = 0; i < 10; i++) {

			// create producer record
			String topic = "first_topic";
			String value = "iteration " + i;

			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, value);

			// send data with call back
			producer.send(producerRecord,new Callback() {

				public void onCompletion(RecordMetadata metadata, Exception exception) {

					if (exception == null) {
						//successful execution
						logger.info(
								"Offset: {} \n, Partition: {} \n, Topic: {} \n, Serialized Key Size: {} \n, Serialized Value Size: {}",
								metadata.offset(), metadata.partition(), metadata.topic(), metadata.serializedKeySize(),
								metadata.serializedValueSize());
						
					} else {
						//exception thrown
						logger.error("Error while producing", exception);
					}
					
				}
			});
			
			//flush data
			producer.flush();
						
		}
		// close the producer
		producer.close();
	}

}
