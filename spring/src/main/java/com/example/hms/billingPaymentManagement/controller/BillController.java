package com.example.hms.billingPaymentManagement.controller;

import com.example.hms.billingPaymentManagement.dto.bill.*;
import com.example.hms.billingPaymentManagement.entity.PaymentStatus;
import com.example.hms.billingPaymentManagement.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BillResponse create(@Valid @RequestBody BillCreateRequest request) { return billService.create(request); }
    @GetMapping
    public Page<BillResponse> search(@RequestParam(required = false) UUID patientId,
                                     @RequestParam(required = false) PaymentStatus status,
                                     @RequestParam(required = false) LocalDate fromDate,
                                     @RequestParam(required = false) LocalDate toDate, Pageable pageable) {
        return billService.search(patientId, status, fromDate, toDate, pageable);
    }
    @GetMapping("/{billId}")
    public BillResponse get(@PathVariable UUID billId) { return billService.get(billId); }
    @PutMapping("/{billId}")
    public BillResponse update(@PathVariable UUID billId, @Valid @RequestBody BillUpdateRequest request) {
        return billService.update(billId, request);
    }
    @PatchMapping("/{billId}/status")
    public BillResponse status(@PathVariable UUID billId, @Valid @RequestBody BillStatusRequest request) {
        return billService.setStatus(billId, request.status());
    }
    @DeleteMapping("/{billId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID billId) { billService.setStatus(billId, PaymentStatus.CANCELLED); }
    @GetMapping("/pending")
    public Page<BillResponse> pending(Pageable pageable) {
        return billService.pending(pageable);
    }
    @PostMapping("/{billId}/items")
    public BillItemResponse addItem(@PathVariable UUID billId, @Valid @RequestBody BillItemRequest request) {
        return billService.addItem(billId, request);
    }
    @GetMapping("/{billId}/items")
    public List<BillItemResponse> items(@PathVariable UUID billId) { return billService.items(billId); }
    @PutMapping("/{billId}/items/{itemId}")
    public BillItemResponse updateItem(@PathVariable UUID billId, @PathVariable UUID itemId,
                                       @Valid @RequestBody BillItemRequest request) {
        return billService.updateItem(billId, itemId, request);
    }
    @DeleteMapping("/{billId}/items/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID billId, @PathVariable UUID itemId) {
        billService.deleteItem(billId, itemId);
    }
}
