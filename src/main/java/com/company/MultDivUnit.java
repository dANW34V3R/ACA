package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MultDivUnit implements Module{

    Processor p;
    Module nextModule;
    Execute exUnit;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    Random rand = new Random();

    public MultDivUnit(Processor proc, Module next, Execute executionUnit){
        p = proc;
        nextModule = next;
        exUnit = executionUnit;
    }

    // Tick sub-pipeline
    @Override
    public void tick() {
        stage3Tick();
        stage2Tick();
        stage1Tick();
    }

    private void stage1Tick() {

        // Get list of instructions ready to dispatch
        List<RSEntry> validEntriesList = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).collect(Collectors.toList());

        if (validEntriesList.size() > 0) {
            // RS policies
//            RSEntry entry = validEntriesList.get(rand.nextInt(validEntriesList.size())); //random
//            RSEntry entry = validEntriesList.get(validEntriesList.size() - 1); //newest
            RSEntry entry = validEntriesList.get(0); //oldest
//            RSEntry entry = validEntriesList.get(exUnit.getMostDependedOn(validEntriesList)); //max dependence

            stage1EndInstruction = entry;
            RS.remove(stage1EndInstruction);
        } else {
            stage1EndInstruction = null;
        }
    }

    private RSEntry stage1EndInstruction = null;

    private void stage2Tick(){stage2EndInstruction = stage1EndInstruction;}

    private RSEntry stage2EndInstruction = null;

    private void stage3Tick(){

        RSEntry validEntry = stage2EndInstruction;

        // Perform operation
        if (validEntry != null) {
            // WB, ROB entry , value, unused
            Instruction WBins = new Instruction("WB", validEntry.ROBdestination, 0, 0);
            switch (validEntry.opcode) {
                case "MUL":
                    WBins.operand2 = validEntry.val1 * validEntry.val2;
                    break;
                case "DIV":
                    //account for div by 0 error
                    if (validEntry.val2 == 0) {
                        throw new java.lang.Error("Divide by 0");
                    }
                    WBins.operand2 = validEntry.val1 / validEntry.val2;
                    break;
                default:
                    throw new java.lang.Error("opcode " + validEntry.opcode + " not recognised in MultDivUnit");
            }
            nextModule.setNextInstruction(WBins);
        }
    }

    @Override
    public boolean blocked() {
        return false;
    }

    // Called by issue
    // Creates ROB entry, RS entry and RAT entry
    // Returns false if blocked
    @Override
    public boolean setNextInstruction(Instruction instruction) {
        if (RS.size() < RSsize) {
            // Add ROB entry
            int ROBindex = p.addROB(new ROBEntry(2, instruction.operand1, 0, false));

            Integer RATtag1 = null;
            Integer val1 = null;
            Integer RATtag2 = null;
            Integer val2 = null;

            // Check RAT and ROB to determine where operands come from
            RATtag1 = p.RAT.get(instruction.operand2);
            if (RATtag1 == null) {
                val1 = p.ARF.get(instruction.operand2);
            } else if (p.ROB.get(RATtag1).ready) {
                // Check whether value is already available
                val1 = p.ROB.get(RATtag1).value;
                RATtag1 = null;
            }

            RATtag2 = p.RAT.get(instruction.operand3);
            if (RATtag2 == null) {
                val2 = p.ARF.get(instruction.operand3);
            } else if (p.ROB.get(RATtag2).ready) {
                // Check whether value is already available
                val2 = p.ROB.get(RATtag2).value;
                RATtag2 = null;
            }

            // Add RS entry
            RS.add(new RSEntry(instruction.opcode, ROBindex, RATtag1, RATtag2, val1, val2));
            // Add RAT entry
            p.RAT.set(instruction.operand1, ROBindex);
            return true;
        }
        return false;
    }

    @Override
    public void invalidateCurrentInstruction() {
        RS.clear();
        stage1EndInstruction = null;
        stage2EndInstruction = null;
    }

    // Update entries on value broadcast
    public void updateRS(int ROBdestination, int value) {
        for (RSEntry entry : RS) {
            if (entry.tag1 != null) {
                if (entry.tag1 == ROBdestination) {
                    entry.tag1 = null;
                    entry.val1 = value;
                }
            }
            if (entry.tag2 != null) {
                if (entry.tag2 == ROBdestination) {
                    entry.tag2 = null;
                    entry.val2 = value;
                }
            }
        }
    }

    public void printState() {
        String s1 = stage1EndInstruction != null ? stage1EndInstruction.toString() : "NULL";
        String s2 = stage2EndInstruction != null ? stage2EndInstruction.toString() : "NULL";
        System.out.println(s1 + "+" + s2);
    }
}
