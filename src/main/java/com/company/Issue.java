package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Issue implements Module{

    Processor p;
    Execute nextModule;
    int width = 8;
    public List<Instruction> nextInstructionList = new ArrayList<>();

    boolean blocked = false;

    public Issue(Processor proc, Execute next) {
        p = proc;
        nextModule = next;
        for (Instruction nIns : nextInstructionList) {
            nIns.valid = false;
        }
    }

    @Override
    public void tick() {
//        System.out.println("ISSUE" + blocked());
//        if (!blocked()) {
//            nextModule.intUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.multDivUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.branchUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.loadStoreUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            // Check ROB status
//        if (nextInstructionList.size() < width) {
//            // prevents issue sticky blocking after flush
//            blocked = false;
//        }

        blocked = false;

        List<Instruction> movedOn = new ArrayList<>();
        for (Instruction nextInstruction : nextInstructionList) {
            if (!blocked) {
                if (!p.ROBFull()) {
                    if (nextInstruction.valid) {
                        // Decide which execution unit to go to
                        // Attempt to put instruction in RS, block if can't
                        if (Arrays.asList("MOV", "MOVi", "MOVPC", "ADDi", "ADD", "SUBi", "SUB", "CMP", "NOP", "HALT").contains(nextInstruction.opcode)) {
//                        System.out.println("intUnit");
                            blocked = !nextModule.intUnit.setNextInstruction(nextInstruction);
//                        System.out.println("ISSUE" + blocked);
                        } else if (Arrays.asList("MUL", "DIV").contains(nextInstruction.opcode)) {
//                        System.out.println("multDivUnit");
                            blocked = !nextModule.multDivUnit.setNextInstruction(nextInstruction);
                        } else if (Arrays.asList("BEQ", "BNE", "BLT", "BGT", "B", "BR").contains(nextInstruction.opcode)) {
//                        System.out.println("branchUnit");
                            blocked = !nextModule.branchUnit.setNextInstruction(nextInstruction);
                        } else if (Arrays.asList("LDRi", "LDR", "STRi", "STR").contains(nextInstruction.opcode)) {
//                        System.out.println("loadStoreUnit");
                            blocked = !nextModule.loadStoreUnit.setNextInstruction(nextInstruction);
                        } else {
                            throw new java.lang.Error("Unrecognised opcode in Issue: '" + nextInstruction.opcode + "'");
                        }
                        if (!blocked) {
                            movedOn.add(nextInstruction);
                        }
                    } else {
                        // ignore invalid instructions
                        movedOn.add(nextInstruction);
                    }
//            } else {
//                blocked = true;
                }
            }
        }
        nextInstructionList.removeAll(movedOn);
//        }
    }

    @Override
    public boolean blocked() {
        return nextInstructionList.size() >= width;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        // Get rid of invalid instructions
        if (instruction.valid) {
            nextInstructionList.add(instruction);
        }
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        for (Instruction nIns : nextInstructionList) {
            nIns.valid = false;
        }
    }
}
