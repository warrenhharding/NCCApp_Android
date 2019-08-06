package com.salzburg101.nccapp;

public class StructureForRetrieveMyPayments {

    private String mChargeDescription, mDate, mAmount, mRecordId;

    public StructureForRetrieveMyPayments(String chargeDescription, String date, String amount, String recordId) {
        mChargeDescription = chargeDescription;
        mDate = date;
        mAmount = amount;
        mRecordId = recordId;
    }

    public String getChargeDescription() {
        return mChargeDescription;
    }

    public String getDate() {
        return mDate;
    }

    public String getAmount() { return mAmount; }

    public String getRecordId() {
        return mRecordId;
    }




}

