package com.company;

import java.util.*;

public class Processor {

    public List<Instruction> INSMEM;
    public List<Integer> MEM = new ArrayList<>(Collections.nCopies(1024, 0));

    public List<Integer> ARF = new ArrayList<>(Collections.nCopies(32, -1));

    public boolean fin = false;

    //flags
    public int f;


    private Fetch insF = new Fetch(this);
    private Decode insD = new Decode(this);
    private Execute insE = new Execute(this);

    public Instruction fetchInstruction;
    public Instruction decodeInstruction;
    public Instruction executeInstruction;


    private int tick = 0;
    private int cycles = 0;

    public Processor(List<Instruction> instructions, List<Integer> memory) {
        INSMEM = instructions;
        for (int i = 0; i < memory.size(); i++ ) {
            MEM.set(i, memory.get(i));
        }
        System.out.println(MEM.toString());
        System.out.println(ARF.toString());
        go();
    }

    private void go() {

        while (!fin) {
            insF.tick();
            insD.tick();
            insE.tick();
            cycles += 3;
            System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
            System.out.println(MEM.toString());
        }

        System.out.println("process finished");

    }








}
