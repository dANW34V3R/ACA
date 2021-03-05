package com.company;

public class Fetch implements Module{

    Processor p;

    public Fetch(Processor proc) {
        p = proc;
    }


    @Override
    public void tick() {
        p.ARF.set(30, p.ARF.get(30) + 1);
        p.fetchInstruction = p.INSMEM.get(p.ARF.get(30));
    }
}
