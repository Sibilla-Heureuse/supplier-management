package com.stockmanager.supplier.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateSupplierException extends RuntimeException {
    public DuplicateSupplierException(String field, String value) {
        super("Supplier already exists with " + field + ": " + value);
    }
}
