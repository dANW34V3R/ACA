package com.company;

public class Execute implements Module {

    Processor p;
    int cyclesToGo = 0;

    public Execute(Processor proc) {
        p = proc;
    }

    @Override
    public boolean blocked() {
        return cyclesToGo > 0;
    }

    private void invalidatePipeline() {
        p.fetchInstruction.valid = false;
//        p.decodeInstruction.valid = false;
    }

    @Override
    public void tick() {
        cyclesToGo -= 1;

        Instruction instruction = p.decodeInstruction;
        p.executeInstruction = instruction;
//        System.out.println("EX:" + instruction.toString() + instruction.valid);

        if (instruction.valid) {
            switch (instruction.opcode) {
                case "MOVi":
                    p.ARF.set(instruction.operand1, instruction.operand2);
                    break;
                case "MOV":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2));
                    break;
                case "ADDi":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand1) + instruction.operand2);
                    break;
                case "ADD":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3));
                    break;
                case "SUBi":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand1) - instruction.operand2);
                    break;
                case "SUB":
                    p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) - p.ARF.get(instruction.operand3));
                    break;
                case "MUL":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) * p.ARF.get(instruction.operand3));
                    }
                    break;
                case "DIV":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.ARF.set(instruction.operand1, p.ARF.get(instruction.operand2) / p.ARF.get(instruction.operand3));
                    }
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
                    break;
                case "BEQ":
                    if (p.f == 0) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    break;
                case "BNE":
                    if (p.f != 0) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    break;
                case "BLT":
                    if (p.f == -1) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    break;
                case "BGT":
                    if (p.f == 1) {
                        p.ARF.set(30, instruction.operand1 - 1);
                        invalidatePipeline();
                    }
                    break;
                case "B":
                    p.ARF.set(30, instruction.operand1 - 1);
                    invalidatePipeline();
                    break;
                case "LDRi":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.ARF.set(instruction.operand1, p.MEM.get(p.ARF.get(instruction.operand2) + instruction.operand3));
                    }
                    break;
                case "LDR":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.ARF.set(instruction.operand1, p.MEM.get(p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3)));
                    }
                    break;
                case "STRi":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.MEM.set(p.ARF.get(instruction.operand2) + instruction.operand3, p.ARF.get(instruction.operand1));
                    }
                    break;
                case "STR":
                    if (cyclesToGo < 0) {
                        cyclesToGo = 3;
                    } else if (cyclesToGo == 0) {
                        p.MEM.set(p.ARF.get(instruction.operand2) + p.ARF.get(instruction.operand3), p.ARF.get(instruction.operand1));
                    }
                    break;
                case "NOP":
                    break;
                case "HALT":
                    p.fin = true;
                    break;
                default:
                    System.out.println("opcode " + instruction.opcode + " not recognised");
                    break;
            }
        }
    }
}
