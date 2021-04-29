package com.company;

import com.sun.istack.internal.Nullable;

public class RSEntry {

    String opcode;
    int ROBdestination;
    Integer tag1;
    Integer tag2;
    Integer val1;
    Integer val2;

    public RSEntry(String op, int destTag, @Nullable Integer tag1Val, @Nullable Integer tag2Val, @Nullable Integer val1Val, @Nullable Integer val2Val) {
        opcode = op;
        ROBdestination = destTag;
        tag1 = tag1Val;
        tag2 = tag2Val;
        val1 = val1Val;
        val2 = val2Val;
    }

    @Override
    public String toString() {
        return "RSEntry{" +
                "opcode='" + opcode + '\'' +
                ", ROBdestination=" + ROBdestination +
                ", tag1=" + tag1 +
                ", tag2=" + tag2 +
                ", val1=" + val1 +
                ", val2=" + val2 +
                '}';
    }

    // returns this RS entry with tags set to -1 for most depended on RSEntry evaluation
    public RSEntry getNonNullEntry() {
        Integer nonNullTag1 = tag1;
        Integer nonNullTag2 = tag2;
        if (tag1 == null) {
            nonNullTag1 = -1;
        }
        if (tag2 == null) {
            nonNullTag2 = -1;
        }
        return new RSEntry(opcode, ROBdestination, nonNullTag1, nonNullTag2, val1, val2);
    }
}
