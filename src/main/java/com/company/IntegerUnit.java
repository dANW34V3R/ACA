package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IntegerUnit implements Module{

    Processor p;
    Module nextModule;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public IntegerUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {

        Optional<RSEntry> entry = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).findFirst();

        if (entry.isPresent()) {
            RSEntry validEntry = entry.get();

            RS.remove(validEntry);

            // WB, ROB entry , value, unused
            Instruction WBins = new Instruction("WB", validEntry.ROBdestination, 0, 0);
            p.noInstructions += 1;
            switch (validEntry.opcode) {
                case "MOVi":
                case "MOVPC":
                    WBins.operand2 = validEntry.val2;
                    break;
                case "MOV":
                    WBins.operand2 = validEntry.val1;
                    break;
                case "ADDi":
                case "ADD":
                    WBins.operand2 = validEntry.val1 + validEntry.val2;
                    break;
                case "SUBi":
                case "SUB":
                    WBins.operand2 = validEntry.val1 - validEntry.val2;
                    break;
                case "NOP":
                    p.noInstructions -= 1;
                    break;
                case "HALT":
                    break;
                default:
                    throw new java.lang.Error("opcode " + validEntry.opcode + " not recognised by IntegerUnit");
            }
            nextModule.setNextInstruction(WBins);
        }
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    // Puts instruction in RS and ROB if space
    public boolean setNextInstruction(Instruction instruction) {
        if (RS.size() < RSsize) {
            int ROBindex;
            if (instruction.opcode.compareTo("HALT") == 0) {
                ROBindex = p.addROB(new ROBEntry(instruction.operand1, 0, false, true));
                RS.add(new RSEntry(instruction.opcode, ROBindex, null, null, 0, 0));
                return true;
            }
            // Add ROB entry
            ROBindex = p.addROB(new ROBEntry(instruction.operand1, 0, false));
            if (instruction.opcode.compareTo("NOP") == 0) {
                p.ROB.get(ROBindex).WB = false;
//                System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOP");
            }
            // Add RS entry
            Integer RATtag1 = null;
            Integer val1 = null;
            Integer RATtag2 = null;
            Integer val2 = null;
            if (instruction.opcode.length() < 4) {
                // MOV, ADD, SUB, NOP
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
                if (instruction.opcode.compareTo("MOV") == 0) {
                    val2 = -1; //ignored
                    RATtag2 = null;
                }
            } else if (instruction.opcode.compareTo("MOVPC") == 0) {
                val1 = -1; //ignored
                val2 = instruction.PC;
            } else {
                // MOVi, ADDi, SUBi and MOVPC
                // val1 ignored for MOV
                RATtag1 = p.RAT.get(instruction.operand1);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand1);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }
                val2 = instruction.operand2;
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
        // TODO Invalidate all
//        nextInstruction.valid = false;
    }

    public void updateRS(int ROBdestination, int value) {
        System.out.println(ROBdestination + ":" + value);
        for (RSEntry entry : RS) {
            System.out.println(entry.toString());
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
}
