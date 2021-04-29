package com.company;

public class LSQEntry {

    static int staticSeqInc = 0;

    int ROBdestination;
    // true = load, false = store
    boolean LSBool;
    Integer address;
    // allows loads and stores to be kept track of within LSQ
    int sequenceNumber;
    Integer value;
    boolean complete = false;
    boolean waiting = false;
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
                ", sequenceNumber=" + sequenceNumber +
                ", value=" + value +
                ", complete=" + complete +
                ", waiting=" + waiting +
                ", tag1=" + tag1 +
                ", tag2=" + tag2 +
                ", strValTag=" + strValTag +
                ", val1=" + val1 +
                ", val2=" + val2 +
                ", strValVal=" + strValVal +
                '}';
    }

    // returns this LSQ entry with tags set to -1 for most depended on RSEntry evaluation
    public LSQEntry getNonNullEntry(){
        Integer nonNullTag1 = tag1;
        Integer nonNullTag2 = tag2;
        Integer nonNullValTag = strValTag;
        if (tag1 == null) {
            nonNullTag1 = -1;
        }
        if (tag2 == null) {
            nonNullTag2 = -1;
        }
        if (strValTag == null) {
            nonNullValTag = -1;
        }
        return new LSQEntry(ROBdestination, LSBool, address, value, nonNullTag1, val1, nonNullTag2, val2, nonNullValTag, strValVal);
    }
}
