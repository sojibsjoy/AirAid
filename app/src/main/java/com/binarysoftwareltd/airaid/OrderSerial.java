package com.binarysoftwareltd.airaid;

public class OrderSerial {

    private int currentOrderSerial;

    public OrderSerial(int currentOrderSerial) {
        this.currentOrderSerial = currentOrderSerial;
    }

    public int getCurrentOrderSerial() {
        return currentOrderSerial;
    }

    public void setCurrentOrderSerial(int currentOrderSerial) {
        this.currentOrderSerial = currentOrderSerial;
    }
}
