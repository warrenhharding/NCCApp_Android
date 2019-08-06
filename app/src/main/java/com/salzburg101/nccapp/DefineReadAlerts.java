package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineReadAlerts {

    public ArrayList<String> readAlerts;

    private DefineReadAlerts() { readAlerts = new ArrayList<String>(); }

    private static DefineReadAlerts instance;


    public static DefineReadAlerts getInstance() {
        if (instance == null) instance = new DefineReadAlerts();
        return instance;
    }

    public ArrayList<String> getArrayList() {
        return readAlerts;
    }

    public void setArrayList(String newAlertItem) {
        readAlerts.add(newAlertItem);
    }

    public void remArrayList(String newAlertItem) {
        readAlerts.remove(newAlertItem);
    }

}
