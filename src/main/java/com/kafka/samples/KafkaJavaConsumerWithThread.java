package com.kafka.samples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaJavaConsumerWithThread {
	static Logger logger = LoggerFactory.getLogger(KafkaJavaConsumerWithThread.class.getName());

	public static void main(String[] args) {
		new KafkaJavaConsumerWithThread().run();
	}

	public void run() {
		String bootstrapServer = "127.0.0.1:9092";
		String consumerGroudID = "my-java-consumer-application-1";
		String topic = "first_topic";

		// latch for dealing with multiple threads
		CountDownLatch latch = new CountDownLatch(1);

		// instantiate the runnable class
		ConsumerRunnable myConsumerRunnable = new ConsumerRunnable(bootstrapServer, consumerGroudID, topic, latch);

		// create a thread to execute the runnable class code
		Thread myRunnableConsumerThread = new Thread(myConsumerRunnable);

		// start the thread
		myRunnableConsumerThread.start();

		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Caught Shutdown hook");
			myConsumerRunnable.shutdown();
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("Application is interuppted", e);
		} finally {

		}
	}

	public class ConsumerRunnable implements Runnable {

		private KafkaConsumer<String, String> consumer;
		private CountDownLatch latch;

		public ConsumerRunnable(String bootstrapServer, String consumerGroudID, String topic, CountDownLatch latch) {
			this.latch = latch;
			// Create Consumer Properties
			Properties consumerProps = new Properties();
			consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
			consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class.getName());
			// specify the consumer group it belongs to
			consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroudID);
			// it can be earliest/latest/none -> earliest: resets the offset to the earliest
			// offset
			consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			// Create Consumer
			consumer = new KafkaConsumer<String, String>(consumerProps);

			// subscribe to topic
			consumer.subscribe(Arrays.asList(topic));
		}

		public void run() {
			try {
				while (true) {
					// Poll for Consumer records
					ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

					for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {

						logger.info("key: " + consumerRecord.key() + ", value: " + consumerRecord.value());
						logger.info(
								"partition: " + consumerRecord.partition() + ", offset: " + consumerRecord.offset());

					}
				}
			} catch (WakeupException e) {
				logger.info("Received Shutdown signal!");
			} finally {
				consumer.close();
				// tell our main code that we are done with this consumer
				latch.countDown();
			}

		}

		public void shutdown() {
			// the wakeup() is a special method to interrupt the consumer.poll()
			// this will throw an exception - WakeUpException
			consumer.wakeup();
		}
	}
}
