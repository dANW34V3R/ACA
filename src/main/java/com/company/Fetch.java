package com.company;

public class Fetch implements Module{

    Processor p;
    Module nextModule;
    int width = 4;

    public Fetch(Processor proc, Module next) {
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {
//        if (!blocked()) {
            for (int i = 0; i < width; i++) {
                if (!nextModule.blocked()) {
                    // Increase PC by 1
                    p.ARF.set(30, p.BP.nextPC(p.ARF.get(30)));
                    if (p.INSMEM.size() - 1 < p.ARF.get(30)) {
                        // NOP to prevent out of bounds error at end of program due to pipelining
                        nextModule.setNextInstruction(new Instruction("NOP", 0, 0, 0, false));
                    } else {
                        // Create new instructions object to prevent values sticking e.g. valid
                        nextModule.setNextInstruction(new Instruction(p.INSMEM.get(p.ARF.get(30)), p.ARF.get(30), p.BP.taken(p.ARF.get(30))));
                    }
                }
            }
//        }
    }

    @Override
    public boolean blocked() {
        return nextModule.blocked();
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {return true;}

    @Override
    public void invalidateCurrentInstruction() {}

}
