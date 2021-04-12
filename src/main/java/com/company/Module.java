package com.company;

public interface Module {

    void tick();

    boolean blocked();

    boolean setNextInstruction(Instruction instruction);

    void invalidateCurrentInstruction();

}
