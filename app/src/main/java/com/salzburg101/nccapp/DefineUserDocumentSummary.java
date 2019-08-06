package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineUserDocumentSummary {

    public ArrayList<StructureForRetrieveUserUploadedDocuments> userDocSummary;

    private DefineUserDocumentSummary() { userDocSummary = new ArrayList<StructureForRetrieveUserUploadedDocuments>(); }

    private static DefineUserDocumentSummary instance;


    public static DefineUserDocumentSummary getInstance() {
        if (instance == null) instance = new DefineUserDocumentSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveUserUploadedDocuments> getArrayList() {
        return userDocSummary;
    }

    public void setArraySummary(StructureForRetrieveUserUploadedDocuments newDocItem) {
        userDocSummary.add(newDocItem);
    }

    public void remArrayList(String newDocItem) {
        userDocSummary.remove(newDocItem);
    }

}
