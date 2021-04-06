package com.company;

public interface Module {

    void tick();

    boolean blocked();

    void setNextInstruction(Instruction instruction);

    void invalidateCurrentInstruction();

}
