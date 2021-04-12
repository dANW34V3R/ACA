package com.company;

public class ROBEntry {
    //type
    int destinationRegister;
    int value;
    boolean ready;
    boolean WB = true;
    boolean halt = false;

    public ROBEntry(int destReg, int val, boolean readyVal) {
        destinationRegister = destReg;
        value = val;
        ready = readyVal;
    }

    public ROBEntry(int destReg, int val, boolean readyVal, boolean haltVal) {
        destinationRegister = destReg;
        value = val;
        ready = readyVal;
        halt = haltVal;
        WB = !haltVal;
    }

    @Override
    public String toString() {
        return "ROBEntry{" +
                "destinationRegister=" + destinationRegister +
                ", value=" + value +
                ", ready=" + ready +
                '}';
    }
}
