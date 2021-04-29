package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Memory implements Module{

    Processor p;
    LoadStoreUnit ldstrUnit;

    int width = 4;

    List<Instruction> queue = new ArrayList<>();

    public Memory(Processor proc) {
        p = proc;
    }


    // Tick sub-pipeline
    @Override
    public void tick() {
        // TODO this is not correct
        stage3Tick();
        stage2Tick();
        stage1Tick();
    }


    private void stage1Tick() {
        for (int k = 0; k < width; k++) {
            if (queue.size() > 0) {
                // Oldest instruction first
                stage1EndInstructionList.add(queue.get(0));
                queue.remove(0);
            }
        }
    }

    private List<Instruction> stage1EndInstructionList = new ArrayList<>();

    private void stage2Tick(){
        stage2EndInstructionList = new ArrayList<>(stage1EndInstructionList);
        stage1EndInstructionList = new ArrayList<>();
    }

    private List<Instruction> stage2EndInstructionList = new ArrayList<>();

    // Perform operations
    private void stage3Tick(){
        for (Instruction stage2EndInstruction : stage2EndInstructionList) {
//            if (stage2EndInstruction != null) {
                // Check instruction is within bounds of memory
                if (stage2EndInstruction.operand1 > -1 && stage2EndInstruction.operand1 < p.MEM.size()) {
                    if (stage2EndInstruction.opcode.compareTo("load") == 0) {
                        // Send loaded value back to load/store unit
                        ldstrUnit.sendLoad(p.MEM.get(stage2EndInstruction.operand1), stage2EndInstruction.operand2);
                    } else if (stage2EndInstruction.opcode.compareTo("store") == 0) {
                        // Update memory
                        p.MEM.set(stage2EndInstruction.operand1, stage2EndInstruction.operand3);
                    } else {
                        throw new Error("Unknown instruction in memory stage " + stage2EndInstruction.opcode);
                    }
                } else {
                    System.out.println("Memory out of bounds error" + p.noInstructions);
                }
//            }
        }
        stage2EndInstructionList = new ArrayList<>();
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
    public void invalidateCurrentInstruction() {}

    public void printState() {
        String s1 = stage1EndInstructionList.toString();
        String s2 = stage2EndInstructionList.toString();
        System.out.println("Memory pipeline:" + s1 + "+" + s2);
    }
}
