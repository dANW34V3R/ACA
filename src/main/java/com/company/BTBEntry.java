package com.company;

public class BTBEntry {

    //current PC, branch to PC, taken
    int PC;
    int branchToPC;
    // Strongly not taken, not taken, taken, strongly taken
    // 0, 1, 2, 3 if < 2 not taken else taken
    int bitPredictor;

    public BTBEntry(int PCVal, int branchToPCVal, int bitPredictorVal){
        PC = PCVal;
        branchToPC = branchToPCVal;
        bitPredictor = bitPredictorVal;
    }

    // updates BTB entry depending on whether the branch was taken or not
    public void updateEntry(boolean taken) {
        if (taken) {
            bitPredictor++;
            if (bitPredictor > 3) {
                bitPredictor = 3;
            }
        } else {
            bitPredictor--;
            if (bitPredictor < 0) {
                bitPredictor = 0;
            }
        }
    }

    // Returns the predicted next PC value for this entry
    public int getNextPC() {
        if (bitPredictor > 1) {
            return branchToPC;
        } else {
            return PC + 1;
        }
    }

    public boolean taken(){
        return bitPredictor > 1;
    }

    @Override
    public String toString() {
        return "BTBEntry{" +
                "PC=" + PC +
                ", branchToPC=" + branchToPC +
                ", bitPredictor=" + bitPredictor +
                '}';
    }
}
