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
    public Execute insE = new Execute(this);
    public Issue insI =  new Issue(this, insE);
    public Decode insD = new Decode(this, insI);
    public Fetch insF = new Fetch(this, insD);


//    public Instruction fetchInstruction = new Instruction("NOP",0,0,0);
//    public Instruction decodeInstruction = new Instruction("NOP",0,0,0);
//    public Instruction executeInstruction = new Instruction("NOP",0,0,0);;

    private int tick = 0;
    private int cycles = 0;
    public int noInstructions = 0;

    public Processor(List<Instruction> instructions, List<Integer> memory) {
        INSMEM = instructions;
        for (int i = 0; i < memory.size(); i++ ) {
            MEM.set(i, memory.get(i));
        }
        insE.setFrontEnd(Arrays.asList(insF, insD, insI));
        System.out.println(MEM.toString());
        System.out.println(ARF.toString());
        go();
    }

    private void go() {

        while (!fin) {
            insE.tick();
            insI.tick();
            insD.tick();
            insF.tick();
            cycles += 1;
            System.out.println("FE:" + insD.nextInstruction.toString());
            System.out.println("DE:" + insI.nextInstruction.toString());
            System.out.println("IS:" + insE.nextInstruction.toString());
//            System.out.println("EX:" + executeInstruction.toString());
            System.out.println("Execution unit blocked: " + insE.blocked());
            System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
            System.out.println(MEM.toString());
            System.out.println("__________________________________");
        }

        System.out.println("process finished");
        System.out.println("No. cycles: " + cycles);
        System.out.println("No. instructions: " + noInstructions);
        System.out.println("Instructions/Cycle: " + (float) noInstructions/(float) cycles);

    }








}
