package com.company;

public class LoadStoreUnit implements Module{

    Processor p;
    public Instruction nextInstruction = new Instruction("NOP", 0, 0,0);

    public LoadStoreUnit(Processor proc) {
        p = proc;
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
    public void setNextInstruction(Instruction instruction) {
        nextInstruction = instruction;
    }

    @Override
    public void invalidateCurrentInstruction() {
        nextInstruction.valid = false;
    }
}
