package com.stockmanager.supplier.service;

import com.stockmanager.supplier.dto.SupplierDto;

public interface SupplierService {
    SupplierDto.Response addSupplier(SupplierDto.CreateRequest request);
    SupplierDto.Response updateSupplier(Long id, SupplierDto.UpdateRequest request);
    SupplierDto.Response getSupplierById(Long id);
    void deleteSupplier(Long id);
}
