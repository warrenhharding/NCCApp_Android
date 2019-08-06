package com.salzburg101.nccapp;


// public class StructureForRetrieveAllAlerts implements Comparable<StructureForRetrieveAllAlerts> {

public class StructureForRetrieveAllAlerts {

    private String mUserName, mMessage, mDate, mRecordId, mTimeInterval;

    public StructureForRetrieveAllAlerts(String userName, String message, String date, String recordId, String timeInterval) {
        mUserName = userName;
        mMessage = message;
        mDate = date;
        mRecordId = recordId;
        mTimeInterval = timeInterval;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        return mDate;
    }

    public String getRecordId() {
        return mRecordId;
    }

    public String getTimeInterval() { return mTimeInterval; }



//    public int compareTo(StructureForRetrieveAllAlerts compareAlerts) {
//
//
//        int compareQuantity = Integer.parseInt(((StructureForRetrieveAllAlerts) compareAlerts).getDate1());
//
//
//        return compareQuantity - Integer.parseInt(this.mDate1);
//    }




}

