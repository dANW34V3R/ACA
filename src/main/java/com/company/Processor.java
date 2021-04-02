package com.company;

import sun.awt.X11.XSystemTrayPeer;

import java.util.*;

public class Processor {

    public List<Instruction> INSMEM;
    public List<Integer> MEM = new ArrayList<>(Collections.nCopies(1024, 0));

    public List<Integer> ARF = new ArrayList<>(Collections.nCopies(32, -1));

    public boolean fin = false;

    //flags
    public int f;

    // Modules
    public Fetch insF = new Fetch(this);
    public Decode insD = new Decode(this);
    public Execute insE = new Execute(this);

    public Instruction fetchInstruction = new Instruction("NOP",0,0,0);
    public Instruction decodeInstruction = new Instruction("NOP",0,0,0);
    public Instruction executeInstruction = new Instruction("NOP",0,0,0);;

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
            insE.tick();
            insD.tick();
            insF.tick();
            cycles += 3;
            System.out.println("FE:" + fetchInstruction.toString());
            System.out.println("DE:" + decodeInstruction.toString());
            System.out.println("EX:" + executeInstruction.toString());
            System.out.println("Execution unit blocked: " + insE.blocked());
            System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
            System.out.println(MEM.toString());
            System.out.println("__________________________________");
        }

        System.out.println("process finished");

    }








}
