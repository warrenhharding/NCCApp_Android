package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineEventSummary {

    public ArrayList<StructureForRetrieveAllEvents> eventSummary;

    private DefineEventSummary() {
        eventSummary = new ArrayList<StructureForRetrieveAllEvents>();
    }

    private static DefineEventSummary instance;


    public static DefineEventSummary getInstance() {
        if (instance == null) instance = new DefineEventSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveAllEvents> getArrayList() {
        return eventSummary;
    }

    public void setArraySummary(StructureForRetrieveAllEvents newEventItem) {
        eventSummary.add(newEventItem);
    }

    public void remArrayList(String newEventItem) {
        eventSummary.remove(newEventItem);
    }

}
