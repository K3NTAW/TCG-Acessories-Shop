package com.tcgshop.manufacturing.controller;

import com.tcgshop.manufacturing.model.PrintJob;
import com.tcgshop.manufacturing.model.PrintJobStatus;
import com.tcgshop.manufacturing.service.ManufacturingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturing")
@CrossOrigin(origins = "*")
public class ManufacturingController {

    private final ManufacturingService manufacturingService;

    @Autowired
    public ManufacturingController(ManufacturingService manufacturingService) {
        this.manufacturingService = manufacturingService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<PrintJob>> getAllPrintJobs() {
        List<PrintJob> jobs = manufacturingService.getAllPrintJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<PrintJob> getPrintJob(@PathVariable Long id) {
        PrintJob job = manufacturingService.getPrintJob(id);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/jobs/order/{orderId}")
    public ResponseEntity<PrintJob> getPrintJobByOrderId(@PathVariable Long orderId) {
        PrintJob job = manufacturingService.getPrintJobByOrderId(orderId);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<PrintJob>> getPrintQueue() {
        List<PrintJob> queue = manufacturingService.getPrintQueue();
        return ResponseEntity.ok(queue);
    }

    @PutMapping("/jobs/{id}/status")
    public ResponseEntity<PrintJob> updatePrintJobStatus(
            @PathVariable Long id,
            @RequestParam PrintJobStatus status) {
        PrintJob job = manufacturingService.updatePrintJobStatus(id, status);
        return ResponseEntity.ok(job);
    }
}

