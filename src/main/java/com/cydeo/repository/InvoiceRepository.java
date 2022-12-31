package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
//    List<Invoice> findAllByInvoiceTypeAndIsDeleted(InvoiceType invoiceType, Boolean deleted);
//    Invoice findByIdAndIsDeleted(Long id, Boolean deleted);

    @Query("SELECT coalesce(max(ch.id), 1) FROM Invoice ch where ch.invoiceType =?1")
    Long getMaxId(InvoiceType invoiceType);


    Invoice findInvoiceById(Long id);
    List<Invoice> findInvoicesByCompanyAndInvoiceType(Company company, InvoiceType invoiceType);
    List<Invoice> findInvoicesByCompanyAndInvoiceStatus(Company company, InvoiceStatus invoiceStatus);
    List<Invoice> findInvoicesByCompanyAndInvoiceStatusOrderByDateDesc(Company company, InvoiceStatus invoiceStatus);
    Integer countAllByCompanyAndClientVendor_Id(Company company, Long clientVendorId);

}
