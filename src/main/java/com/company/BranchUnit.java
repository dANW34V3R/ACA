package com.company;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BranchUnit implements Module{

    Processor p;
    Module nextModule;

    List<? extends Module> frontEnd;

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public BranchUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }

    public void setFrontEnd(List<? extends Module> frontEndList) {
        frontEnd = frontEndList;
    }

    private void invalidatePipeline() {
        for (Module module : frontEnd) {
            module.invalidateCurrentInstruction();
        }
    }

    @Override
    public void tick() {

        Optional<RSEntry> entry = RS.stream().filter(rsEntry -> rsEntry.val1 != null && rsEntry.val2 != null).findFirst();

        if (entry.isPresent()) {
            RSEntry validEntry = entry.get();

            RS.remove(validEntry);

            p.noInstructions += 1;

            // WB, ROB entry , value, unused
            Instruction WBins = new Instruction("WB", validEntry.ROBdestination, 0, 0);

            switch (validEntry.opcode) {
                case "NOP":
                    break;
                case "BEQ":
                    if (validEntry.val2 == 0) {
//                        p.ARF.set(30, validEntry.val1 - 1);
                        WBins.operand2 = validEntry.val1 - 1;
                        p.ROB.get(validEntry.ROBdestination).misPredict = true;
                    }
                    break;
                case "BNE":
                    if (validEntry.val2 != 0) {
                        WBins.operand2 = validEntry.val1 - 1;
                        p.ROB.get(validEntry.ROBdestination).misPredict = true;
                    }
                    break;
                case "BLT":
                    if (validEntry.val2 == -1) {
                        WBins.operand2 = validEntry.val1 - 1;
                        p.ROB.get(validEntry.ROBdestination).misPredict = true;
                    }
                    break;
                case "BGT":
                    if (validEntry.val2 == 1) {
                        WBins.operand2 = validEntry.val1 - 1;
                        p.ROB.get(validEntry.ROBdestination).misPredict = true;
                    }
                    break;
                case "BR":
                    validEntry.val1++;
                case "B":
                    WBins.operand2 = validEntry.val1 - 1;
                    p.ROB.get(validEntry.ROBdestination).misPredict = true;
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

    @Override
    public boolean setNextInstruction(Instruction instruction) {

        if (RS.size() < RSsize) {
            int ROBindex = p.addROB(new ROBEntry(0, 30, 0, false));

            // Add RS entry
            // Where to branch
            Integer RATtag1 = null;
            Integer val1 = instruction.operand1;

            Integer RATtag2 = null;
            Integer val2 = null;

            if (instruction.opcode.compareTo("B") == 0) {
                val2 = -2;
            } else if (instruction.opcode.compareTo("BR") == 0) {

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
                for (int i = ROBindex; i > p.ROBcommit - 1; i--) {
                    // TODO mod
                    if (p.ROB.get(i).type == 3) {
                        if (p.ROB.get(i).ready) {
                            p.f = p.ROB.get(i).value;
                        } else {
                            RATtag2 = i;
                        }
                        break;
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
