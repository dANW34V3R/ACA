package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

public class Processor {

    public List<Instruction> INSMEM;
    public List<Integer> MEM = new ArrayList<>(Collections.nCopies(1024, 0));

    public List<Integer> ARF = new ArrayList<>(Collections.nCopies(32, -1));
    public List<Integer> RAT = new ArrayList<>(Collections.nCopies(32, null));

    public int ROBSize = 1024;
    public int ROBissue = 0;
    public int ROBcommit = 0;
    public List<ROBEntry> ROB = new ArrayList<>(Collections.nCopies(ROBSize, null));


    public boolean fin = false;

    //flags
    public int f;

    // Modules
    public Commit insC = new Commit(this);
    public WriteBack insWB = new WriteBack(this);
    public Memory insMEM = new Memory(this);
    public Execute insE = new Execute(this, insWB, insMEM);
    public Issue insI =  new Issue(this, insE);
    public Decode insD = new Decode(this, insI);
    public Fetch insF = new Fetch(this, insD);
    public BranchPredictor BP = new BranchPredictor();


//    public Instruction fetchInstruction = new Instruction("NOP",0,0,0);
//    public Instruction decodeInstruction = new Instruction("NOP",0,0,0);
//    public Instruction executeInstruction = new Instruction("NOP",0,0,0);;

    private int tick = 0;
    private int cycles = 0;
    public int noInstructions = 0;
    public int noBranches = 0;
    public int noMispredicts = 0;

    private boolean stepMode = false;

    public Processor(List<Instruction> instructions, List<Integer> memory) {
//        ARF.set(1, -23);
//        ARF.set(2, 16);
//        ARF.set(3, 45);
//        ARF.set(4, 5);
//        ARF.set(5, 3);
//        ARF.set(6, 4);
//        ARF.set(7, 1);
//        ARF.set(8, 2);
//        ARF.set(9, 1);

//        MOV R1 #-23
//        MOV R2 #16
//        MOV R3 #45
//        MOV R4 #5
//        MOV R5 #3
//        MOV R6 #4
//        MOV R7 #1
//        MOV R8 #2
//
//        DIV R2 R3 R4
//        MUL R1 R5 R6
//
//        MUL R1 R1 R3
//        SUB R4 R1 R5


        INSMEM = instructions;
        for (int i = 0; i < memory.size(); i++ ) {
            MEM.set(i, memory.get(i));
        }
        insE.setFrontEnd(Arrays.asList(insF, insD, insI, insE));
        insMEM.ldstrUnit = insE.loadStoreUnit;
        System.out.println(MEM.toString());
        System.out.println(ARF.toString());
        System.out.println(ROBcommit + ":" + ROBissue + ":" + ROB.toString());
        System.out.println("IS:" + insE.intUnit.RS.toString());

        go();
    }

    private void go() {

        while (!fin) {
            insC.tick();
            insMEM.tick();
            insE.tick();
            insI.tick();
            insD.tick();
            insF.tick();
            // WB ticks last to allow instructions to be issued before values are broadcast
            insWB.tick();
            cycles += 1;
            System.out.println("FE:" + insD.nextInstructionList.toString() + insF.blocked());
            System.out.println("DE:" + insI.nextInstructionList.toString() + insD.blocked());
            System.out.println("ISBlocked:" + insI.nextInstructionList.size() + insI.blocked + insI.blocked());
            System.out.println("ISint:" + insE.intUnit.RS.toString() + (insE.intUnit.RS.size() >= insE.intUnit.RSsize));
            System.out.println("ISmult:" + insE.multDivUnit.RS.toString() + (insE.multDivUnit.RS.size() >= insE.multDivUnit.RSsize));
            System.out.println("ISbranch:" + insE.branchUnit.RS.toString() + (insE.branchUnit.RS.size() >= insE.branchUnit.RSsize));
            System.out.println("LSQ:" + insE.loadStoreUnit.LSQ.toString() + (insE.loadStoreUnit.LSQ.size() >= insE.loadStoreUnit.LSQsize));
            insE.multDivUnit.printState();
            System.out.println("WBqueue:" + insWB.WBqueue.toString());
            System.out.println("MEMqueue:" + insMEM.queue.toString());
            insMEM.printState();
//            System.out.println("EX:" + executeInstruction.toString());
            System.out.println("Execution unit blocked: " + insE.blocked());
            System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
            System.out.println(ROBcommit + ":" + ROBissue + ":" + ROB.toString());
            System.out.println(MEM.toString());
            System.out.println("__________________________________");

            if (stepMode) {
                try {
                    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
                    byte dataBytes[] = keyboard.readLine().getBytes(Charset.forName("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("process finished");
        System.out.println("No. cycles: " + cycles);
        System.out.println("No. instructions: " + noInstructions);
        System.out.println("Instructions/Cycle: " + (float) noInstructions/(float) cycles);
        System.out.println("Branches: " + noBranches + ", Mispredicts: " + noMispredicts);
        System.out.println("Mispredict rate: " + (float) noMispredicts/(float) noBranches);

    }


    public boolean ROBFull() {
        return ROB.stream().noneMatch(Objects::isNull);
    }

    public boolean ROBEmpty() {
        return ROBissue == ROBcommit;
    }

    public int addROB(ROBEntry robEntry){
        if (ROBFull()) {
            throw new java.lang.Error("Attempting to add entry to full ROB");
        }
        // Finds first empty slot
        int tempROBissue = ROBissue;
        ROB.set(ROBissue, robEntry);
        ROBissue += 1;
        ROBissue = ROBissue % ROBSize;
        return tempROBissue;
    }

    public void clearPipelineAndReset(){
        insF.invalidateCurrentInstruction();
        insD.invalidateCurrentInstruction();
        insI.invalidateCurrentInstruction();
        insE.invalidateCurrentInstruction();
        insMEM.invalidateCurrentInstruction();
        insWB.invalidateCurrentInstruction();
        insC.invalidateCurrentInstruction();

        BP.setPipelineFlush();

        RAT = new ArrayList<>(Collections.nCopies(32, null));
        ROBissue = (ROBcommit + 1) % ROB.size();
    }
}
