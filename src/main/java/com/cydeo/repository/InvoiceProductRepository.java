package com.cydeo.repository;

import com.cydeo.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {


    @Query(value="SELECT * from InvoiceProduct i where i.invoice_id =?1", nativeQuery=true)
    List<InvoiceProduct> findInvoiceProductByInvoiceid(@Param("invoiceId") Long invoiceId);

    List<InvoiceProduct> findInvoiceProductByInvoice_Id(Long id);

}
