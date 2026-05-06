package com.stockmanager.supplier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockmanager.supplier.entity.Supplier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Objects for the Supplier module.
 * Contains nested classes for Create Request, Update Request, and API Response.
 */
public class SupplierDto {

    // =========================================================
    // REQUEST: Create a new supplier
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "SupplierCreateRequest", description = "Payload for creating a new supplier")
    public static class CreateRequest {

        @NotBlank(message = "Supplier code is required")
        @Size(max = 50, message = "Supplier code must not exceed 50 characters")
        @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Supplier code must be uppercase alphanumeric with hyphens only")
        @Schema(description = "Unique supplier code", example = "SUP-001")
        private String supplierCode;

        @NotBlank(message = "Supplier name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        @Schema(description = "Full business name of the supplier", example = "Acme Corporation Ltd")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Business email address", example = "contact@acme.com")
        private String email;

        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Invalid phone number format")
        @Schema(description = "Contact phone number", example = "+1-800-555-0199")
        private String phone;

        @Size(max = 500, message = "Address must not exceed 500 characters")
        @Schema(description = "Street address", example = "123 Business Park, Suite 400")
        private String address;

        @Size(max = 100)
        @Schema(description = "City", example = "New York")
        private String city;

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        @Schema(description = "Country", example = "United States")
        private String country;

        @Size(max = 100)
        @Schema(description = "Primary contact person name", example = "John Smith")
        private String contactPerson;

        @Size(max = 50)
        @Schema(description = "Tax identification number", example = "US-TAX-123456")
        private String taxNumber;

        @Size(max = 1000)
        @Schema(description = "Additional notes about the supplier")
        private String notes;
    }

    // =========================================================
    // REQUEST: Update an existing supplier
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "SupplierUpdateRequest", description = "Payload for updating an existing supplier")
    public static class UpdateRequest {

        @NotBlank(message = "Supplier name is required")
        @Size(max = 150)
        @Schema(description = "Full business name of the supplier", example = "Acme Corporation Ltd")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Business email address", example = "updated@acme.com")
        private String email;

        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Invalid phone number format")
        @Schema(description = "Contact phone number", example = "+1-800-555-0199")
        private String phone;

        @Size(max = 500)
        @Schema(description = "Street address", example = "456 New Office Blvd")
        private String address;

        @Size(max = 100)
        @Schema(description = "City", example = "Los Angeles")
        private String city;

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        @Schema(description = "Country", example = "United States")
        private String country;

        @Size(max = 100)
        @Schema(description = "Primary contact person name", example = "Jane Doe")
        private String contactPerson;

        @Size(max = 50)
        @Schema(description = "Tax identification number")
        private String taxNumber;

        @Schema(description = "Supplier status")
        private Supplier.SupplierStatus status;

        @Size(max = 1000)
        @Schema(description = "Additional notes about the supplier")
        private String notes;
    }

    // =========================================================
    // RESPONSE: Supplier API response payload
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "SupplierResponse", description = "Supplier data returned from the API")
    public static class Response {

        @Schema(description = "Supplier unique ID", example = "1")
        private Long id;

        @Schema(description = "Unique supplier code", example = "SUP-001")
        private String supplierCode;

        @Schema(description = "Full business name", example = "Acme Corporation Ltd")
        private String name;

        @Schema(description = "Business email address", example = "contact@acme.com")
        private String email;

        @Schema(description = "Contact phone number", example = "+1-800-555-0199")
        private String phone;

        @Schema(description = "Street address")
        private String address;

        @Schema(description = "City")
        private String city;

        @Schema(description = "Country")
        private String country;

        @Schema(description = "Primary contact person")
        private String contactPerson;

        @Schema(description = "Tax identification number")
        private String taxNumber;

        @Schema(description = "Supplier status")
        private Supplier.SupplierStatus status;

        @Schema(description = "Additional notes")
        private String notes;

        @Schema(description = "Record creation timestamp")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        /**
         * Static factory method to map a Supplier entity to a Response DTO.
         */
        public static Response fromEntity(Supplier supplier) {
            return Response.builder()
                    .id(supplier.getId())
                    .supplierCode(supplier.getSupplierCode())
                    .name(supplier.getName())
                    .email(supplier.getEmail())
                    .phone(supplier.getPhone())
                    .address(supplier.getAddress())
                    .city(supplier.getCity())
                    .country(supplier.getCountry())
                    .contactPerson(supplier.getContactPerson())
                    .taxNumber(supplier.getTaxNumber())
                    .status(supplier.getStatus())
                    .notes(supplier.getNotes())
                    .createdAt(supplier.getCreatedAt())
                    .updatedAt(supplier.getUpdatedAt())
                    .build();
        }
    }

    // =========================================================
    // GENERIC API WRAPPER
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "ApiResponse", description = "Standard API response wrapper")
    public static class ApiResponse<T> {

        @Schema(description = "Whether the request succeeded")
        private boolean success;

        @Schema(description = "Human-readable message")
        private String message;

        @Schema(description = "Response payload")
        private T data;

        @Schema(description = "Error details (only present on failure)")
        private String error;

        public static <T> ApiResponse<T> success(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .build();
        }

        public static <T> ApiResponse<T> error(String message, String error) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .error(error)
                    .build();
        }
    }
}