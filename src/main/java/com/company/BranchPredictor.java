package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BranchPredictor{

    //current PC, branch to PC, taken

    private List<BTBEntry> BTB = new ArrayList<>();
    private boolean pipelineFlush = false;

    public BranchPredictor() {

    }

    public int nextPC(int currentPC) {
        if (pipelineFlush) {
            pipelineFlush = false;
            return currentPC + 1;
        }
        Optional<BTBEntry> entry = BTB.stream().filter(e -> e.PC == currentPC).findFirst();
        if (entry.isPresent()) {
            System.out.println("entry present");
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
            System.out.println("create BTB entry: " + currentPC);
            // create
            int val = taken ? 2 : 1;
            BTB.add(new BTBEntry(currentPC, branchToPC, val));
        }
    }

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
