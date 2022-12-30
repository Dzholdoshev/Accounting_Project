package com.cydeo.enums;

public enum ClientVendorType {

    CLIENT("Client"), VENDOR("Vendor");

    public final String value;

    ClientVendorType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public Integer size(){
       return ClientVendorType.values().length;
    }

}