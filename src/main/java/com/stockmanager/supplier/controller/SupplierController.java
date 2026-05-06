package com.stockmanager.supplier.controller;

import com.stockmanager.supplier.dto.SupplierDto;
import com.stockmanager.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for the Supplier Management module.
 *
 * <p>All endpoints follow REST conventions and are secured with HTTP Basic Auth.
 * Every operation publishes a Kafka event for audit/notification purposes.
 *
 * <p>Base URL: /api/v1/suppliers
 */
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supplier Management", description = "CRUD operations for managing suppliers in the Stock Management system")
@SecurityRequirement(name = "basicAuth")
public class SupplierController {

    private final SupplierService supplierService;


    // POST /api/v1/suppliers/add


    @PostMapping("/add")
    @Operation(
            summary = "Add a new supplier",
            description = "Creates a new supplier record in the database. " +
                    "Publishes a SUPPLIER_CREATED Kafka event after successful creation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created successfully",
                    content = @Content(schema = @Schema(implementation = SupplierDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Supplier with same code or email already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SupplierDto.ApiResponse<SupplierDto.Response>> addSupplier(
            @Valid @RequestBody SupplierDto.CreateRequest request) {

        log.info("POST /api/v1/suppliers/add | SupplierCode: {}", request.getSupplierCode());
        SupplierDto.Response created = supplierService.addSupplier(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SupplierDto.ApiResponse.success("Supplier created successfully", created));
    }


    // PUT /api/v1/suppliers/update/{id}


    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update an existing supplier",
            description = "Updates the information of an existing supplier identified by ID. " +
                    "Publishes a SUPPLIER_UPDATED Kafka event after successful update."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully",
                    content = @Content(schema = @Schema(implementation = SupplierDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use by another supplier"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SupplierDto.ApiResponse<SupplierDto.Response>> updateSupplier(
            @Parameter(description = "Supplier unique ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SupplierDto.UpdateRequest request) {

        log.info("PUT /api/v1/suppliers/update/{}", id);
        SupplierDto.Response updated = supplierService.updateSupplier(id, request);

        return ResponseEntity.ok(
                SupplierDto.ApiResponse.success("Supplier updated successfully", updated));
    }


    // GET /api/v1/suppliers/{id}


    @GetMapping("/{id}")
    @Operation(
            summary = "Get a supplier by ID",
            description = "Retrieves the details of a supplier by their unique ID. " +
                    "Publishes a SUPPLIER_RETRIEVED Kafka event for audit purposes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found",
                    content = @Content(schema = @Schema(implementation = SupplierDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SupplierDto.ApiResponse<SupplierDto.Response>> getSupplierById(
            @Parameter(description = "Supplier unique ID", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/suppliers/{}", id);
        SupplierDto.Response supplier = supplierService.getSupplierById(id);

        return ResponseEntity.ok(
                SupplierDto.ApiResponse.success("Supplier retrieved successfully", supplier));
    }


    // DELETE /api/v1/suppliers/delete/{id}


    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete a supplier",
            description = "Performs a soft-delete on the supplier (sets status to DELETED). " +
                    "The record is retained for audit purposes. " +
                    "Publishes a SUPPLIER_DELETED Kafka event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SupplierDto.ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "Supplier unique ID", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/suppliers/delete/{}", id);
        supplierService.deleteSupplier(id);

        return ResponseEntity.ok(
                SupplierDto.ApiResponse.success("Supplier deleted successfully", null));
    }
}