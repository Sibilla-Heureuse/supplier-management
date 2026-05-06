package com.stockmanager.supplier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer responsible for publishing SupplierEvent messages
 * to the supplier-events topic for audit, notification, and integration purposes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierKafkaProducer {

    private final KafkaTemplate<String, SupplierEvent> kafkaTemplate;

    @Value("${kafka.topic.supplier-events}")
    private String supplierEventsTopic;

    /**
     * Publishes a SupplierEvent to the Kafka topic asynchronously.
     *
     * @param event the supplier event to publish
     */
    public void publishSupplierEvent(SupplierEvent event) {
        log.info("Publishing Kafka event | Topic: {} | EventType: {} | SupplierId: {}",
                supplierEventsTopic, event.getEventType(), event.getSupplierId());

        // Use the supplierCode as the Kafka message key for partition affinity
        String messageKey = event.getSupplierCode() != null
                ? event.getSupplierCode()
                : String.valueOf(event.getSupplierId());

        CompletableFuture<SendResult<String, SupplierEvent>> future =
                kafkaTemplate.send(supplierEventsTopic, messageKey, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish Kafka event | EventType: {} | SupplierId: {} | Error: {}",
                        event.getEventType(), event.getSupplierId(), ex.getMessage(), ex);
            } else {
                log.info("Kafka event published successfully | EventType: {} | SupplierId: {} | Offset: {} | Partition: {}",
                        event.getEventType(),
                        event.getSupplierId(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            }
        });
    }
}