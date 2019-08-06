package com.salzburg101.nccapp;

public class StructureForRetrieveAllEvents {

    private String mUserName, mHeadline, mDate, mRecordId, mTimeInterval, mStartDate, mSortDate;

    public StructureForRetrieveAllEvents(String userName, String headline, String date, String recordId, String timeInterval, String startDate, String sortDate) {
        mUserName = userName;
        mHeadline = headline;
        mDate = date;
        mRecordId = recordId;
        mTimeInterval = timeInterval;
        mStartDate = startDate;
        mSortDate = sortDate;
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

    public String getStartDate() {
        return mStartDate;
    }

    public String getTimeInterval() { return mTimeInterval; }

    public String getSortDate() { return mSortDate; }


}
