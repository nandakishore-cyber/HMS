package com.example.hms.billingPaymentManagement.service;

import com.example.hms.billingPaymentManagement.dto.bill.*;
import com.example.hms.billingPaymentManagement.entity.*;
import com.example.hms.billingPaymentManagement.exception.*;
import com.example.hms.billingPaymentManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BillService {
    private static final int SCALE = 2;
    private final BillRepository billRepository;
    private final BillItemRepository itemRepository;

    @Transactional
    public BillResponse create(BillCreateRequest request) {
        String number = "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Bill bill = new Bill();
        bill.setBillNumber(number);
        bill.setPatientId(request.patientId());
        bill.setBillDate(request.billDate());
        bill.setDueDate(request.dueDate());
        bill.setTaxRate(money(request.taxRate()));
        recalculate(bill);
        return response(billRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public BillResponse get(UUID id) { return response(find(id)); }

    @Transactional(readOnly = true)
    public Page<BillResponse> search(UUID patientId, PaymentStatus status, LocalDate fromDate,
                                     LocalDate toDate, Pageable pageable) {
        Page<Bill> page;
        if (patientId != null && status != null) page = billRepository.findByPatientIdAndStatus(patientId, status, pageable);
        else if (patientId != null) page = billRepository.findByPatientId(patientId, pageable);
        else if (status != null) page = billRepository.findByStatus(status, pageable);
        else if (fromDate != null && toDate != null) page = billRepository.findByBillDateBetween(fromDate, toDate, pageable);
        else page = billRepository.findAll(pageable);
        return page.map(this::response);
    }

    @Transactional(readOnly = true)
    public Page<BillResponse> pending(Pageable pageable) {
        return billRepository.findByStatusIn(List.of(PaymentStatus.PENDING, PaymentStatus.PARTIALLY_PAID), pageable)
                .map(this::response);
    }

    @Transactional
    public BillResponse update(UUID id, BillUpdateRequest request) {
        Bill bill = find(id);
        ensureEditable(bill);
        bill.setDueDate(request.dueDate());
        bill.setTaxRate(money(request.taxRate()));
        recalculate(bill);
        return response(billRepository.save(bill));
    }

    @Transactional
    public BillResponse setStatus(UUID id, PaymentStatus status) {
        Bill bill = find(id);
        if (status == PaymentStatus.CANCELLED && bill.getPaidAmount().signum() > 0)
            throw new ConflictException("A bill with payments cannot be cancelled");
        if (status == PaymentStatus.PAID && bill.getBalanceAmount().signum() != 0)
            throw new ConflictException("Only fully paid bills can have PAID status");
        bill.setStatus(status);
        return response(billRepository.save(bill));
    }

    @Transactional
    public BillItemResponse addItem(UUID billId, BillItemRequest request) {
        Bill bill = find(billId);
        ensureEditable(bill);
        BillItem item = new BillItem();
        item.setBill(bill);
        apply(item, request);
        BillItem saved = itemRepository.save(item);
        recalculate(bill);
        billRepository.save(bill);
        return BillItemResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<BillItemResponse> items(UUID billId) {
        find(billId);
        return itemRepository.findByBillIdOrderByCreatedAtAsc(billId).stream().map(BillItemResponse::from).toList();
    }

    @Transactional
    public BillItemResponse updateItem(UUID billId, UUID itemId, BillItemRequest request) {
        Bill bill = find(billId);
        ensureEditable(bill);
        BillItem item = itemRepository.findByIdAndBillId(itemId, billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill item not found: " + itemId));
        apply(item, request);
        BigDecimal newTotal = itemRepository.findByBillIdOrderByCreatedAtAsc(billId).stream()
                .map(existing -> existing.getId().equals(itemId) ? item.getAmount() : existing.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal projectedTotal = money(newTotal.add(newTotal.multiply(bill.getTaxRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        if (bill.getPaidAmount().compareTo(projectedTotal) > 0)
            throw new ConflictException("Bill total cannot be lower than payments already recorded");
        recalculate(bill);
        billRepository.save(bill);
        return BillItemResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(UUID billId, UUID itemId) {
        Bill bill = find(billId);
        ensureEditable(bill);
        BillItem item = itemRepository.findByIdAndBillId(itemId, billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill item not found: " + itemId));
        itemRepository.delete(item);
        itemRepository.flush();
        recalculate(bill);
        if (bill.getPaidAmount().compareTo(bill.getTotalAmount()) > 0)
            throw new ConflictException("Bill total cannot be lower than payments already recorded");
        billRepository.save(bill);
    }

    public Bill find(UUID id) {
        return billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + id));
    }

    private BillResponse response(Bill bill) {
        return BillResponse.from(bill, itemRepository.findByBillIdOrderByCreatedAtAsc(bill.getId()));
    }
    private void ensureEditable(Bill bill) {
        if (bill.getStatus() == PaymentStatus.CANCELLED || bill.getStatus() == PaymentStatus.PAID)
            throw new ConflictException("This bill cannot be edited");
    }
    private void apply(BillItem item, BillItemRequest request) {
        item.setDescription(request.description().trim());
        item.setCategory(request.category());
        item.setQuantity(request.quantity());
        item.setUnitPrice(money(request.unitPrice()));
        item.setAmount(money(request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()))));
    }
    private void recalculate(Bill bill) {
        BigDecimal subtotal = bill.getId() == null ? BigDecimal.ZERO : itemRepository.findByBillIdOrderByCreatedAtAsc(bill.getId())
                .stream().map(BillItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        bill.setSubtotal(money(subtotal));
        bill.setTaxAmount(money(subtotal.multiply(bill.getTaxRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        bill.setTotalAmount(money(subtotal.add(bill.getTaxAmount())));
        bill.setBalanceAmount(money(bill.getTotalAmount().subtract(bill.getPaidAmount())));
        if (bill.getStatus() != PaymentStatus.CANCELLED)
            bill.setStatus(bill.getPaidAmount().signum() == 0 ? PaymentStatus.PENDING :
                    bill.getBalanceAmount().signum() == 0 ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
    }
    private BigDecimal money(BigDecimal value) { return value.setScale(SCALE, RoundingMode.HALF_UP); }
}
