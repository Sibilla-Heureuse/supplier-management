package com.stockmanager.supplier.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockmanager.supplier.entity.Supplier;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Kafka event model published for every Supplier operation.
 * Used for audit trails, notifications, and downstream integrations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierEvent {

    /** Unique event identifier (UUID string). */
    private String eventId;

    /** Type of operation that triggered this event. */
    private EventType eventType;

    /** The supplier ID this event relates to. */
    private Long supplierId;

    /** Supplier code for quick identification in consumers. */
    private String supplierCode;

    /** Supplier name snapshot at the time of the event. */
    private String supplierName;

    /** Supplier email snapshot. */
    private String supplierEmail;

    /** Supplier status at the time of the event. */
    private Supplier.SupplierStatus supplierStatus;

    /** ISO timestamp of when the event was created. */
    private LocalDateTime eventTimestamp;

    /** Service or component that published the event. */
    private String source;

    /** Optional metadata (e.g. which fields changed on UPDATE). */
    private String metadata;

    /**
     * Enum representing the type of Supplier operation.
     */
    public enum EventType {
        SUPPLIER_CREATED,
        SUPPLIER_UPDATED,
        SUPPLIER_RETRIEVED,
        SUPPLIER_DELETED
    }
}