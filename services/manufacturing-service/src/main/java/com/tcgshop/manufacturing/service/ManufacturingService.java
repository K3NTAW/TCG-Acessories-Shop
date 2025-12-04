package com.tcgshop.manufacturing.service;

import com.tcgshop.manufacturing.model.PrintJob;
import com.tcgshop.manufacturing.model.PrintJobStatus;
import com.tcgshop.manufacturing.repository.PrintJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ManufacturingService {

    private final PrintJobRepository printJobRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ManufacturingService(PrintJobRepository printJobRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.printJobRepository = printJobRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public PrintJob createPrintJob(Long orderId, String orderNumber) {
        PrintJob printJob = new PrintJob();
        printJob.setOrderId(orderId);
        printJob.setOrderNumber(orderNumber);
        printJob.setStatus(PrintJobStatus.QUEUED);
        printJob.setEstimatedCompletion(LocalDateTime.now().plusHours(24)); // Mock: 24h estimate

        PrintJob saved = printJobRepository.save(printJob);

        // Publish Kafka event: manufacturing-job-created
        Map<String, Object> event = new HashMap<>();
        event.put("printJobId", saved.getId());
        event.put("orderId", saved.getOrderId());
        event.put("orderNumber", saved.getOrderNumber());
        event.put("status", saved.getStatus().name());
        kafkaTemplate.send("manufacturing-job-created", event);

        return saved;
    }

    public PrintJob getPrintJob(Long id) {
        return printJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print job not found: " + id));
    }

    public PrintJob getPrintJobByOrderId(Long orderId) {
        return printJobRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Print job not found for order: " + orderId));
    }

    public List<PrintJob> getPrintQueue() {
        return printJobRepository.findByStatus(PrintJobStatus.QUEUED);
    }

    public PrintJob updatePrintJobStatus(Long id, PrintJobStatus status) {
        PrintJob printJob = printJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print job not found: " + id));
        printJob.setStatus(status);
        return printJobRepository.save(printJob);
    }

    public List<PrintJob> getAllPrintJobs() {
        return printJobRepository.findAll();
    }
}

