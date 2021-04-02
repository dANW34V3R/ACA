package com.company;

public class Fetch implements Module{

    Processor p;

    public Fetch(Processor proc) {
        p = proc;
    }

    @Override
    public void tick() {
        if (!blocked()) {
            // Increase PC by 1
            p.ARF.set(30, p.ARF.get(30) + 1);
            if (p.INSMEM.size() - 1 < p.ARF.get(30)) {
                // NOP to prevent out of bounds error at end of program due to pipelining
                p.fetchInstruction = new Instruction("NOP", 0, 0, 0);
            } else {
                // Create new instructions object to prevent values sticking e.g. valid
                p.fetchInstruction = new Instruction(p.INSMEM.get(p.ARF.get(30)));
                p.fetchInstruction.PC = p.ARF.get(30);
            }
        }
    }

    @Override
    public boolean blocked() {
        return p.insD.blocked();
    }
}
