package com.company;

import java.util.ArrayList;
import java.util.List;

public class LoadStoreUnit implements Module{

    Processor p;
    Module nextModule;

    public Instruction nextInstruction = new Instruction("NOP", 0, 0,0);

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public LoadStoreUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }
    @Override
    public void tick() {
        if (nextInstruction.valid) {
            Instruction instruction = nextInstruction;
            switch (instruction.opcode) {
                case "NOP":
                    break;
                case "LDRi":
                    p.ARF.set(instruction.operand1, p.MEM.get(p.ARF.get(instruction.operand2) + instruction.operand3));
                    p.noInstructions += 1;
                    break;
                case "LDR":
                    p.ARF.set(instruction.operand1, p.MEM.get(p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3)));
                    p.noInstructions += 1;
                    break;
                case "STRi":
                    p.MEM.set(p.ARF.get(instruction.operand2) + instruction.operand3, p.ARF.get(instruction.operand1));
                    p.noInstructions += 1;
                    break;
                case "STR":
                    p.MEM.set(p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3), p.ARF.get(instruction.operand1));
                    p.noInstructions += 1;
                    break;
                default:
                    System.out.println("opcode " + instruction.opcode + " not recognised");
                    break;
            }
        }
    }

    @Override
    public boolean blocked() {
        return false;
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
