package com.company;

public class Instruction {

    public String opcode;
    public int operand1;
    public int operand2;
    public int operand3;

    public Instruction(String opcodeVal, int operand1Val, int operand2Val, int operand3Val){
        opcode = opcodeVal;
        operand1 = operand1Val;
        operand2 = operand2Val;
        operand3 = operand3Val;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode='" + opcode + '\'' +
                ", operand1=" + operand1 +
                ", operand2=" + operand2 +
                ", operand3=" + operand3 +
                '}';
    }
}
