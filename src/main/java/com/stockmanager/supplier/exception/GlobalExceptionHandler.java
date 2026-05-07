package com.stockmanager.supplier.exception;

import com.stockmanager.supplier.dto.SupplierDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<SupplierDto.ApiResponse<Void>> handleNotFound(SupplierNotFoundException ex) {
        log.warn("Supplier not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(SupplierDto.ApiResponse.error("Supplier not found", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateSupplierException.class)
    public ResponseEntity<SupplierDto.ApiResponse<Void>> handleDuplicate(DuplicateSupplierException ex) {
        log.warn("Duplicate supplier: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(SupplierDto.ApiResponse.error("Duplicate supplier", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SupplierDto.ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(SupplierDto.ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SupplierDto.ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SupplierDto.ApiResponse.error("Internal server error", ex.getMessage()));
    }
}
