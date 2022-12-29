package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByInvoiceTypeAndIsDeleted(InvoiceType invoiceType, Boolean deleted);
    Invoice findByIdAndIsDeleted(Long id, Boolean deleted);

    @Query("SELECT coalesce(max(ch.id), 0) FROM Invoice ch where ch.invoiceType =?1")
    Long getMaxId(InvoiceType invoiceType);


}
