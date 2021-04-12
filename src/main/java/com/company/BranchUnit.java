package com.company;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.List;

public class BranchUnit implements Module{

    Processor p;
    Module nextModule;

    List<? extends Module> frontEnd;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0,0);

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public BranchUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }

    public void setFrontEnd(List<? extends Module> frontEndList) {
        frontEnd = frontEndList;
    }

    private void invalidatePipeline() {
        for (Module module : frontEnd) {
            module.invalidateCurrentInstruction();
        }
    }

    @Override
    public void tick() {

        Instruction instruction = nextInstruction;

        if (instruction.valid) {
            switch (instruction.opcode) {
                case "NOP":
                    break;
                case "CMP":
                    //op1 - op2 , update flags
                    int result = p.ARF.get(instruction.operand1) - p.ARF.get(instruction.operand2);
                    if (result == 0) {
                        p.f = 0;
                    } else if (result < 0) {
                        p.f = -1;
                    } else {
                        p.f = 1;
                    }
                    p.noInstructions += 1;
                    break;
                case "BEQ":
                    if (p.f == 0) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    p.noInstructions += 1;
                    break;
                case "BNE":
                    if (p.f != 0) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    p.noInstructions += 1;
                    break;
                case "BLT":
                    if (p.f == -1) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    p.noInstructions += 1;
                    break;
                case "BGT":
                    if (p.f == 1) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    p.noInstructions += 1;
                    break;
                case "B":
                    p.ARF.set(30, instruction.operand1 - 1);
                    invalidatePipeline();
                    p.noInstructions += 1;
                    break;
                case "BR":
                    p.ARF.set(30, p.ARF.get(instruction.operand1));
                    invalidatePipeline();
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
