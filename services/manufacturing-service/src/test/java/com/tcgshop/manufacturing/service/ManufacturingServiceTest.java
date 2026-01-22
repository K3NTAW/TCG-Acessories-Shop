package com.tcgshop.manufacturing.service;

import com.tcgshop.manufacturing.model.PrintJob;
import com.tcgshop.manufacturing.model.PrintJobStatus;
import com.tcgshop.manufacturing.repository.PrintJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturingServiceTest {

    @Mock
    private PrintJobRepository printJobRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
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
    void testCreatePrintJob() {
        // Given
        when(printJobRepository.save(any(PrintJob.class))).thenAnswer(invocation -> {
            PrintJob job = invocation.getArgument(0);
            job.setId(1L);
            return job;
        });

        // When
        PrintJob result = manufacturingService.createPrintJob(1L, "ORD-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("ORD-123");
        assertThat(result.getStatus()).isEqualTo(PrintJobStatus.QUEUED);
        
        verify(printJobRepository).save(any(PrintJob.class));
        verify(kafkaTemplate).send(eq("manufacturing-job-created"), any());
    }

    @Test
    void testCreatePrintJob_PublishesKafkaEvent() {
        // Given
        when(printJobRepository.save(any(PrintJob.class))).thenAnswer(invocation -> {
            PrintJob job = invocation.getArgument(0);
            job.setId(1L);
            return job;
        });
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        manufacturingService.createPrintJob(1L, "ORD-123");

        // Then
        verify(kafkaTemplate).send(eq("manufacturing-job-created"), eventCaptor.capture());
        Map<String, Object> event = eventCaptor.getValue();
        assertThat(event).containsKey("printJobId");
        assertThat(event).containsKey("orderId");
        assertThat(event).containsKey("orderNumber");
        assertThat(event).containsKey("status");
    }

    @Test
    void testGetPrintJob() {
        // Given
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(testPrintJob));

        // When
        PrintJob result = manufacturingService.getPrintJob(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(printJobRepository).findById(1L);
    }

    @Test
    void testGetPrintJob_NotFound() {
        // Given
        when(printJobRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> manufacturingService.getPrintJob(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Print job not found");
        verify(printJobRepository).findById(999L);
    }

    @Test
    void testGetAllPrintJobs() {
        // Given
        List<PrintJob> jobs = Arrays.asList(testPrintJob);
        when(printJobRepository.findAll()).thenReturn(jobs);

        // When
        List<PrintJob> result = manufacturingService.getAllPrintJobs();

        // Then
        assertThat(result).hasSize(1);
        verify(printJobRepository).findAll();
    }

    @Test
    void testGetPrintJobByOrderId() {
        // Given
        when(printJobRepository.findByOrderId(1L)).thenReturn(Optional.of(testPrintJob));

        // When
        PrintJob result = manufacturingService.getPrintJobByOrderId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        verify(printJobRepository).findByOrderId(1L);
    }

    @Test
    void testGetPrintJobByOrderId_NotFound() {
        // Given
        when(printJobRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> manufacturingService.getPrintJobByOrderId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Print job not found for order");
        verify(printJobRepository).findByOrderId(999L);
    }

    @Test
    void testGetPrintQueue() {
        // Given
        List<PrintJob> queue = Arrays.asList(testPrintJob);
        when(printJobRepository.findByStatus(PrintJobStatus.QUEUED)).thenReturn(queue);

        // When
        List<PrintJob> result = manufacturingService.getPrintQueue();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PrintJobStatus.QUEUED);
        verify(printJobRepository).findByStatus(PrintJobStatus.QUEUED);
    }

    @Test
    void testUpdatePrintJobStatus() {
        // Given
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(testPrintJob));
        when(printJobRepository.save(any(PrintJob.class))).thenReturn(testPrintJob);

        // When
        PrintJob result = manufacturingService.updatePrintJobStatus(1L, PrintJobStatus.PRINTING);

        // Then
        assertThat(result).isNotNull();
        verify(printJobRepository).findById(1L);
        verify(printJobRepository).save(any(PrintJob.class));
    }
}

