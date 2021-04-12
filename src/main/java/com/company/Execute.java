package com.company;

import java.util.List;

public class Execute implements Module {

    Processor p;

    WriteBack nextModule;

    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    List<? extends Module> frontEnd;

    public Execute(Processor proc, WriteBack next) {
        p = proc;
        nextModule = next;
        intUnit = new IntegerUnit(p, nextModule);
        multDivUnit = new MultDivUnit(p, nextModule);
//        branchUnit = new BranchUnit(p, nextModule);
//        loadStoreUnit = new LoadStoreUnit(p, nextModule);

        nextModule.intUnit = intUnit;
        nextModule.multDivUnit = multDivUnit;
//        nextModule.branchUnit = branchUnit;
//        nextModule.loadStoreUnit = loadStoreUnit;
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        return true;
    }

    @Override
    public void invalidateCurrentInstruction() {}

    public void setFrontEnd(List<? extends Module> frontEndList) {
        frontEnd = frontEndList;
//        branchUnit.setFrontEnd(frontEndList);
    }

    @Override
    public void tick() {
        intUnit.tick();
        multDivUnit.tick();
//        branchUnit.tick();
//        loadStoreUnit.tick();
    }
}
