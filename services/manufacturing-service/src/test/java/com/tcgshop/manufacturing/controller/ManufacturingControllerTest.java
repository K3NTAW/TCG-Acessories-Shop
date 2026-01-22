package com.tcgshop.manufacturing.controller;

import com.tcgshop.manufacturing.model.PrintJob;
import com.tcgshop.manufacturing.model.PrintJobStatus;
import com.tcgshop.manufacturing.service.ManufacturingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManufacturingController.class)
class ManufacturingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManufacturingService manufacturingService;

    private PrintJob testPrintJob;

    @BeforeEach
    void setUp() {
        testPrintJob = new PrintJob();
        testPrintJob.setId(1L);
        testPrintJob.setOrderId(1L);
        testPrintJob.setOrderNumber("ORD-123");
        testPrintJob.setStatus(PrintJobStatus.QUEUED);
        testPrintJob.setEstimatedCompletion(LocalDateTime.now().plusHours(24));
        testPrintJob.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllPrintJobs() throws Exception {
        // Given
        List<PrintJob> jobs = Arrays.asList(testPrintJob);
        when(manufacturingService.getAllPrintJobs()).thenReturn(jobs);

        // When & Then
        mockMvc.perform(get("/manufacturing/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(manufacturingService).getAllPrintJobs();
    }

    @Test
    void testGetPrintJob() throws Exception {
        // Given
        when(manufacturingService.getPrintJob(1L)).thenReturn(testPrintJob);

        // When & Then
        mockMvc.perform(get("/manufacturing/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1));

        verify(manufacturingService).getPrintJob(1L);
    }

    @Test
    void testGetPrintJobByOrderId() throws Exception {
        // Given
        when(manufacturingService.getPrintJobByOrderId(1L)).thenReturn(testPrintJob);

        // When & Then
        mockMvc.perform(get("/manufacturing/jobs/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1));

        verify(manufacturingService).getPrintJobByOrderId(1L);
    }

    @Test
    void testGetPrintQueue() throws Exception {
        // Given
        List<PrintJob> queue = Arrays.asList(testPrintJob);
        when(manufacturingService.getPrintQueue()).thenReturn(queue);

        // When & Then
        mockMvc.perform(get("/manufacturing/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("QUEUED"));

        verify(manufacturingService).getPrintQueue();
    }

    @Test
    void testUpdatePrintJobStatus() throws Exception {
        // Given
        testPrintJob.setStatus(PrintJobStatus.PRINTING);
        when(manufacturingService.updatePrintJobStatus(1L, PrintJobStatus.PRINTING)).thenReturn(testPrintJob);

        // When & Then
        mockMvc.perform(put("/manufacturing/jobs/1/status")
                        .param("status", "PRINTING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PRINTING"));

        verify(manufacturingService).updatePrintJobStatus(1L, PrintJobStatus.PRINTING);
    }
}

