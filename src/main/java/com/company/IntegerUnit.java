package com.company;

public class IntegerUnit implements Module{

    Processor p;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0,0);

    public IntegerUnit(Processor proc){
        p = proc;
    }

    @Override
    public void tick() {

        Instruction instruction = nextInstruction;

        if (instruction.valid) {
            switch (instruction.opcode) {
                case "MOVi":
                    p.ARF.set(instruction.operand1, instruction.operand2);
                    p.noInstructions += 1;
                    break;
                case "MOV":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2));
                    p.noInstructions += 1;
                    break;
                case "MOVPC":
                    p.ARF.set(instruction.operand1, instruction.PC);
                    p.noInstructions += 1;
                    break;
                case "ADDi":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand1) + instruction.operand2);
                    p.noInstructions += 1;
                    break;
                case "ADD":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3));
                    p.noInstructions += 1;
                    break;
                case "SUBi":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand1) - instruction.operand2);
                    p.noInstructions += 1;
                    break;
                case "SUB":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) - p.ARF.get(instruction.operand3));
                    p.noInstructions += 1;
                    break;
                case "NOP":
                    break;
                case "HALT":
                    p.fin = true;
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
    public void setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
