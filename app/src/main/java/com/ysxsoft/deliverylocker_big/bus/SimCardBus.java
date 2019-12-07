package com.ysxsoft.deliverylocker_big.bus;

public class SimCardBus {

    private boolean isInPut;

    private String text = "sim卡插拔广播";

    public SimCardBus(boolean isInPut) {
        this.isInPut = isInPut;
    }

    public boolean isInPut() {
        return isInPut;
    }

    public void setInPut(boolean inPut) {
        isInPut = inPut;
    }
}
