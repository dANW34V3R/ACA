package com.company;

import java.util.Arrays;

public class Issue implements Module{

    Processor p;
    Execute nextModule;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    boolean blocked = false;

    public Issue(Processor proc, Execute next) {
        p = proc;
        nextModule = next;
        nextInstruction.valid = false;
    }

    @Override
    public void tick() {
        System.out.println("ISSUE" + blocked());
//        if (!blocked()) {
//            nextModule.intUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.multDivUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.branchUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
//            nextModule.loadStoreUnit.setNextInstruction(new Instruction("NOP", 0, 0,0));
            // Check ROB status
            if (!p.ROBFull()){
                if (nextInstruction.valid) {
                    // Decide which execution unit to go to
                    // Attempt to put instruction in RS, block if can't
                    if (Arrays.asList("MOV", "MOVi", "MOVPC", "ADDi", "ADD", "SUBi", "SUB", "CMP", "NOP", "HALT").contains(nextInstruction.opcode)) {
                        System.out.println("intUnit");
                        blocked = !nextModule.intUnit.setNextInstruction(nextInstruction);
                        System.out.println("ISSUE" + blocked);
                    } else if (Arrays.asList("MUL", "DIV").contains(nextInstruction.opcode)) {
                        System.out.println("multDivUnit");
                        blocked = !nextModule.multDivUnit.setNextInstruction(nextInstruction);
                    } else if (Arrays.asList("BEQ", "BNE", "BLT", "BGT", "B", "BR").contains(nextInstruction.opcode)) {
                        System.out.println("branchUnit");
                        blocked = !nextModule.branchUnit.setNextInstruction(nextInstruction);
                    } else if (Arrays.asList("LDRi", "LDR", "STRi", "STR").contains(nextInstruction.opcode)) {
                        System.out.println("loadStoreUnit");
                        blocked = !nextModule.loadStoreUnit.setNextInstruction(nextInstruction);
                    } else {
                        throw new java.lang.Error("Unrecognised opcode in Issue: '" + nextInstruction.opcode + "'");
                    }
                }
            } else {
                blocked = true;
            }
//        }
    }

    @Override
    public boolean blocked() {
        return nextModule.blocked() || blocked;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
        return false;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
