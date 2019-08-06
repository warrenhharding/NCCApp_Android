package com.salzburg101.nccapp;

public class StructureForUserUploadedDocuments {

    private String mCustomerImpacted, mDocCreateDate, mDocCreatedBy, mDocDescription, mDocType, mDocUrl, mOriginalFileName;

    public StructureForUserUploadedDocuments(String customerImpacted, String docCreateDate, String docCreatedBy, String docDescription, String docType, String docUrl, String originalFileName) {
        mCustomerImpacted = customerImpacted;
        mDocCreateDate = docCreateDate;
        mDocCreatedBy = docCreatedBy;
        mDocDescription = docDescription;
        mDocUrl = docUrl;
        mOriginalFileName = originalFileName;
    }

    public String getCustomerImpacted() {
        return mCustomerImpacted;
    }

    public String getDocCreateDate() {
        return mDocCreateDate;
    }

    public String getDocCreatedBy() {
        return mDocCreatedBy;
    }

    public String getDocDescription() {
        return mDocDescription;
    }

    public String getDocType() {
        return mDocType;
    }

    public String getDocUrl() {
        return mDocUrl;
    }

    public String getOriginalFileName() {
        return mOriginalFileName;
    }


}