package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Decode implements Module{

    Processor p;
    Module nextModule;
    public List<Instruction> nextInstructionList = new ArrayList<>();
    int width = 4;

    public Decode(Processor proc, Module next) {
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {
        if (nextInstructionList.size() > width) {
            throw new java.lang.Error("Decode contains more instructions that width");
        }
        List<Instruction> movedOn = new ArrayList<>();
        for (Instruction nIns : nextInstructionList) {
            if (!nextModule.blocked()) {
                nextModule.setNextInstruction(nIns);
                movedOn.add(nIns);
            }
        }
        nextInstructionList.removeAll(movedOn);
    }

    @Override
    public boolean blocked() {
        return nextInstructionList.size() >= width;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        nextInstructionList.add(instruction);
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
        for (Instruction nIns : nextInstructionList) {
            nIns.valid = false;
        }
    }
}
