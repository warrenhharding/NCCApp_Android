package com.salzburg101.nccapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Date;
import java.util.Locale;

public class DetailAlert extends AppCompatActivity implements View.OnClickListener {

    // private static final String TAG = "PreviousOrderDetail";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    private String recordId, date, link, message, subject, userName;
    private Button backButton;

    public TextView subjectView, sentByView, dateView, linkView, messageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alert);

        subjectView = (TextView) findViewById(R.id.subjectView);
        sentByView = (TextView) findViewById(R.id.sentByView);
        dateView = (TextView) findViewById(R.id.dateView);
        linkView = (TextView) findViewById(R.id.linkView);
        messageView = (TextView) findViewById(R.id.messageView);
        messageView.setMovementMethod(new ScrollingMovementMethod());
        backButton = (Button) findViewById(R.id.largeMenuButton);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        recordId = this.getIntent().getExtras().getString("recordId");
        System.out.println("The recordId transferred from the summary screen was : " + recordId);

        retrieveOrder();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent alertsIntent = new Intent(this, RetrieveAlerts.class);
            startActivity(alertsIntent);
            finish();
        }

//        if (i == R.id.largeQButton) {
//            showGuide();
//        }
    }


    private void retrieveOrder() {
        mDatabaseReference = mFirebaseDatabase.getReference();

        Query query = mDatabaseReference.child("Notices").child(recordId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    System.out.println("The snapshot for the discount details does exist.");

                    String date, time, date1;
                    Date date2 = new Date();

                    // for (DataSnapshot alert : dataSnapshot.getChildren()) {

                        System.out.println("Snapshot : " + dataSnapshot);

                        if (dataSnapshot.child("link").exists()) {
                            link = dataSnapshot.child("link").getValue().toString();

                            if (link.equals("")) {
                                link = "None with this alert";
                            }


                        } else {
                            link = "None with this alert.";
                        }

                        linkView.setText("Link - " + link);
                        System.out.println("linkView: " + linkView.getText());

                        if (dataSnapshot.child("message").exists()) {
                            message = dataSnapshot.child("message").getValue().toString();
                        } else {
                            message = "";
                        }

                        messageView.setText(message);
                        System.out.println("messageView: " + messageView.getText().toString());


                        if (dataSnapshot.child("subject").exists()) {
                            subject = dataSnapshot.child("subject").getValue().toString();
                        } else {
                            subject = "";
                        }

                        subjectView.setText(subject);

                        if (dataSnapshot.child("userName").exists()) {
                            userName = dataSnapshot.child("userName").getValue().toString();
                        } else {
                            userName = "";
                        }

                        sentByView.setText("Posted By: " + userName);

                        if (dataSnapshot.child("date").exists()) {
                            date1 = dataSnapshot.child("date").getValue().toString();
                            SimpleDateFormat outputDateDate = new SimpleDateFormat("dd-MMM-yyyy");
                            SimpleDateFormat outputDateTime = new SimpleDateFormat("HH:mm");

                            try {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000", Locale.ENGLISH);
                                date2 = dateFormat.parse(date1);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            date = outputDateDate.format(date2);
                            time = outputDateTime.format(date2);

                            dateView.setText("Posted on " + date + " at " + time);

                        } else {
                            return;
                        }

//                        orderSummaryTable.add(new DetailOrderSummary(seqNumber, itemCat, orderItem, orderPrice, orderQuantity));
//
//                        System.out.println("The seqNumber : " + seqNumber);
//                        System.out.println("The orderPrice : " + orderPrice);
//                        System.out.println("The orderQuantity : " + orderQuantity);
//                        System.out.println("The itemCat : " + itemCat);
//                        System.out.println("The orderItem : " + orderItem);

                    // }

                } else {
                    System.out.println("The snapshot for the discount details DOES NOT exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.w(TAG, "There was a problem retrieving the complex order data for this order : " + orderRefClean, databaseError.toException());
            }
        });

    }



}
