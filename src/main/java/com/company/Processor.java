package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

public class Processor {

    // Memory
    public List<Instruction> INSMEM;
    public List<Integer> MEM = new ArrayList<>(Collections.nCopies(4096, 0));

    // Registers and Register Alias Table
    public List<Integer> ARF = new ArrayList<>(Collections.nCopies(32, -1));
    public List<Integer> RAT = new ArrayList<>(Collections.nCopies(32, null));

    // Reorder Buffer
    public int ROBSize = 64;
    public int ROBissue = 0;
    public int ROBcommit = 0;
    public List<ROBEntry> ROB = new ArrayList<>(Collections.nCopies(ROBSize, null));

    // Finish flag
    public boolean fin = false;

    // Compare flag
    public int f;

    // Modules
    public Commit insC = new Commit(this);
    public Broadcast insWB = new Broadcast(this);
    public Memory insMEM = new Memory(this);
    public Execute insE = new Execute(this, insWB, insMEM);
    public Issue insI =  new Issue(this, insE);
    public Decode insD = new Decode(this, insI);
    public Fetch insF = new Fetch(this, insD);
    public BranchPredictor BP = new BranchPredictor();

    // Data
    private int cycles = 0;
    public int noInstructions = 0;
    public int noBranches = 0;
    public int noMispredicts = 0;

    // Step mode. On when true
    private boolean stepMode = false;

    // Processor constructor
    public Processor(List<Instruction> instructions, List<Integer> memory) {

        // Populate memories
        INSMEM = instructions;
        for (int i = 0; i < memory.size(); i++ ) {
            MEM.set(i, memory.get(i));
        }

        // Finish module setup
        insE.setFrontEnd(Arrays.asList(insF, insD, insI, insE));
        insMEM.ldstrUnit = insE.loadStoreUnit;

        // Print initial processor state
        System.out.println(MEM.toString());
        System.out.println(ARF.toString());
        System.out.println(ROBcommit + ":" + ROBissue + ":" + ROB.toString());
        System.out.println("IS:" + insE.intUnit.RS.toString());

        // Run the program
        go();
    }

    private void go() {

        while (!fin) {
            // Tick all modules, back to front to allow for pipelining
            insC.tick();
            insMEM.tick();
            insE.tick();
            insI.tick();
            insD.tick();
            insF.tick();
            // WB ticks last to allow instructions to be issued before values are broadcast
            insWB.tick();
            cycles += 1;
            // Print state of processor
//            System.out.println("BTB: " + BP.BTB.toString());
//            System.out.println("FE:" + insD.nextInstructionList.toString() + insF.blocked());
//            System.out.println("DE:" + insI.nextInstructionList.toString() + insD.blocked());
////            System.out.println("ISBlocked:" + insI.nextInstructionList.size() + insI.blocked + insI.blocked());
//            System.out.println("ISint:" + insE.intUnit.RS.toString() + (insE.intUnit.RS.size() >= insE.intUnit.RSsize));
//            System.out.println("ISmult:" + insE.multDivUnit.RS.toString() + (insE.multDivUnit.RS.size() >= insE.multDivUnit.RSsize));
//            System.out.println("ISbranch:" + insE.branchUnit.RS.toString() + (insE.branchUnit.RS.size() >= insE.branchUnit.RSsize));
//            System.out.println("LSQ:" + insE.loadStoreUnit.LSQ.toString() + (insE.loadStoreUnit.LSQ.size() >= insE.loadStoreUnit.LSQsize));
//            insE.multDivUnit.printState();
//            System.out.println("WBqueue:" + insWB.WBqueue.toString());
//            System.out.println("MEMqueue:" + insMEM.queue.toString());
//            insMEM.printState();
////            System.out.println("Execution unit blocked: " + insE.blocked());
//            System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
//            System.out.println(ROBcommit + ":" + ROBissue + ":" + ROB.toString());
////            System.out.println(MEM.toString());
//            System.out.println("__________________________________");
////            System.out.println(noInstructions);

            // Step mode
            if (stepMode && cycles % 100 == 0 && cycles > 20000) {
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

        // Print final state of the processor
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
        System.out.println("Execution unit blocked: " + insE.blocked());
        System.out.println(ARF.toString() + "flag=" + f + ",PC=" + ARF.get(30));
        System.out.println(ROBcommit + ":" + ROBissue + ":" + ROB.toString());
        System.out.println(MEM.toString());
        System.out.println("__________________________________");

        // Print statistics
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

    // Add a ROB entry to ROB and returns it's address
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

    // Flushes the pipeline
    public void clearPipelineAndReset(){
        insF.invalidateCurrentInstruction();
        insD.invalidateCurrentInstruction();
        insI.invalidateCurrentInstruction();
        insE.invalidateCurrentInstruction();
//        insMEM.invalidateCurrentInstruction();
        insWB.invalidateCurrentInstruction();
        insC.invalidateCurrentInstruction();

        BP.setPipelineFlush();

        // Clear RAT and ROB
        RAT = new ArrayList<>(Collections.nCopies(32, null));
        ROBissue = (ROBcommit + 1) % ROB.size();
    }
}
