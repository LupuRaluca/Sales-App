package com.sia.salesapp.infrastructure.repository;
import com.sia.salesapp.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> { }

