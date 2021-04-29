package com.company;

import java.util.*;
import java.util.stream.Collectors;

public class IntegerUnit implements Module{

    Processor p;
    Module nextModule;
    Execute exUnit;

    int noUnits = 2;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    Random rand = new Random();

    public IntegerUnit(Processor proc, Module next, Execute executionUnit){
        p = proc;
        nextModule = next;
        exUnit = executionUnit;
    }

    @Override
    public void tick() {

        // Loop for each unit, simulates grouped RS
        for (int i = 0; i < noUnits; i++) {

            // Get list of instructions ready to dispatch
            List<RSEntry> validEntriesList = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).collect(Collectors.toList());

            if (validEntriesList.size() > 0) {
                // RS policies
//                RSEntry validEntry = validEntriesList.get(rand.nextInt(validEntriesList.size())); //random
//                RSEntry validEntry = validEntriesList.get(validEntriesList.size() - 1); //newest
                RSEntry validEntry = validEntriesList.get(0); //oldest
//                RSEntry validEntry = validEntriesList.get(exUnit.getMostDependedOn(validEntriesList)); //max dependence

                RS.remove(validEntry);

                // WB, ROB entry , value, unused
                Instruction WBins = new Instruction("WB", validEntry.ROBdestination, 0, 0);
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
                    case "CMP":
                        //op1 - op2 , update flags
                        int result = validEntry.val1 - validEntry.val2;
                        if (result == 0) {
                            WBins.operand2 = 0;
                        } else if (result < 0) {
                            WBins.operand2 = -1;
                        } else {
                            WBins.operand2 = 1;
                        }
                        break;
                    case "NOP":
                        break;
                    case "HALT":
                        break;
                    default:
                        throw new java.lang.Error("opcode " + validEntry.opcode + " not recognised by IntegerUnit");
                }
                nextModule.setNextInstruction(WBins);
            }
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
            int ROBindex;
            if (instruction.opcode.compareTo("HALT") == 0) {
                ROBindex = p.addROB(new ROBEntry(4, -1, 0, false));
                RS.add(new RSEntry(instruction.opcode, ROBindex, null, null, 0, 0));
                return true;
            }
            else if (Arrays.asList("CMP", "NOP").contains(instruction.opcode)) {
                // Add ROB entry
                ROBindex = p.addROB(new ROBEntry(3, -1, 0, false));

                // Add RS entry
                Integer RATtag1 = null;
                Integer val1 = null;
                Integer RATtag2 = null;
                Integer val2 = null;

                // set val1
                RATtag1 = p.RAT.get(instruction.operand1);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand1);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }
                // set val2
                RATtag2 = p.RAT.get(instruction.operand2);
                if (RATtag2 == null) {
                    val2 = p.ARF.get(instruction.operand2);
                } else if (p.ROB.get(RATtag2).ready) {
                    // Check whether value is already available
                    val2 = p.ROB.get(RATtag2).value;
                    RATtag2 = null;
                }

                RS.add(new RSEntry(instruction.opcode, ROBindex, RATtag1, RATtag2, val1, val2));

                return true;
            } else if (Arrays.asList("MOV", "ADD", "SUB").contains(instruction.opcode)) {
                // Add ROB entry
                ROBindex = p.addROB(new ROBEntry(2, instruction.operand1, 0, false));

                // Add RS entry
                Integer RATtag1 = null;
                Integer val1 = null;
                Integer RATtag2 = null;
                Integer val2 = null;

                // set val1
                RATtag1 = p.RAT.get(instruction.operand2);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand2);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }
                // set val2
                RATtag2 = p.RAT.get(instruction.operand3);
                if (RATtag2 == null) {
                    val2 = p.ARF.get(instruction.operand3);
                } else if (p.ROB.get(RATtag2).ready) {
                    // Check whether value is already available
                    val2 = p.ROB.get(RATtag2).value;
                    RATtag2 = null;
                }

                if (instruction.opcode.compareTo("MOV") == 0) {
                    val2 = -1; //ignored but allows MOV to move through exe as all vals populated
                    RATtag2 = null;
                }

                RS.add(new RSEntry(instruction.opcode, ROBindex, RATtag1, RATtag2, val1, val2));
                // Add RAT entry
                p.RAT.set(instruction.operand1, ROBindex);
                return true;
            } else {

                ROBindex = p.addROB(new ROBEntry(2, instruction.operand1, 0, false));

                // Add RS entry
                Integer RATtag1 = null;
                Integer val1 = null;
                Integer RATtag2 = null;
                Integer val2 = null;

                if (instruction.opcode.compareTo("MOVPC") == 0) {
                    val1 = -1; //ignored ignored but allows MOV to move through exe as all vals populated
                    val2 = instruction.PC;
                } else {
                    // MOVi, ADDi, SUBi
                    // Use operand 2 as val2 as these are already provided by issue
                    // set val1

                    if (instruction.opcode.compareTo("MOVi") == 0) {
                        val1 = -1;
                        RATtag1 = null;
                    } else {
                        RATtag1 = p.RAT.get(instruction.operand1);
                        if (RATtag1 == null) {
                            val1 = p.ARF.get(instruction.operand1);
                        } else if (p.ROB.get(RATtag1).ready) {
                            // Check whether value is already available
                            val1 = p.ROB.get(RATtag1).value;
                            RATtag1 = null;
                        }
                    }

                    val2 = instruction.operand2;
                }

                RS.add(new RSEntry(instruction.opcode, ROBindex, RATtag1, RATtag2, val1, val2));
                // Add RAT entry
                p.RAT.set(instruction.operand1, ROBindex);
                return true;
            }
        }
        return false;
    }

    @Override
    public void invalidateCurrentInstruction() {
        RS.clear();
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
}
