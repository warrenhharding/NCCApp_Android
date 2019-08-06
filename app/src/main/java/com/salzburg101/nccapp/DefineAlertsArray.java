package com.salzburg101.nccapp;

import java.util.ArrayList;

// AlertsArray is used to store recordIds for alerts which have been 'read'. They are then compared to
// recordIds of existing alerts to determine which have been read and which haven't been read. This then
// determines how many 'new' or 'unread' messages are to be shown on the Alerts badge.

public class DefineAlertsArray {

    public ArrayList<String> alertsArray;

    private DefineAlertsArray() { alertsArray = new ArrayList<String>(); }

    private static DefineAlertsArray instance;


    public static DefineAlertsArray getInstance() {
        if (instance == null) instance = new DefineAlertsArray();
        return instance;
    }

    public ArrayList<String> getArrayList() {
        return alertsArray;
    }

    public void setArrayList(String newAlertItem) {
        alertsArray.add(newAlertItem);
    }

    public void remArrayList(String newAlertItem) {
        alertsArray.remove(newAlertItem);
    }

}

