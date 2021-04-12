package com.company;

import java.util.ArrayList;
import java.util.List;

public class MultDivUnit implements Module{

    Processor p;
    Module nextModule;

    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    int RSsize = 4;
    List<RSEntry> RS = new ArrayList<>();

    public MultDivUnit(Processor proc, Module next){
        p = proc;
        nextModule = next;
    }

    @Override
    public void tick() {
        if (nextInstruction.valid) {
            Instruction instruction = nextInstruction;
            switch (instruction.opcode) {
                case "MUL":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) * p.ARF.get(instruction.operand3));
                    p.noInstructions += 1;
                    break;
                case "DIV":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) / p.ARF.get(instruction.operand3));
                    p.noInstructions += 1;
                    break;
                case "NOP":
                    break;
                default:
                    System.out.println("opcode " + instruction.opcode + " not recognised in MultDivUnit");
                    break;
            }
        }

//        stage3Tick();
//        stage2Tick();
//        stage1Tick();
    }

//    private Instruction stage0EndInstruction = new Instruction("NOP", 0,0,0);
//
//    private void stage1Tick(){stage1EndInstruction = stage0EndInstruction;}
//
//    private Instruction stage1EndInstruction = new Instruction("NOP", 0,0,0);
//
//    private void stage2Tick(){stage2EndInstruction = stage1EndInstruction;}
//
//    private Instruction stage2EndInstruction = new Instruction("NOP", 0,0,0);
//
//    private void stage3Tick(){
////        Instruction instruction = stage2EndInstruction;
////        switch (instruction.opcode) {
////            case "MUL":
////                p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) * p.ARF.get(instruction.operand3));
////                p.noInstructions += 1;
////                break;
////            case "DIV":
////                p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) / p.ARF.get(instruction.operand3));
////                p.noInstructions += 1;
////                break;
////            default:
////                System.out.println("opcode " + instruction.opcode + " not recognised in MultDivUnit");
////                break;
////        }
//    }

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
