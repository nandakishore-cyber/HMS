package com.example.hms.billingPaymentManagement.service;

import com.example.hms.billingPaymentManagement.dto.payment.*;
import com.example.hms.billingPaymentManagement.entity.*;
import com.example.hms.billingPaymentManagement.exception.*;
import com.example.hms.billingPaymentManagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BillService billService;

    @Transactional
    public PaymentResponse record(UUID billId, PaymentCreateRequest request) {
        Bill bill = billService.find(billId);
        if (bill.getStatus() == PaymentStatus.CANCELLED) throw new ConflictException("Cancelled bills cannot receive payments");
        if (paymentRepository.existsByPaymentReference(request.paymentReference().trim()))
            throw new ConflictException("Payment reference already exists");
        BigDecimal amount = request.amount().setScale(2, java.math.RoundingMode.HALF_UP);
        if (amount.compareTo(bill.getBalanceAmount()) > 0) throw new ConflictException("Payment exceeds outstanding balance");
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaymentReference(request.paymentReference().trim());
        payment.setAmount(amount);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setPaymentDate(request.paymentDate());
        payment.setNotes(request.notes());
        Payment saved = paymentRepository.save(payment);
        bill.setPaidAmount(bill.getPaidAmount().add(amount).setScale(2, java.math.RoundingMode.HALF_UP));
        bill.setBalanceAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()).setScale(2, java.math.RoundingMode.HALF_UP));
        bill.setStatus(bill.getBalanceAmount().signum() == 0 ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
        return PaymentResponse.from(saved);
    }

    @Transactional
    public PaymentResponse setStatus(UUID id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        if (status != PaymentStatus.CANCELLED || payment.isCancelled()) {
            throw new ConflictException("Only an active payment can be cancelled");
        }
        Bill bill = payment.getBill();
        payment.setCancelled(true);
        payment.setStatus(PaymentStatus.CANCELLED);
        bill.setPaidAmount(bill.getPaidAmount().subtract(payment.getAmount()).setScale(2, java.math.RoundingMode.HALF_UP));
        bill.setBalanceAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()).setScale(2, java.math.RoundingMode.HALF_UP));
        bill.setStatus(bill.getPaidAmount().signum() == 0 ? PaymentStatus.PENDING : PaymentStatus.PARTIALLY_PAID);
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> history(UUID billId) {
        billService.find(billId);
        return paymentRepository.findByBillIdOrderByPaymentDateDesc(billId).stream().map(PaymentResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public PaymentResponse get(UUID id) {
        return PaymentResponse.from(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id)));
    }
    @Transactional(readOnly = true)
    public Page<PaymentResponse> search(Pageable pageable) {
        return paymentRepository.findAllByOrderByPaymentDateDesc(pageable).map(PaymentResponse::from);
    }
}
