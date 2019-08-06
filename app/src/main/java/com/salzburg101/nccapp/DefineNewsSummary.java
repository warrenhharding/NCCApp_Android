package com.salzburg101.nccapp;

// NewsArray is used to store recordIds for alerts which have been 'read'. They are then compared to
// recordIds of existing alerts to determine which have been read and which haven't been read. This then
// determines how many 'new' or 'unread' messages are to be shown on the Alerts badge.

import java.util.ArrayList;

public class DefineNewsSummary {

    public ArrayList<StructureForRetrieveMyClassNews> newsSummary;

    private DefineNewsSummary() {
        newsSummary = new ArrayList<StructureForRetrieveMyClassNews>();
    }

    private static DefineNewsSummary instance;


    public static DefineNewsSummary getInstance() {
        if (instance == null) instance = new DefineNewsSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveMyClassNews> getArrayList() {
        return newsSummary;
    }

    public void setArraySummary(StructureForRetrieveMyClassNews newNewsItem) {
        newsSummary.add(newNewsItem);
    }

    public void remArrayList(String newNewsItem) {
        newsSummary.remove(newNewsItem);
    }

}
