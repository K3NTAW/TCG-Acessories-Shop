package com.tcgshop.manufacturing.repository;

import com.tcgshop.manufacturing.model.PrintJob;
import com.tcgshop.manufacturing.model.PrintJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrintJobRepository extends JpaRepository<PrintJob, Long> {
    Optional<PrintJob> findByOrderId(Long orderId);
    List<PrintJob> findByStatus(PrintJobStatus status);
    List<PrintJob> findByOrderNumber(String orderNumber);
}

