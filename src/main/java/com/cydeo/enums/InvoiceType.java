package com.cydeo.enums;

public enum InvoiceType {
    PURCHASE("purchase"),
    SALES("Sales");

   private final String value;

    InvoiceType(String value) {
        this.value = value;
    }
}
