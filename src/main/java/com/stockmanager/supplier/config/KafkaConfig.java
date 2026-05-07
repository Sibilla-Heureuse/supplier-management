package com.stockmanager.supplier.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.supplier-events}")
    private String supplierEventsTopic;

    @Bean
    public NewTopic supplierEventsTopic() {
        return TopicBuilder.name(supplierEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
