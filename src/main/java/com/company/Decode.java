package com.company;

public class Decode implements Module{

    Processor p;

    public Decode(Processor proc) {
        p = proc;
    }


    @Override
    public void tick() {
        p.decodeInstruction = p.fetchInstruction;
    }
}
