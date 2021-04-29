package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BranchPredictor{


    public List<BTBEntry> BTB = new ArrayList<>();
    private boolean pipelineFlush = false;

    public BranchPredictor() {}

    public int nextPC(int currentPC) {
        if (pipelineFlush) {
            // if pipeline has just been flushed, do not attempt to jump from incorrectly speculated instruction
            pipelineFlush = false;
            return currentPC + 1;
        }

        // Find first BTB entry matching the current PC
        Optional<BTBEntry> entry = BTB.stream().filter(e -> e.PC == currentPC).findFirst();
        if (entry.isPresent()) {
            return entry.get().getNextPC();
        } else {
            return currentPC + 1;
        }
    }

    public void updateBTB(int currentPC, int branchToPC, boolean taken){
        Optional<BTBEntry> entry = BTB.stream().filter(e -> e.PC == currentPC).findFirst();
        if (entry.isPresent()) {
            // update
            entry.get().updateEntry(taken);
        } else {
            // create entry if no entry exists
            // initialise based on whether branch was initially taken
            int val = taken ? 2 : 1;
            BTB.add(new BTBEntry(currentPC, branchToPC, val));
        }
    }

    // returns prediction of BTB for currentPC
    public boolean taken(int currentPC) {
        Optional<BTBEntry> entry = BTB.stream().filter(e -> e.PC == currentPC).findFirst();
        if (entry.isPresent()) {
            return entry.get().taken();
        } else {
            return false;
        }
    }

    public void setPipelineFlush() {
        pipelineFlush = true;
    }
}
