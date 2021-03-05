package com.company;

public class Decode implements Module{

    Processor p;

    public Decode(Processor proc) {
        p = proc;
    }


    @Override
    public void tick() {
        if (!blocked()) {
            p.decodeInstruction = p.fetchInstruction;
        }
    }

    @Override
    public boolean blocked() {
        return p.insE.blocked();
    }
}
