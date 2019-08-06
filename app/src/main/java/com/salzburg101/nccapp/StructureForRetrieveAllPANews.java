package com.salzburg101.nccapp;

public class StructureForRetrieveAllPANews {

    private String mUserName, mMessage, mDate, mRecordId, mTimeInterval;

    public StructureForRetrieveAllPANews(String userName, String message, String date, String recordId, String timeInterval) {
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

    public String getTimeInterval() {
        return mTimeInterval;
    }


}

