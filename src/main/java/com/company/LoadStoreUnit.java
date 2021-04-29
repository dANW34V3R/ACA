package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LoadStoreUnit implements Module{

    Processor p;
    Broadcast WBModule;
    Memory memModule;

    int noUnits = 4;

    // Load Store Queue
    int LSQsize = 20;
    List<LSQEntry> LSQ = new ArrayList<>();

    public LoadStoreUnit(Processor proc, Broadcast next, Memory memMod){
        p = proc;
        WBModule = next;
        memModule = memMod;
    }

    @Override
    public void tick() {
        for (int k = 0; k < noUnits; k++) {

            Optional<LSQEntry> entry = LSQ.stream().filter(lsqEntry -> lsqEntry.val1 != null && lsqEntry.val2 != null && lsqEntry.strValVal != null && !lsqEntry.complete && !lsqEntry.waiting).findFirst();

            if (entry.isPresent()) {
                LSQEntry validEntry = entry.get();

                // address calculation
                LSQ.get(LSQ.indexOf(validEntry)).address = validEntry.val1 + validEntry.val2;

                // check for value forwarding
                if (validEntry.LSBool) {
                    // load
                    // look up through LSQ for store with same address

                    for (int i = LSQ.indexOf(validEntry) - 1; i > -1; i--) {
                        LSQEntry storeEntry = LSQ.get(i);
                        if (!storeEntry.LSBool && storeEntry.address == validEntry.address && storeEntry.value != null) {
                            LSQ.get(LSQ.indexOf(validEntry)).value = storeEntry.value;
                            LSQ.get(LSQ.indexOf(validEntry)).complete = true;
                            break;
                        }
                    }
                    // if no forwarding, get value from memory
                    if (!LSQ.get(LSQ.indexOf(validEntry)).complete) {
                        // type, address, sequence number, unused
                        memModule.setNextInstruction(new Instruction("load", validEntry.val1 + validEntry.val2, validEntry.sequenceNumber, 0));
                        LSQ.get(LSQ.indexOf(validEntry)).waiting = true;
                    }
                } else {
                    // store
                    // look to forward through LSQ

                    for (int i = LSQ.indexOf(validEntry) + 1; i < LSQ.size(); i++) {
                        LSQEntry loadEntry = LSQ.get(i);
                        if (!loadEntry.LSBool && loadEntry.address == validEntry.address) {
                            // if entry is a store with the same address then break
                            break;
                        }

                        if (loadEntry.LSBool && loadEntry.address == validEntry.address) {
                            // if entry is a load with the same address, forward value and keep going down LSQ
                            LSQ.get(LSQ.indexOf(loadEntry)).value = validEntry.strValVal;
                            LSQ.get(LSQ.indexOf(loadEntry)).complete = true;
                        }
                    }

                    LSQ.get(LSQ.indexOf(validEntry)).complete = true;

                    // set ROB entry to ready so store can be commited
                    p.ROB.get(validEntry.ROBdestination).ready = true;
                }
            }

            // Check if head of LSQ can be committed
            if (LSQ.size() > 0) {
                LSQEntry head = LSQ.get(0);
                if (head.LSBool && head.complete) {
                    // if the head instruction is a load and complete then WB
                    // WB, ROB entry , value, unused
                    Instruction WBins = new Instruction("WB", head.ROBdestination, head.value, 0);
                    WBModule.setNextInstruction(WBins);
                    LSQ.remove(0);
                }
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

        if (LSQ.size() < LSQsize) {

            // Add RS entry
            Integer RATtag1 = null;
            Integer val1 = null;
            Integer RATtag2 = null;
            Integer val2 = null;
            Integer strRATtag = null;
            Integer strval = null;

            if (Arrays.asList("LDRi", "LDR").contains(instruction.opcode)) {
                // load
                int ROBindex = p.addROB(new ROBEntry(1, instruction.operand1, 0, false));

                // set val1
                RATtag1 = p.RAT.get(instruction.operand2);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand2);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }

                if (instruction.opcode.compareTo("LDRi") == 0) {
                    val2 = instruction.operand3;
                } else {
                    // set val2
                    RATtag2 = p.RAT.get(instruction.operand3);
                    if (RATtag2 == null) {
                        val2 = p.ARF.get(instruction.operand3);
                    } else if (p.ROB.get(RATtag2).ready) {
                        // Check whether value is already available
                        val2 = p.ROB.get(RATtag2).value;
                        RATtag2 = null;
                    }
                }

                LSQ.add(new LSQEntry(ROBindex, true, null, null, RATtag1, val1, RATtag2, val2, null, 0));

                // Add RAT entry
                p.RAT.set(instruction.operand1, ROBindex);

                return true;

            } else {
                // store
                int ROBindex = p.addROB(new ROBEntry(5, -1, 0, false));

                // set strVal
                strRATtag = p.RAT.get(instruction.operand1);
                if (strRATtag == null) {
                    strval = p.ARF.get(instruction.operand1);
                } else if (p.ROB.get(strRATtag).ready) {
                    // Check whether value is already available
                    strval = p.ROB.get(strRATtag).value;
                    strRATtag = null;
                }

                // set val1
                RATtag1 = p.RAT.get(instruction.operand2);
                if (RATtag1 == null) {
                    val1 = p.ARF.get(instruction.operand2);
                } else if (p.ROB.get(RATtag1).ready) {
                    // Check whether value is already available
                    val1 = p.ROB.get(RATtag1).value;
                    RATtag1 = null;
                }

                if (instruction.opcode.compareTo("STRi") == 0) {
                    val2 = instruction.operand3;
                } else {
                    // set val2
                    RATtag2 = p.RAT.get(instruction.operand3);
                    if (RATtag2 == null) {
                        val2 = p.ARF.get(instruction.operand3);
                    } else if (p.ROB.get(RATtag2).ready) {
                        // Check whether value is already available
                        val2 = p.ROB.get(RATtag2).value;
                        RATtag2 = null;
                    }
                }

                LSQ.add(new LSQEntry(ROBindex, false, null, null, RATtag1, val1, RATtag2, val2, strRATtag, strval));

                return true;
            }
        }

        return false;

    }

    @Override
    public void invalidateCurrentInstruction() {
        LSQ.clear();
    }

    // Update entries on value broadcast
    public void updateLSQ(int ROBdestination, int value){
        for (LSQEntry entry : LSQ) {
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
            if (entry.strValTag != null) {
                if (entry.strValTag == ROBdestination) {
                    entry.strValTag = null;
                    entry.strValVal = value;
                }
            }
        }
    }

    // Called by commit
    // Sends first entry of LSQ to be stored
    public void sendStoreToMem(int ROBindex) {
        LSQEntry storeEntry = LSQ.get(0);

        if (storeEntry.ROBdestination != ROBindex) {
            throw new java.lang.Error("Store ROB index does not align");
        }

        if (storeEntry.LSBool) {
            throw new java.lang.Error("Using load instruction to store");
        }

        LSQ.remove(0);
        // type, address, sequence number, value
        memModule.setNextInstruction(new Instruction("store", storeEntry.address, storeEntry.sequenceNumber, storeEntry.strValVal));
    }

    // Called by memory
    // Updates load LSQ entry with value from memory
    public void sendLoad(int value, int sequenceNumber) {
        for (LSQEntry entry : LSQ) {
            if(entry.sequenceNumber == sequenceNumber) {
                LSQ.get(LSQ.indexOf(entry)).value = value;
                LSQ.get(LSQ.indexOf(entry)).complete = true;
            }
        }
    }
}
