package com.company;

public class ROBEntry {
    //type 0 = branch, 1 = load, 2 = register, 3 = CMP, 4 = HALT, 5 = store
    int type;
    int destinationRegister;
    int value;
    boolean ready;

    boolean branchFetchTaken = false;
    boolean branchExecuteTaken = false;
    int instructionPC;

    public ROBEntry(int typeVal, int destReg, int val, boolean readyVal) {
        type = typeVal;
        destinationRegister = destReg;
        value = val;
        ready = readyVal;
    }

    public ROBEntry(int typeVal, int destReg, int val, boolean readyVal, boolean branchTakenVal, int instructionPCVal) {
        type = typeVal;
        destinationRegister = destReg;
        value = val;
        ready = readyVal;
        branchFetchTaken = branchTakenVal;
        instructionPC = instructionPCVal;
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
