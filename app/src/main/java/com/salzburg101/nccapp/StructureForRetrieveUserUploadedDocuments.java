package com.salzburg101.nccapp;

public class StructureForRetrieveUserUploadedDocuments {

    private String mMessage, mDate, mRecordId, mTimeInterval, mFilename, mStudent, mUrl;

    public StructureForRetrieveUserUploadedDocuments(String message, String date, String recordId, String timeInterval, String filename, String student, String url) {
        mMessage = message;
        mDate = date;
        mRecordId = recordId;
        mTimeInterval = timeInterval;
        mFilename = filename;
        mStudent = student;
        mUrl = url;
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

    public String getStudent() {
        return mStudent;
    }

    public String getUrl() {
        return mUrl;
    }


}
