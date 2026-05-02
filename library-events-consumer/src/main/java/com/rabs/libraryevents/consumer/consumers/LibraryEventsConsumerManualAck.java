package com.rabs.libraryevents.consumer.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
//@Slf4j
public class LibraryEventsConsumerManualAck implements AcknowledgingMessageListener<Integer, String> {


    @Override
    @KafkaListener(topics = {"${spring.kafka.topic.name}"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, @Nullable Acknowledgment acknowledgment) {
        //log.info("Consumer Record: {}", consumerRecord);

        acknowledgment.acknowledge();
    }


}