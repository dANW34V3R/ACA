package com.company;

public class Commit implements Module{

    Processor p;
    Execute nextModule;

    boolean blocked = false;

    public Commit(Processor proc) {
        p = proc;
    }

    @Override
    public void tick() {
        if (!p.ROBEmpty()) {
            if (p.ROB.get(p.ROBcommit).ready) {
                ROBEntry entry = p.ROB.get(p.ROBcommit);
                if (entry.type == 0) {
//                    System.out.println("commit branch");
                    p.noBranches++;
                    p.BP.updateBTB(entry.instructionPC, entry.value + 1, entry.branchExecuteTaken);
                    if (entry.branchExecuteTaken != entry.branchFetchTaken) {
                        p.noMispredicts++;
                        if (entry.branchFetchTaken) {
                            p.ARF.set(30, entry.instructionPC);
                        } else {
                            p.ARF.set(30, entry.value);
                        }
                        p.clearPipelineAndReset();
                    }
                }

                if (entry.type == 2 || entry.type == 1) {
                    p.ARF.set(entry.destinationRegister, entry.value);

                    // Update RAT
                    if (p.RAT.get(entry.destinationRegister) == p.ROBcommit) {
                        p.RAT.set(entry.destinationRegister, null);
                    }
                }

                if (entry.type == 3) {
                    p.f = entry.value;
                }
                if (entry.type == 4) {
                    p.fin = true;
                    // allow all store instructions to update memory
                    p.noInstructions += 2;
                    p.insMEM.tick();
                    p.insMEM.tick();
//                    p.insMEM.tick();
                }

                if (entry.type == 5) {
                    p.insE.loadStoreUnit.sendStoreToMem(p.ROBcommit);
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
