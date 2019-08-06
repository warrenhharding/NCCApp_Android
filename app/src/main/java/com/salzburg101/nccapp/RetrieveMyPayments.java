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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class RetrieveMyPayments extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "RetrieveMyPayments";

    ArrayList<StructureForRetrieveMyPayments> myPaymentsSummary = new ArrayList<StructureForRetrieveMyPayments>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Kicking off RetrieveMyPayments");
        setContentView(R.layout.retrieve_all_mypayments);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        retrievePreviousOrderSummary();

        mListView = (ListView) findViewById(R.id.order_list_view);

        AdapterMyPaymentsSummary adapter = new AdapterMyPaymentsSummary(this, myPaymentsSummary);

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
//                StructureForRetrieveMyPayments selectedOrder = myPaymentsSummary.get(position);
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
//        StructureForRetrieveMyPayments selectedOrder = myPaymentsSummary.get(position);
//
//        String recordId = selectedOrder.getRecordId();
//
//        AdapterMyPaymentsSummary adapter = new AdapterMyPaymentsSummary(this, myPaymentsSummary);
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



    public void retrievePreviousOrderSummary() {
        Log.i(TAG, "Starting retrievePreviousOrderSummary");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("Payments/" + mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            // query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myPaymentsSummary.clear();
                    System.out.println("The payments Summary arrayList should now be empty: " + myPaymentsSummary);
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The payments summary table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String chargeDescription, date, date1, recordId, amount;
                        Date date2 = new Date();

                        recordId = alerts.getKey();

                        if (alerts.child("chargeName").exists()) {
                            chargeDescription = alerts.child("chargeName").getValue().toString();
                        } else {
                            chargeDescription = "";
                        }

                        if (alerts.child("amount").exists()) {
                            amount = alerts.child("amount").getValue().toString();
                        } else {
                            amount = "0";
                        }
                        Double charge = Double.parseDouble(amount) / 100;
                        amount = String.format("%.2f", charge);
                        amount = "€" + amount;


                        if (alerts.child("date").exists()) {
                            date1 = alerts.child("date").getValue().toString();
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

                            // timeInterval = TimeAgoHelper.getTimeAgo(seconds);

                            // if (timeInterval.equals("NULL")) {
                            //     timeInterval = outputDate2.format(date2);
                            // }

                        } else {
                            return;
                        }

                        System.out.println("Now adding the record to userDocsSummary");
                        myPaymentsSummary.add(new StructureForRetrieveMyPayments(chargeDescription, date, amount, recordId));
                        System.out.println("The payment Summary arrayList should now have a new entry: " + myPaymentsSummary);
                        System.out.println("The number of records is: " + myPaymentsSummary.size());


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
        Collections.sort(myPaymentsSummary, new Comparator<StructureForRetrieveMyPayments>() {
            DateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            @Override
            public int compare(StructureForRetrieveMyPayments t1, StructureForRetrieveMyPayments structureForRetrieveMyPayments) {
                try {
                    return f.parse(structureForRetrieveMyPayments.getDate()).compareTo(f.parse(t1.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        System.out.println("Receibed the list and now sending it to adapter.");

        AdapterMyPaymentsSummary adapter = new AdapterMyPaymentsSummary(this, myPaymentsSummary);
        if (myPaymentsSummary.size() > 0) {
            mListView.setAdapter(adapter);
        }

    }




}
