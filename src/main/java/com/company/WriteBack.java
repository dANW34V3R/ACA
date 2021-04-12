package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WriteBack implements Module{

    Processor p;
    public Optional<Instruction> nextInstruction = Optional.empty();

    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    public List<Instruction> WBqueue = new ArrayList<>();

    public WriteBack(Processor proc) {
        p = proc;
//        nextModule = next;
    }

    @Override
    public void tick() {
        if (nextInstruction.isPresent()) {
            Instruction nextInstructionValue = nextInstruction.get();
            System.out.println(nextInstructionValue.toString());
            p.ROB.get(nextInstructionValue.operand1).value = nextInstructionValue.operand2;
            p.ROB.get(nextInstructionValue.operand1).ready = true;

            if (p.ROB.get(nextInstructionValue.operand1).WB == true) {
                // Broadcast value
                intUnit.updateRS(nextInstructionValue.operand1, nextInstructionValue.operand2);
            }
            WBqueue.remove(0);
        }
        System.out.println(WBqueue.toString());
        nextInstruction = WBqueue.stream().findFirst();
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        System.out.println("WB SET NEXT" + instruction.toString());
        WBqueue.add(instruction);
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        // TODO invalidate all instructions
    }

}
