package com.salzburg101.nccapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class RetrieveAlertPreferences extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "RetrieveAlertPrefs";

    ArrayList<StructureForRetrieveAlertPreferences> alertsPreferences = new ArrayList<StructureForRetrieveAlertPreferences>();
    public static ArrayList<String> topicsSubscribedTo = new ArrayList<String>();
    public static ArrayList<String> codesSubscribedTo = new ArrayList<String>();



    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "AlertPrefs");
        setContentView(R.layout.retrieve_all_alerts_prefs);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mListView = (ListView) findViewById(R.id.order_list_view);

        retrievePreviousOrderSummary();

        AdapterAlertPreferences adapter = new AdapterAlertPreferences(this, alertsPreferences);

        findViewById(R.id.menuButton).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        // findViewById(R.id.largeQButton).setOnClickListener(this);

        // This is required so that ???
        final Context context = this;



//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                setListenerForRowTapped(position);
//
//                StructureForRetrieveAlertPreferences selectedOrder = alertsPreferences.get(position);
//
////                Intent detailIntent = new Intent(context, PreviousOrderDetailActivity.class);
////
////                detailIntent.putExtra("orderRef", selectedOrder.getOrderRef());
////
////                startActivity(detailIntent);
//            }
//        });


    }


//    private void setListenerForRowTapped(Integer position) {
//        if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
//            ConnectionDownAlert.connectionDown(this);
//            return;
//        }
//
//        final Context context = this;
//        StructureForRetrieveAlertPreferences selectedOrder = alertsPreferences.get(position);
//
//        // String recordId = selectedOrder.getFilename();
//
//        AdapterAlertPreferences adapter = new AdapterAlertPreferences(this, alertsPreferences);
//        mListView.setAdapter(adapter);
//
//        Intent userDocIntent = new Intent(context, DetailUserUploadedDocument.class);
//        userDocIntent.putExtra("recordId", recordId);
//        startActivity(userDocIntent);
//        finish();
//    }



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


    public void retrieveCurrentPreferences() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        Query query = mRef.child("Subscriptions").child(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    topicsSubscribedTo.clear();
                    codesSubscribedTo.clear();

                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {
                        dataSnapshot.getChildrenCount();

                        String topicCode, recordId;
                        recordId = alerts.getKey();

                        if (alerts.child("topic").exists()) {
                            topicCode = alerts.child("topic").getValue().toString();
                            System.out.println("About to place " + topicCode + "into the codesSubscribedTo array");
                            codesSubscribedTo.add(topicCode);
                            topicsSubscribedTo.add(recordId);
                            System.out.println("codesSubscribedTo : " + codesSubscribedTo.size());
                            System.out.println("topicsSubscribedTo : " + topicsSubscribedTo.size());
                        }
                    }

                    sortArrayList();

                } else {
                    System.out.println("Looks like no snapshot exists");
                    sortArrayList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }





    public void retrievePreviousOrderSummary() {
        Log.i(TAG, "Starting retrievePreviousOrderSummary");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("Topics");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            // query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    alertsPreferences.clear();
                    System.out.println("The alerts pref Summary arrayList should now be empty: " + alertsPreferences);
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();

                        String topic, topicCode, topicDescription;
                        Boolean topicOptional;

                        if (alerts.child("optional").exists()) {
                            topicOptional = alerts.child("optional").getValue(Boolean.class);
                        } else {
                            topicOptional = false;
                        }

                        if (alerts.child("topic").exists()) {
                            topic = alerts.child("topic").getValue().toString();
                        } else {
                            topic = "";
                        }

                        if (alerts.child("topicCode").exists()) {
                            topicCode = alerts.child("topicCode").getValue().toString();
                        } else {
                            topicCode = "";
                        }

                        if (alerts.child("topicDescription").exists()) {
                            topicDescription = alerts.child("topicDescription").getValue().toString();
                        } else {
                            topicDescription = "";
                        }


                        System.out.println("Now adding the record to userDocsSummary");

                        if (topicOptional == true) {
                            alertsPreferences.add(new StructureForRetrieveAlertPreferences(topic, topicOptional, topicCode, topicDescription));
                        }


                        System.out.println("The alertpref arrayList should now have a new entry: " + alertsPreferences);
                        System.out.println("The number oif records is: " + alertsPreferences.size());


                    }
                    System.out.println("Now going to sort the arrayList userDocsSummary");
                    retrieveCurrentPreferences();

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
//        Collections.sort(userDocsSummary, new Comparator<StructureForRetrieveUserUploadedDocuments>() {
//            DateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//            @Override
//            public int compare(StructureForRetrieveUserUploadedDocuments t1, StructureForRetrieveUserUploadedDocuments structureForRetrieveUserUploadedDocuments) {
//                try {
//                    return f.parse(structureForRetrieveUserUploadedDocuments.getDate()).compareTo(f.parse(t1.getDate()));
//                } catch (ParseException e) {
//                    throw new IllegalArgumentException(e);
//                }
//            }
//        });
        System.out.println("Receibed the list and now sending it to adapter.");

        AdapterAlertPreferences adapter = new AdapterAlertPreferences(this, alertsPreferences);
        mListView.setAdapter(adapter);
    }




}
