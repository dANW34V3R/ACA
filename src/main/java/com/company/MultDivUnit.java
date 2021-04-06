package com.company;

public class MultDivUnit implements Module{

    Processor p;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0, 0);

    public MultDivUnit(Processor proc) {
        p = proc;
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
    public void setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
