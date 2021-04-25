package com.company;

public class Decode implements Module{

    Processor p;
    Module nextModule;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    public Decode(Processor proc, Module next) {
        p = proc;
        nextModule = next;
        nextInstruction.valid = false;
    }


    @Override
    public void tick() {
//        System.out.println("DECODE" + blocked());
        if (!blocked()) {
            nextModule.setNextInstruction(nextInstruction);
        }
    }

    @Override
    public boolean blocked() {
        return nextModule.blocked();
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
