package com.cydeo.repository;

import com.cydeo.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {


    List<InvoiceProduct> findInvoiceProductByInvoice_IdAndIsDeleted(Long id, Boolean deleted);

    List<InvoiceProduct> findAllByIsDeleted(Boolean deleted);

}
