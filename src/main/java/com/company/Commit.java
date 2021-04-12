package com.company;

public class Commit implements Module{

    Processor p;
    Execute nextModule;

    boolean blocked = false;

    public Commit(Processor proc) {
        p = proc; }

    @Override
    public void tick() {
        if (!p.ROBEmpty()) {
            if (p.ROB.get(p.ROBcommit).ready) {
                ROBEntry entry = p.ROB.get(p.ROBcommit);
                if (entry.WB == true) {
                    p.ARF.set(entry.destinationRegister, entry.value);
                }

                // Update RAT
                if (p.RAT.get(entry.destinationRegister) == p.ROBcommit) {
                    p.RAT.set(entry.destinationRegister, null);
                }

                if (entry.halt) {
                    p.fin = true;
                }

                p.ROB.set(p.ROBcommit, null);
                p.ROBcommit += 1;
                p.ROBcommit = p.ROBcommit % p.ROBSize;
            }
        }
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {}
}
