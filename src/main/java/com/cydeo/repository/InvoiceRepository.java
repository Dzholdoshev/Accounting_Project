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

    Long countAllByInvoiceTypeAndCompanyId(InvoiceType invoiceType, Long companyId);
    Invoice findInvoiceById(Long id);
    List<Invoice> findInvoicesByCompanyAndInvoiceTypeAndIsDeleted(Company company, InvoiceType invoiceType, boolean isDeleted);
    List<Invoice> findInvoicesByCompanyAndInvoiceStatusAndIsDeleted(Company company, InvoiceStatus invoiceStatus, boolean isDeleted);
    List<Invoice> findInvoicesByCompanyAndInvoiceStatusAndIsDeletedOrderByDateDesc(Company company, InvoiceStatus invoiceStatus, boolean isDeleted);
    Integer countAllByCompanyAndClientVendor_Id(Company company, Long clientVendorId);

}
