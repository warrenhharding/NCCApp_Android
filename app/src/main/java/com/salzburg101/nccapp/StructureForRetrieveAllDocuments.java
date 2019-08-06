package com.salzburg101.nccapp;

public class StructureForRetrieveAllDocuments {

    private String mMessage, mDate, mRecordId, mTimeInterval, mFilename;

    public StructureForRetrieveAllDocuments(String message, String date, String recordId, String timeInterval, String filename) {
        mMessage = message;
        mDate = date;
        mRecordId = recordId;
        mTimeInterval = timeInterval;
        mFilename = filename;
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

    public String getFilename() {
        return mFilename;
    }

    public String getTimeInterval() {
        return mTimeInterval;
    }


}

