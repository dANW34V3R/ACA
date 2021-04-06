package com.company;

import java.util.Arrays;

public class Issue implements Module{

    Processor p;
    Execute nextModule;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    public Issue(Processor proc, Execute next) {
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {
        if (!blocked()) {
            nextModule.intUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            nextModule.multDivUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            nextModule.branchUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            nextModule.loadStoreUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            // Decide which execution unit to go to
            if (Arrays.asList("MOV", "MOVi", "MOVPC", "ADDi", "ADD", "SUBi", "SUB", "NOP", "HALT").contains(nextInstruction.opcode)) {
                System.out.println("intUnit");
                nextModule.intUnit.setNextInstruction(nextInstruction);
            } else if (Arrays.asList("MUL", "DIV").contains(nextInstruction.opcode)) {
                System.out.println("multDivUnit");
                nextModule.multDivUnit.setNextInstruction(nextInstruction);
            } else if (Arrays.asList("CMP", "BEQ", "BNE", "BLT", "BGT", "B", "BR").contains(nextInstruction.opcode)) {
                System.out.println("branchUnit");
                nextModule.branchUnit.setNextInstruction(nextInstruction);
            } else if (Arrays.asList("LDRi", "LDR", "STRi", "STR").contains(nextInstruction.opcode)) {
                System.out.println("loadStoreUnit");
                nextModule.loadStoreUnit.setNextInstruction(nextInstruction);
            } else {
                throw new java.lang.Error("Unrecognised opcode in Issue");
            }
        }
    }

    @Override
    public boolean blocked() {
        return nextModule.blocked();
    }

    @Override
    public void setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
