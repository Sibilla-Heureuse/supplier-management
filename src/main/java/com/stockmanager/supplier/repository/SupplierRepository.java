package com.stockmanager.supplier.repository;

import com.stockmanager.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.status != 'DELETED'")
    Optional<Supplier> findActiveById(@Param("id") Long id);

    boolean existsBySupplierCode(String supplierCode);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.email = :email AND s.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    boolean existsByEmail(String email);

    Optional<Supplier> findBySupplierCode(String supplierCode);
}
