package com.example.hms.billingPaymentManagement.exception;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String error, String message) {}
