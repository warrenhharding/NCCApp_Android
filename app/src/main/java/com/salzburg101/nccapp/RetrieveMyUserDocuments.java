package com.salzburg101.nccapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// IMPORTANT NOTE ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Don't MIX UP StructureForRetrieveUserUploadedDocuments (which should be used here and which is for retrieving the documents
// uploaded) and StructureForUserUploadedDocuments which is used to create a new document by a user. Mixing them up will cost you
// time and effort
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class RetrieveMyUserDocuments extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "RetrieveMyUserDocuments";

    ArrayList<StructureForRetrieveUserUploadedDocuments> userDocsSummary = new ArrayList<StructureForRetrieveUserUploadedDocuments>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Kicking off RetrieveMyUserDocuments");
        setContentView(R.layout.retrieve_all_user_uploaded_documents);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        retrievePreviousOrderSummary();

        mListView = (ListView) findViewById(R.id.order_list_view);

        AdapterUserDocumentSummary adapter = new AdapterUserDocumentSummary(this, userDocsSummary);

        findViewById(R.id.menuButton).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        // findViewById(R.id.largeQButton).setOnClickListener(this);

        // This is required so that ???
        final Context context = this;



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setListenerForRowTapped(position);

                StructureForRetrieveUserUploadedDocuments selectedOrder = userDocsSummary.get(position);

//                Intent detailIntent = new Intent(context, PreviousOrderDetailActivity.class);
//
//                detailIntent.putExtra("orderRef", selectedOrder.getOrderRef());
//
//                startActivity(detailIntent);
            }
        });


    }


    private void setListenerForRowTapped(Integer position) {
        if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
            ConnectionDownAlert.connectionDown(this);
            return;
        }

        final Context context = this;
        StructureForRetrieveUserUploadedDocuments selectedOrder = userDocsSummary.get(position);

        String recordId = selectedOrder.getFilename();

        AdapterUserDocumentSummary adapter = new AdapterUserDocumentSummary(this, userDocsSummary);
        mListView.setAdapter(adapter);

        Intent userDocIntent = new Intent(context, DetailUserUploadedDocument.class);
        userDocIntent.putExtra("recordId", recordId);
        startActivity(userDocIntent);
        finish();
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent expandedIntent = new Intent(this, ExpandedMenu.class);
            startActivity(expandedIntent);
            finish();
        }

//        if (i == R.id.largeQButton) {
//            showGuide();
//        }

    }



    public void retrievePreviousOrderSummary() {
        Log.i(TAG, "Starting retrievePreviousOrderSummary");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("UploadedDocuments/" + mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            // query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userDocsSummary.clear();
                    System.out.println("The user Docs Summary arrayList should now be empty: " + userDocsSummary);
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The user Docs table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String recordId, docType, date, date1, timeInterval, time, filename, student, url;
                        Date date2 = new Date();

                        recordId = alerts.getKey();


                        if (alerts.child("docType").exists()) {
                            docType = alerts.child("docType").getValue().toString();
                        } else {
                            docType = "";
                        }

                        if (alerts.child("originalFileName").exists()) {
                            filename = alerts.child("originalFileName").getValue().toString();
                        } else {
                            filename = "";
                        }

                        if (alerts.child("customerImpacted").exists()) {
                            student = alerts.child("customerImpacted").getValue().toString();
                        } else {
                            student = "";
                        }

                        if (alerts.child("docUrl").exists()) {
                            url = alerts.child("docUrl").getValue().toString();
                        } else {
                            url = "";
                        }

                        if (alerts.child("docCreateDate").exists()) {
                            date1 = alerts.child("docCreateDate").getValue().toString();
                            SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            SimpleDateFormat outputDate2 = new SimpleDateFormat("dd-MMM-yyyy");

                            try {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000", Locale.ENGLISH);
                                date2 = dateFormat.parse(date1);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date2);
                            date = outputDate.format(date2);

                            Date nowDate = new Date();

                            long diff = nowDate.getTime() - date2.getTime();
                            long seconds = diff/1000;

                            timeInterval = TimeAgoHelper.getTimeAgo(seconds);

                            if (timeInterval.equals("NULL")) {
                                timeInterval = outputDate2.format(date2);
                            }

                        } else {
                            return;
                        }

                        System.out.println("Now adding the record to userDocsSummary");
                        userDocsSummary.add(new StructureForRetrieveUserUploadedDocuments(docType, date, recordId, timeInterval, filename, student, url));
                        System.out.println("The userDocsSummary arrayList should now have a new entry: " + userDocsSummary);
                        System.out.println("The number oif records is: " + userDocsSummary.size());


                    }
                    System.out.println("Now going to sort the arrayList userDocsSummary");
                    sortArrayList();

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }



    private void sortArrayList() {
        Log.i(TAG, "Kicking off sortArrayList");
        Collections.sort(userDocsSummary, new Comparator<StructureForRetrieveUserUploadedDocuments>() {
            DateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            @Override
            public int compare(StructureForRetrieveUserUploadedDocuments t1, StructureForRetrieveUserUploadedDocuments structureForRetrieveUserUploadedDocuments) {
                try {
                    return f.parse(structureForRetrieveUserUploadedDocuments.getDate()).compareTo(f.parse(t1.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        System.out.println("Receibed the list and now sending it to adapter.");

        AdapterUserDocumentSummary adapter = new AdapterUserDocumentSummary(this, userDocsSummary);
        if (userDocsSummary.size() > 0) {
            mListView.setAdapter(adapter);
        }

    }




}
