package com.company;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class Execute implements Module {

    Processor p;
    private boolean stepMode = false;

    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    List<? extends Module> frontEnd;

    public Execute(Processor proc) {
        p = proc;
        intUnit = new IntegerUnit(p);
        multDivUnit = new MultDivUnit(p);
        branchUnit = new BranchUnit(p);
        loadStoreUnit = new LoadStoreUnit(p);
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public void setNextInstruction(Instruction instruction) {}

    @Override
    public void invalidateCurrentInstruction() {}

    public void setFrontEnd(List<? extends Module> frontEndList) {
        frontEnd = frontEndList;
        branchUnit.setFrontEnd(frontEndList);
    }

    @Override
    public void tick() {

        intUnit.tick();
        multDivUnit.tick();
        branchUnit.tick();
        loadStoreUnit.tick();


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
}
