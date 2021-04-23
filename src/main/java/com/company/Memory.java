package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Memory implements Module{

    Processor p;
//    Instruction nextInstruction = null;
    LoadStoreUnit ldstrUnit;

    List<Instruction> queue = new ArrayList<>();

    public Memory(Processor proc) {
        p = proc;
//        ldstrUnit = ldstr;
//        nextModule = next;
    }


    @Override
    public void tick() {
        stage3Tick();
        stage2Tick();
        stage1Tick();
    }

    private void stage1Tick() {
        if (queue.size() > 0) {
            stage1EndInstruction = queue.get(0);
            queue.remove(0);
        } else {
            stage1EndInstruction = null;
        }
    }

    private Instruction stage1EndInstruction = null;

    private void stage2Tick(){stage2EndInstruction = stage1EndInstruction;}

    private Instruction stage2EndInstruction = null;

    private void stage3Tick(){
        if (stage2EndInstruction != null) {
            if (stage2EndInstruction.operand1 > -1 && stage2EndInstruction.operand1 < p.MEM.size()) {
                if (stage2EndInstruction.opcode.compareTo("load") == 0) {
                    ldstrUnit.sendLoad(p.MEM.get(stage2EndInstruction.operand1), stage2EndInstruction.operand2);
                } else if (stage2EndInstruction.opcode.compareTo("store") == 0) {
                    p.MEM.set(stage2EndInstruction.operand1, stage2EndInstruction.operand3);
                } else {
                    throw new Error("Unknown instruction in memory stage " + stage2EndInstruction.opcode);
                }
            } else {
                System.out.println("Memory out of bounds error");
            }
        }
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        queue.add(instruction);
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {
    }

    public void printState() {
        String s1 = stage1EndInstruction != null ? stage1EndInstruction.toString() : "NULL";
        String s2 = stage2EndInstruction != null ? stage2EndInstruction.toString() : "NULL";
        System.out.println(s1 + "+" + s2);
    }
}
