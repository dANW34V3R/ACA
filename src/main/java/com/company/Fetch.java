package com.company;

public class Fetch implements Module{

    Processor p;

    public Fetch(Processor proc) {
        p = proc;
    }


    @Override
    public void tick() {
        p.PC++;
    }
}
