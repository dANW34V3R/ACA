package com.company;

public class LSQEntry {

    static int staticSeqInc = 0;

    int ROBdestination;
    // true = load, false = store
    boolean LSBool;
    Integer address;
    int sequenceNumber;
    Integer value;
    boolean complete = false;
    Integer tag1;
    Integer tag2;
    Integer strValTag = null;
    Integer val1;
    Integer val2;
    Integer strValVal = null;

    public LSQEntry(int ROBdestinationVal, boolean LSBoolVal, Integer addressVal, Integer valueVal, Integer tag1Val, Integer val1Val, Integer tag2Val, Integer val2Val) {
        ROBdestination = ROBdestinationVal;
        LSBool = LSBoolVal;
        address = addressVal;
        sequenceNumber = staticSeqInc;
        value = valueVal;
        tag1 = tag1Val;
        val1 = val1Val;
        tag2 = tag2Val;
        val2 = val2Val;
        staticSeqInc++;
    }

    public LSQEntry(int ROBdestinationVal, boolean LSBoolVal, Integer addressVal, Integer valueVal, Integer tag1Val, Integer val1Val, Integer tag2Val, Integer val2Val, Integer strValTagVal, Integer strValValVal) {
        ROBdestination = ROBdestinationVal;
        LSBool = LSBoolVal;
        address = addressVal;
        sequenceNumber = staticSeqInc;
        value = valueVal;
        tag1 = tag1Val;
        val1 = val1Val;
        tag2 = tag2Val;
        val2 = val2Val;
        strValTag = strValTagVal;
        strValVal = strValValVal;
        staticSeqInc++;
    }

    @Override
    public String toString() {
        return "LSQEntry{" +
                "ROBdestination=" + ROBdestination +
                ", LSBool=" + LSBool +
                ", address=" + address +
                ", value=" + value +
                ", complete=" + complete +
                ", tag1=" + tag1 +
                ", tag2=" + tag2 +
                ", val1=" + val1 +
                ", val2=" + val2 +
                '}';
    }
}
