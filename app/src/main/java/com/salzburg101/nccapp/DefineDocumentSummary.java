package com.salzburg101.nccapp;

import java.util.ArrayList;

public class DefineDocumentSummary {

    public ArrayList<StructureForRetrieveAllDocuments> docSummary;

    private DefineDocumentSummary() { docSummary = new ArrayList<StructureForRetrieveAllDocuments>(); }

    private static DefineDocumentSummary instance;


    public static DefineDocumentSummary getInstance() {
        if (instance == null) instance = new DefineDocumentSummary();
        return instance;
    }

    public ArrayList<StructureForRetrieveAllDocuments> getArrayList() {
        return docSummary;
    }

    public void setArraySummary(StructureForRetrieveAllDocuments newDocItem) {
        docSummary.add(newDocItem);
    }

    public void remArrayList(String newDocItem) {
        docSummary.remove(newDocItem);
    }

}
