package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Issue implements Module{

    Processor p;
    Execute nextModule;
    int width = 4;
    int IQLength = 20;
    public List<Instruction> nextInstructionList = new ArrayList<>();

    boolean blocked = false;

    public Issue(Processor proc, Execute next) {
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {

        blocked = false;
        int instructionsMoved = 0;

        List<Instruction> movedOn = new ArrayList<>();
        for (Instruction nextInstruction : nextInstructionList) {
            if (!blocked && instructionsMoved < width) {
                instructionsMoved++;
                if (!p.ROBFull()) {
//                    if (nextInstruction.valid) {
                        // Decide which execution unit to issue to
                        // Attempt to put instruction in RS, block if can't preventing any more instructions being issued this cycle
 //---------------------// TODO issue out of order, need to change how ROB entry is created e.g. module before this
                        if (Arrays.asList("MOV", "MOVi", "MOVPC", "ADDi", "ADD", "SUBi", "SUB", "CMP", "NOP", "HALT").contains(nextInstruction.opcode)) {
                            blocked = !nextModule.intUnit.setNextInstruction(nextInstruction);
                        } else if (Arrays.asList("MUL", "DIV").contains(nextInstruction.opcode)) {
                            blocked = !nextModule.multDivUnit.setNextInstruction(nextInstruction);
                        } else if (Arrays.asList("BEQ", "BNE", "BLT", "BGT", "B", "BR").contains(nextInstruction.opcode)) {
                            blocked = !nextModule.branchUnit.setNextInstruction(nextInstruction);
                        } else if (Arrays.asList("LDRi", "LDR", "STRi", "STR").contains(nextInstruction.opcode)) {
                            blocked = !nextModule.loadStoreUnit.setNextInstruction(nextInstruction);
                        } else {
                            throw new java.lang.Error("Unrecognised opcode in Issue: '" + nextInstruction.opcode + "'");
                        }
                        if (!blocked) {
                            movedOn.add(nextInstruction);
                        }
//                    } else {
//                        // ignore invalid instructions
//                        movedOn.add(nextInstruction);
//                    }
                }
            }
        }
        nextInstructionList.removeAll(movedOn);
    }

    @Override
    public boolean blocked() {
        return nextInstructionList.size() >= IQLength;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        if (nextInstructionList.size() < IQLength) {
            // Get rid of invalid instructions
            if (instruction.valid) {
                nextInstructionList.add(instruction);
            }
            return true;
        }
        return false;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstructionList.clear();
    }
}
