package com.salzburg101.nccapp;

public class StructureForRetrieveMyClassNews {

    private String mUserName, mHeadline, mDate, mRecordId, mTimeInterval, mClassroom;

    public StructureForRetrieveMyClassNews(String userName, String headline, String date, String recordId, String timeInterval, String classroom) {
        mUserName = userName;
        mHeadline = headline;
        mDate = date;
        mRecordId = recordId;
        mTimeInterval = timeInterval;
        mClassroom = classroom;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getDate() {
        return mDate;
    }

    public String getRecordId() {
        return mRecordId;
    }

    public String getTimeInterval() { return mTimeInterval; }

    public String getClassroom() { return mClassroom; }



//    public int compareTo(StructureForRetrieveAllAlerts compareAlerts) {
//
//
//        int compareQuantity = Integer.parseInt(((StructureForRetrieveAllAlerts) compareAlerts).getDate1());
//
//
//        return compareQuantity - Integer.parseInt(this.mDate1);
//    }




}

