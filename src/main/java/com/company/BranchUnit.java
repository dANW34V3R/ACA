package com.company;

import java.util.*;
import java.util.stream.Collectors;

public class BranchUnit implements Module{

    Processor p;
    Module nextModule;
    Execute exUnit;

    List<? extends Module> frontEnd;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    // Initialise random
    Random rand = new Random();

    public BranchUnit(Processor proc, Module next, Execute executionUnit){
        p = proc;
        nextModule = next;
        exUnit = executionUnit;
    }

//    public void setFrontEnd(List<? extends Module> frontEndList) {
//        frontEnd = frontEndList;
//    }
//
//    private void invalidatePipeline() {
//        for (Module module : frontEnd) {
//            module.invalidateCurrentInstruction();
//        }
//    }

    @Override
    public void tick() {

        // Get list of instructions ready to dispatch
        List<RSEntry> validEntriesList = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).collect(Collectors.toList());

        if (validEntriesList.size() > 0) {
            // RS policies
//            RSEntry validEntry = validEntriesList.get(rand.nextInt(validEntriesList.size())); //random
//            RSEntry validEntry = validEntriesList.get(validEntriesList.size() - 1); //newest
            RSEntry validEntry = validEntriesList.get(0); //oldest
//            RSEntry validEntry = validEntriesList.get(exUnit.getMostDependedOn(validEntriesList)); //max dependence

            RS.remove(validEntry);

            // Data passed to broadcast
            // WB, ROB entry , value, unused
            Instruction WBins = new Instruction("WB", validEntry.ROBdestination, validEntry.val1 - 1, 0, p.ROB.get(validEntry.ROBdestination).instructionPC);

            // Check CMP flag and set ROB branch flag
            switch (validEntry.opcode) {
                case "BEQ":
                    if (validEntry.val2 == 0) {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = true;
                    } else {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = false;
                    }
                    break;
                case "BNE":
                    if (validEntry.val2 != 0) {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = true;
                    } else {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = false;
                    }
                    break;
                case "BLT":
                    if (validEntry.val2 == -1) {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = true;
                    }else {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = false;
                    }
                    break;
                case "BGT":
                    if (validEntry.val2 == 1) {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = true;
                    } else {
                        p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = false;
                    }
                    break;
                case "BR":
                    WBins.operand2++;
                case "B":
                    p.ROB.get(validEntry.ROBdestination).branchExecuteTaken = true;
                    break;
                default:
                    System.out.println("opcode " + validEntry.opcode + " not recognised");
                    break;
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
            // create ROB entry
            int ROBindex = p.addROB(new ROBEntry(0, 30, 0, false, instruction.branchTaken, instruction.PC));

            // Add RS entry
            // Where to branch
            Integer RATtag1 = null;
            Integer val1 = instruction.operand1;

            // CMP flag value
            Integer RATtag2 = null;
            Integer val2 = null;

            if (instruction.opcode.compareTo("B") == 0) {
                val2 = -2;
            } else if (instruction.opcode.compareTo("BR") == 0) {

                // Need value from given register
                val1 = null;
                // set val1
                RATtag1 = p.RAT.get(instruction.operand1);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand1);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }

                val2 = -2;
            } else {
                // CMP value i.e. if to branch

                // Search up through ROB
                // Find most recent CMP
                // If no CMP set val2 = p.f
                // Else wait on CMP value

                // TODO will break if first ins is branch
                if (ROBindex > p.ROBcommit) {
                    // Issue pointer after commit
                    for (int i = ROBindex; i > p.ROBcommit - 1; i--) {
                        // if entry is a CMP
                        if (p.ROB.get(i).type == 3) {
                            if (p.ROB.get(i).ready) {
                                p.f = p.ROB.get(i).value;
                            } else {
                                RATtag2 = i;
                            }
                            break;
                        }
                    }
                } else {
                    // Commit pointer after issue
                    boolean found = false;
                    // Search down to 0
                    for (int i = ROBindex; i > -1; i--) {
                        // if entry is a CMP
                        if (p.ROB.get(i).type == 3) {
                            if (p.ROB.get(i).ready) {
                                p.f = p.ROB.get(i).value;
                            } else {
                                RATtag2 = i;
                            }
                            found = true;
                            break;
                        }
                    }
                    // Search from end to commit
                    if (!found) {
                        for (int i = p.ROBSize - 1; i > p.ROBcommit - 1; i--) {
                            if (p.ROB.get(i).type == 3) {
                                if (p.ROB.get(i).ready) {
                                    p.f = p.ROB.get(i).value;
                                } else {
                                    RATtag2 = i;
                                }
                                break;
                            }
                        }
                    }
                }

                if (RATtag2 == null) {
                    val2 = p.f;
                }
            }

            // opcode, branch to, flag val
            RS.add(new RSEntry(instruction.opcode, ROBindex, RATtag1, RATtag2, val1, val2));
            // Add RAT entry
            p.RAT.set(30, ROBindex);
            return true;
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
