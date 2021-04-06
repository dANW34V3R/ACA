package com.company;

public class Decode implements Module{

    Processor p;
    Module nextModule;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    public Decode(Processor proc, Module next) {
        p = proc;
        nextModule = next;
    }


    @Override
    public void tick() {
        if (!blocked()) {
            nextModule.setNextInstruction(nextInstruction);
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
