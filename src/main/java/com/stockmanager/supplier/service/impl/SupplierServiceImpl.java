package com.stockmanager.supplier.service.impl;

import com.stockmanager.supplier.dto.SupplierDto;
import com.stockmanager.supplier.entity.Supplier;
import com.stockmanager.supplier.exception.DuplicateSupplierException;
import com.stockmanager.supplier.exception.SupplierNotFoundException;
import com.stockmanager.supplier.kafka.SupplierEvent;
import com.stockmanager.supplier.kafka.SupplierKafkaProducer;
import com.stockmanager.supplier.repository.SupplierRepository;
import com.stockmanager.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link SupplierService}.
 * Handles all business logic for Supplier CRUD operations
 * and publishes Kafka events after each operation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierKafkaProducer kafkaProducer;

    private static final String EVENT_SOURCE = "supplier-management-service";


    // CREATE


    @Override
    @Transactional
    public SupplierDto.Response addSupplier(SupplierDto.CreateRequest request) {
        log.info("Creating new supplier with code: {}", request.getSupplierCode());

        // Validate uniqueness
        if (supplierRepository.existsBySupplierCode(request.getSupplierCode())) {
            throw new DuplicateSupplierException("supplier code", request.getSupplierCode());
        }
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateSupplierException("email", request.getEmail());
        }

        // Map DTO to Entity
        Supplier supplier = Supplier.builder()
                .supplierCode(request.getSupplierCode())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .contactPerson(request.getContactPerson())
                .taxNumber(request.getTaxNumber())
                .notes(request.getNotes())
                .status(Supplier.SupplierStatus.ACTIVE)
                .build();

        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created successfully with ID: {}", saved.getId());

        // Publish Kafka event
        kafkaProducer.publishSupplierEvent(buildEvent(saved, SupplierEvent.EventType.SUPPLIER_CREATED, null));

        return SupplierDto.Response.fromEntity(saved);
    }


    // UPDATE


    @Override
    @Transactional
    public SupplierDto.Response updateSupplier(Long id, SupplierDto.UpdateRequest request) {
        log.info("Updating supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findActiveById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        // Check email uniqueness if it's being changed
        if (!supplier.getEmail().equalsIgnoreCase(request.getEmail())
                && supplierRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateSupplierException("email", request.getEmail());
        }

        // Track changed fields for metadata
        String changedFields = buildChangedFieldsMetadata(supplier, request);

        // Apply updates
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setCountry(request.getCountry());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setTaxNumber(request.getTaxNumber());
        supplier.setNotes(request.getNotes());

        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }

        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier updated successfully with ID: {}", updated.getId());

        // Publish Kafka event
        kafkaProducer.publishSupplierEvent(
                buildEvent(updated, SupplierEvent.EventType.SUPPLIER_UPDATED, changedFields));

        return SupplierDto.Response.fromEntity(updated);
    }


    // READ


    @Override
    @Transactional(readOnly = true)
    public SupplierDto.Response getSupplierById(Long id) {
        log.info("Fetching supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findActiveById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        // Publish audit event
        kafkaProducer.publishSupplierEvent(
                buildEvent(supplier, SupplierEvent.EventType.SUPPLIER_RETRIEVED, null));

        return SupplierDto.Response.fromEntity(supplier);
    }


    // DELETE (soft delete)


    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Soft-deleting supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findActiveById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        supplier.setStatus(Supplier.SupplierStatus.DELETED);
        supplier.setDeletedAt(LocalDateTime.now());

        supplierRepository.save(supplier);
        log.info("Supplier soft-deleted successfully with ID: {}", id);

        // Publish Kafka event
        kafkaProducer.publishSupplierEvent(
                buildEvent(supplier, SupplierEvent.EventType.SUPPLIER_DELETED, null));
    }


    // HELPERS


    /**
     * Builds a SupplierEvent from a given supplier entity and event type.
     */
    private SupplierEvent buildEvent(Supplier supplier, SupplierEvent.EventType eventType, String metadata) {
        return SupplierEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .supplierEmail(supplier.getEmail())
                .supplierStatus(supplier.getStatus())
                .eventTimestamp(LocalDateTime.now())
                .source(EVENT_SOURCE)
                .metadata(metadata)
                .build();
    }

    /**
     * Compares old and new supplier values to produce a change log string.
     */
    private String buildChangedFieldsMetadata(Supplier existing, SupplierDto.UpdateRequest request) {
        StringBuilder sb = new StringBuilder("Changed fields: ");
        if (!existing.getName().equals(request.getName()))         sb.append("name, ");
        if (!existing.getEmail().equals(request.getEmail()))       sb.append("email, ");

        String existingPhone = existing.getPhone() == null ? "" : existing.getPhone();
        String newPhone      = request.getPhone()   == null ? "" : request.getPhone();
        if (!existingPhone.equals(newPhone))                       sb.append("phone, ");

        if (request.getStatus() != null && existing.getStatus() != request.getStatus()) sb.append("status, ");

        String result = sb.toString().replaceAll(", $", "");
        return result.equals("Changed fields:") ? "No changes detected" : result;
    }
}