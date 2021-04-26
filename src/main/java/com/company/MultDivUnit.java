package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultDivUnit implements Module{

    Processor p;
    Module nextModule;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public MultDivUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {
        stage3Tick();
        stage2Tick();
        stage1Tick();
    }

    private void stage1Tick() {
        // Find the first (oldest) entry in the RS that has all values
        Optional<RSEntry> entry = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).findFirst();

        if (entry.isPresent()) {
            stage1EndInstruction = entry.get();
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

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        if (RS.size() < RSsize) {
            int ROBindex;
            // Add ROB entry
            ROBindex = p.addROB(new ROBEntry(2, instruction.operand1, 0, false));
            // Add RS entry
            Integer RATtag1 = null;
            Integer val1 = null;
            Integer RATtag2 = null;
            Integer val2 = null;
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
