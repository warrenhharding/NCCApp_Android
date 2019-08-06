package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineAlertPreferences {

    public ArrayList<String> alertsPreferences;

    private DefineAlertPreferences() { alertsPreferences = new ArrayList<String>(); }

    private static DefineAlertPreferences instance;


    public static DefineAlertPreferences getInstance() {
        if (instance == null) instance = new DefineAlertPreferences();
        return instance;
    }

    public ArrayList<String> getArrayList() {
        return alertsPreferences;
    }

    public void setArrayList(String newPref) {
        alertsPreferences.add(newPref);
    }

    public void remArrayList(String newPref) {
        alertsPreferences.remove(newPref);
    }

}

