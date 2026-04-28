package com.rabs.library_events_producer.config;

import jakarta.validation.Valid;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

    @Value("${spring.kafka.topic.name}")
    public String topicName;

    @Value("${spring.kafka.topic.replicas}")
    public Integer topicReplicas;

    @Value("${spring.kafka.topic.partitions}")
    public Integer topicPartitions;

    /*@Bean
    public NewTopic libraryEvents(){
        return TopicBuilder.name(topicName)
                .partitions(topicPartitions)
                .replicas(topicReplicas)
                .build();
    }*/
}
