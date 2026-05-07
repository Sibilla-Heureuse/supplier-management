package com.stockmanager.supplier.service;

import com.stockmanager.supplier.dto.SupplierDto;
import com.stockmanager.supplier.entity.Supplier;
import com.stockmanager.supplier.exception.DuplicateSupplierException;
import com.stockmanager.supplier.exception.SupplierNotFoundException;
import com.stockmanager.supplier.kafka.SupplierEvent;
import com.stockmanager.supplier.kafka.SupplierKafkaProducer;
import com.stockmanager.supplier.repository.SupplierRepository;
import com.stockmanager.supplier.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Unit Tests")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierKafkaProducer kafkaProducer;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier testSupplier;
    private SupplierDto.CreateRequest createRequest;
    private SupplierDto.UpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-001")
                .name("Acme Corporation")
                .email("contact@acme.com")
                .phone("+1-800-555-0199")
                .city("New York")
                .country("United States")
                .status(Supplier.SupplierStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = SupplierDto.CreateRequest.builder()
                .supplierCode("SUP-001")
                .name("Acme Corporation")
                .email("contact@acme.com")
                .phone("+1-800-555-0199")
                .city("New York")
                .country("United States")
                .build();

        updateRequest = SupplierDto.UpdateRequest.builder()
                .name("Acme Corporation Updated")
                .email("updated@acme.com")
                .country("Canada")
                .build();
    }

    // addSupplier tests


    @Test
    @DisplayName("Should create supplier successfully")
    void addSupplier_success() {
        when(supplierRepository.existsBySupplierCode("SUP-001")).thenReturn(false);
        when(supplierRepository.existsByEmail("contact@acme.com")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        SupplierDto.Response result = supplierService.addSupplier(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getSupplierCode()).isEqualTo("SUP-001");
        assertThat(result.getName()).isEqualTo("Acme Corporation");

        verify(supplierRepository).save(any(Supplier.class));
        verify(kafkaProducer).publishSupplierEvent(argThat(
                event -> event.getEventType() == SupplierEvent.EventType.SUPPLIER_CREATED));
    }

    @Test
    @DisplayName("Should throw DuplicateSupplierException when supplier code exists")
    void addSupplier_duplicateCode_throwsException() {
        when(supplierRepository.existsBySupplierCode("SUP-001")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.addSupplier(createRequest))
                .isInstanceOf(DuplicateSupplierException.class)
                .hasMessageContaining("SUP-001");

        verify(supplierRepository, never()).save(any());
        verify(kafkaProducer, never()).publishSupplierEvent(any());
    }

    @Test
    @DisplayName("Should throw DuplicateSupplierException when email exists")
    void addSupplier_duplicateEmail_throwsException() {
        when(supplierRepository.existsBySupplierCode("SUP-001")).thenReturn(false);
        when(supplierRepository.existsByEmail("contact@acme.com")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.addSupplier(createRequest))
                .isInstanceOf(DuplicateSupplierException.class)
                .hasMessageContaining("contact@acme.com");

        verify(supplierRepository, never()).save(any());
    }


    // updateSupplier tests


    @Test
    @DisplayName("Should update supplier successfully")
    void updateSupplier_success() {
        when(supplierRepository.findActiveById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.existsByEmailAndIdNot("updated@acme.com", 1L)).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        SupplierDto.Response result = supplierService.updateSupplier(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(supplierRepository).save(any(Supplier.class));
        verify(kafkaProducer).publishSupplierEvent(argThat(
                event -> event.getEventType() == SupplierEvent.EventType.SUPPLIER_UPDATED));
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException when updating non-existent supplier")
    void updateSupplier_notFound_throwsException() {
        when(supplierRepository.findActiveById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.updateSupplier(99L, updateRequest))
                .isInstanceOf(SupplierNotFoundException.class)
                .hasMessageContaining("99");
    }


    // getSupplierById tests


    @Test
    @DisplayName("Should retrieve supplier by ID successfully")
    void getSupplierById_success() {
        when(supplierRepository.findActiveById(1L)).thenReturn(Optional.of(testSupplier));

        SupplierDto.Response result = supplierService.getSupplierById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("contact@acme.com");

        verify(kafkaProducer).publishSupplierEvent(argThat(
                event -> event.getEventType() == SupplierEvent.EventType.SUPPLIER_RETRIEVED));
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException for non-existent ID")
    void getSupplierById_notFound_throwsException() {
        when(supplierRepository.findActiveById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(999L))
                .isInstanceOf(SupplierNotFoundException.class)
                .hasMessageContaining("999");
    }


    // deleteSupplier tests


    @Test
    @DisplayName("Should soft-delete supplier successfully")
    void deleteSupplier_success() {
        when(supplierRepository.findActiveById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).save(argThat(s ->
                s.getStatus() == Supplier.SupplierStatus.DELETED &&
                        s.getDeletedAt() != null));

        verify(kafkaProducer).publishSupplierEvent(argThat(
                event -> event.getEventType() == SupplierEvent.EventType.SUPPLIER_DELETED));
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException when deleting non-existent supplier")
    void deleteSupplier_notFound_throwsException() {
        when(supplierRepository.findActiveById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.deleteSupplier(99L))
                .isInstanceOf(SupplierNotFoundException.class);

        verify(supplierRepository, never()).save(any());
    }
}