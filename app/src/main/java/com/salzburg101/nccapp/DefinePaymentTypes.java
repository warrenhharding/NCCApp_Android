package com.salzburg101.nccapp;

// NewsArray is used to store recordIds for alerts which have been 'read'. They are then compared to
// recordIds of existing alerts to determine which have been read and which haven't been read. This then
// determines how many 'new' or 'unread' messages are to be shown on the Alerts badge.

import java.util.ArrayList;

public class DefinePaymentTypes {

    public ArrayList<StructureForRetrieveAllPaymentTypes> paymentTypesSummary;

    private DefinePaymentTypes() {
        paymentTypesSummary = new ArrayList<StructureForRetrieveAllPaymentTypes>();
    }

    private static DefinePaymentTypes instance;


    public static DefinePaymentTypes getInstance() {
        if (instance == null) instance = new DefinePaymentTypes();
        return instance;
    }

    public ArrayList<StructureForRetrieveAllPaymentTypes> getArrayList() {
        return paymentTypesSummary;
    }

    public void setArraySummary(StructureForRetrieveAllPaymentTypes newPaymentItem) {
        paymentTypesSummary.add(newPaymentItem);
    }

    public void remArrayList(String newPaymentItem) {
        paymentTypesSummary.remove(newPaymentItem);
    }

}
