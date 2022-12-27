package com.cydeo.enums;

public enum InvoiceStatus {
    AWAITING_APPROVAL ("Awaiting Approval"),
    APPROVED("Approved");

    private final String value;

    InvoiceStatus(String value) {
        this.value = value;
    }
}
