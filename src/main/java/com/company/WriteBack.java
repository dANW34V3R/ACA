package com.company;

public class WriteBack implements Module{

    Processor p;
    private Instruction todoInstruction = null;
    public Instruction nextInstruction = null;

    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    public WriteBack(Processor proc) {
        p = proc;
//        nextModule = next;
    }

    @Override
    public void tick() {
        if (nextInstruction != null) {
            p.ROB.get(nextInstruction.operand1).value = nextInstruction.operand2;
            p.ROB.get(nextInstruction.operand1).ready = true;

            if (p.ROB.get(nextInstruction.operand1).WB == true) {
                // Broadcast value
                intUnit.updateRS(nextInstruction.operand1, nextInstruction.operand2);
            }
        }
        nextInstruction = todoInstruction;
        todoInstruction = null;
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        todoInstruction = instruction;
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }

}
