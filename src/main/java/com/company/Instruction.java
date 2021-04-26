package com.company;

public class Instruction {

    public String opcode;
    public int operand1;
    public int operand2;
    public int operand3;
    public boolean valid = true;
    public int PC;
    public boolean branchTaken = false;

    public Instruction(String opcodeVal, int operand1Val, int operand2Val, int operand3Val){
        opcode = opcodeVal;
        operand1 = operand1Val;
        operand2 = operand2Val;
        operand3 = operand3Val;
    }
    public Instruction(String opcodeVal, int operand1Val, int operand2Val, int operand3Val, boolean validVal){
        opcode = opcodeVal;
        operand1 = operand1Val;
        operand2 = operand2Val;
        operand3 = operand3Val;
        valid = validVal;
    }

    public Instruction(String opcodeVal, int operand1Val, int operand2Val, int operand3Val, int PCVal){
        opcode = opcodeVal;
        operand1 = operand1Val;
        operand2 = operand2Val;
        operand3 = operand3Val;
        PC = PCVal;
    }

    public Instruction(Instruction ins) {
        opcode = ins.opcode;
        operand1 = ins.operand1;
        operand2 = ins.operand2;
        operand3 = ins.operand3;
    }

    public Instruction(Instruction ins, int PCVal, boolean branchTakenVal) {
        opcode = ins.opcode;
        operand1 = ins.operand1;
        operand2 = ins.operand2;
        operand3 = ins.operand3;
        PC = PCVal;
        branchTaken = branchTakenVal;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode='" + opcode + '\'' +
                ", operand1=" + operand1 +
                ", operand2=" + operand2 +
                ", operand3=" + operand3 +
                ", valid=" + valid +
                ", PC=" + PC +
                '}';
    }
}
