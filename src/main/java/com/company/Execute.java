package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Execute implements Module {

    Processor p;

    Broadcast nextModule;

    // Execution units
    public IntegerUnit intUnit;
    public MultDivUnit multDivUnit;
    public BranchUnit branchUnit;
    public LoadStoreUnit loadStoreUnit;

    // All modules before this
    List<? extends Module> frontEnd;

    public Execute(Processor proc, Broadcast next, Memory memoryUnit) {
        p = proc;
        nextModule = next;
        intUnit = new IntegerUnit(p, nextModule, this);
        multDivUnit = new MultDivUnit(p, nextModule, this);
        branchUnit = new BranchUnit(p, nextModule, this);
        loadStoreUnit = new LoadStoreUnit(p, nextModule, memoryUnit);

        nextModule.intUnit = intUnit;
        nextModule.multDivUnit = multDivUnit;
        nextModule.branchUnit = branchUnit;
        nextModule.loadStoreUnit = loadStoreUnit;
    }

    @Override
    public boolean blocked() {
        return false;
    }

    @Override
    public boolean setNextInstruction(Instruction instruction) {
        return true;
    }

    // Invalidate instructions for all execution units
    @Override
    public void invalidateCurrentInstruction() {
        intUnit.invalidateCurrentInstruction();
        multDivUnit.invalidateCurrentInstruction();
        branchUnit.invalidateCurrentInstruction();
        loadStoreUnit.invalidateCurrentInstruction();
    }

    public void setFrontEnd(List<? extends Module> frontEndList) {
        frontEnd = frontEndList;
//        branchUnit.setFrontEnd(frontEndList);
    }

    @Override
    public void tick() {
        intUnit.tick();
        multDivUnit.tick();
        branchUnit.tick();
        loadStoreUnit.tick();
    }

    // Returns index of most depended on instruction within entries
    public int getMostDependedOn(List<RSEntry> entries){
        List<Integer> tally = new ArrayList<>();
        for (RSEntry entry : entries) {
            Integer noDepending = intUnit.RS.stream().map(RSEntry::getNonNullEntry).filter(r -> r.tag1 == entry.ROBdestination || r.tag2 == entry.ROBdestination).collect(Collectors.toList()).size();
            noDepending += branchUnit.RS.stream().map(RSEntry::getNonNullEntry).filter(r -> r.tag1 == entry.ROBdestination || r.tag2 == entry.ROBdestination).collect(Collectors.toList()).size();
            noDepending += multDivUnit.RS.stream().map(RSEntry::getNonNullEntry).filter(r -> r.tag1 == entry.ROBdestination || r.tag2 == entry.ROBdestination).collect(Collectors.toList()).size();
            noDepending += loadStoreUnit.LSQ.stream().map(LSQEntry::getNonNullEntry).filter(l -> l.tag1 == entry.ROBdestination || l.tag2 == entry.ROBdestination || l.strValTag == entry.ROBdestination).collect(Collectors.toList()).size();
            tally.add(noDepending);
        }
        return tally.indexOf(Collections.max(tally));
    }
}
