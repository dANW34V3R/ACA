package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Broadcast implements Module{

    Processor p;
    public Optional<Instruction> nextInstruction = Optional.empty();

    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    private int width = 4;

    // TODO limit this size
    public List<Instruction> WBqueue = new ArrayList<>();

    public Broadcast(Processor proc) {
        p = proc;
    }

    @Override
    public void tick() {
        for (int i = 0; i < width; i++) {
            if (nextInstruction.isPresent()) {
                Instruction nextInstructionValue = nextInstruction.get();

                // Update ROB
                p.ROB.get(nextInstructionValue.operand1).value = nextInstructionValue.operand2;
                p.ROB.get(nextInstructionValue.operand1).ready = true;

                // If register update or CMP
                if ( p.ROB.get(nextInstructionValue.operand1).type == 1 || p.ROB.get(nextInstructionValue.operand1).type == 2 || p.ROB.get(nextInstructionValue.operand1).type == 3) {
                    // Broadcast value to all execution units
                    intUnit.updateRS(nextInstructionValue.operand1, nextInstructionValue.operand2);
                    multDivUnit.updateRS(nextInstructionValue.operand1, nextInstructionValue.operand2);
                    branchUnit.updateRS(nextInstructionValue.operand1, nextInstructionValue.operand2);
                    loadStoreUnit.updateLSQ(nextInstructionValue.operand1, nextInstructionValue.operand2);
                }
                WBqueue.remove(0);
            }
            nextInstruction = WBqueue.stream().findFirst();
        }
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        WBqueue.add(instruction);
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction = Optional.empty();
        WBqueue.clear();
    }

}
