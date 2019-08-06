package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefinePANewsSummary {

    public ArrayList<StructureForRetrieveAllPANews> paNewsSummary;

    private DefinePANewsSummary() { paNewsSummary = new ArrayList<StructureForRetrieveAllPANews>(); }

    private static DefinePANewsSummary instance;


    public static DefinePANewsSummary getInstance() {
        if (instance == null) instance = new DefinePANewsSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveAllPANews> getArrayList() {
        return paNewsSummary;
    }

    public void setArraySummary(StructureForRetrieveAllPANews newPaNewsItem) {
        paNewsSummary.add(newPaNewsItem);
    }

    public void remArrayList(String newPaNewsItem) {
        paNewsSummary.remove(newPaNewsItem);
    }

}
