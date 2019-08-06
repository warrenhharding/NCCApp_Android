package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineMyPayments {

    public ArrayList<StructureForRetrieveMyPayments> myPaymentsSummary;

    private DefineMyPayments() { myPaymentsSummary = new ArrayList<StructureForRetrieveMyPayments>(); }

    private static DefineMyPayments instance;


    public static DefineMyPayments getInstance() {
        if (instance == null) instance = new DefineMyPayments();
        return instance;
    }

    public ArrayList<StructureForRetrieveMyPayments> getArrayList() {
        return myPaymentsSummary;
    }

    public void setArraySummary(StructureForRetrieveMyPayments newMyPaymentItem) {
        myPaymentsSummary.add(newMyPaymentItem);
    }

    public void remArrayList(String newMyPaymentItem) {
        myPaymentsSummary.remove(newMyPaymentItem);
    }

}
