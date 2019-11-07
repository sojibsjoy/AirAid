package com.binarysoftwareltd.airaid;

public class OrderSerial {

    private String currentOrderSerial;

    public OrderSerial(String currentOrderSerial) {
        this.currentOrderSerial = currentOrderSerial;
    }

    public String getCurrentOrderSerial() {
        return currentOrderSerial;
    }

    public void setCurrentOrderSerial(String currentOrderSerial) {
        this.currentOrderSerial = currentOrderSerial;
    }
}
