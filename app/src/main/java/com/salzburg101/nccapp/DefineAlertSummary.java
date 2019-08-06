package com.salzburg101.nccapp;

import java.util.ArrayList;

// AlertsArray is used to store recordIds for alerts which have been 'read'. They are then compared to
// recordIds of existing alerts to determine which have been read and which haven't been read. This then
// determines how many 'new' or 'unread' messages are to be shown on the Alerts badge.

public class DefineAlertSummary {

    public ArrayList<StructureForRetrieveAllAlerts> alertsSummary;

    private DefineAlertSummary() { alertsSummary = new ArrayList<StructureForRetrieveAllAlerts>(); }

    private static DefineAlertSummary instance;


    public static DefineAlertSummary getInstance() {
        if (instance == null) instance = new DefineAlertSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveAllAlerts> getArrayList() {
        return alertsSummary;
    }

    public void setArraySummary(StructureForRetrieveAllAlerts newAlertItem) {
        alertsSummary.add(newAlertItem);
    }

    public void remArrayList(String newAlertItem) {
        alertsSummary.remove(newAlertItem);
    }

}
