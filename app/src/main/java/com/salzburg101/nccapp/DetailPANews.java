package com.salzburg101.nccapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailPANews extends AppCompatActivity implements View.OnClickListener {

    // private static final String TAG = "PreviousOrderDetail";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private ValueEventListener mValueEventListener;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("/files/");


    private String recordId, attachment1, message, headline, link, date, picture1, subheadline, userName;
    private Button backButton;
    // ImageView pictureView;

    public TextView subjectView, subheadlineView, sentByView, dateView, linkView, messageView, fromView, linkTitleView, dateTitleView;

    LinearLayout linearLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_panews);

        subjectView = (TextView) findViewById(R.id.subjectView);
        subheadlineView = (TextView) findViewById(R.id.subheadlineView);
        sentByView = (TextView) findViewById(R.id.sentByView);
        dateView = (TextView) findViewById(R.id.dateView);
        linkView = (TextView) findViewById(R.id.linkView);
        messageView = (TextView) findViewById(R.id.messageView);
        // messageView.setMovementMethod(new ScrollingMovementMethod());
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        fromView = (TextView) findViewById(R.id.fromView);
        linkTitleView = (TextView) findViewById(R.id.linkTitleView);
        dateTitleView = (TextView) findViewById(R.id.dateTitleView);

        if (Build.VERSION.SDK_INT < 23) {
            TextViewCompat.setTextAppearance(subjectView, R.style.fontForTitles);
            TextViewCompat.setTextAppearance(subheadlineView, R.style.fontForSubtitles);
            TextViewCompat.setTextAppearance(fromView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(sentByView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(dateTitleView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(dateView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(linkTitleView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(linkView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(messageView, R.style.fontForMessages);
        } else {
            subjectView.setTextAppearance(R.style.fontForTitles);
            subheadlineView.setTextAppearance(R.style.fontForSubtitles);
            fromView.setTextAppearance(R.style.fontForMessages);
            sentByView.setTextAppearance(R.style.fontForMessages);
            dateTitleView.setTextAppearance(R.style.fontForMessages);
            dateView.setTextAppearance(R.style.fontForMessages);
            linkTitleView.setTextAppearance(R.style.fontForMessages);
            linkView.setTextAppearance(R.style.fontForMessages);
            messageView.setTextAppearance(R.style.fontForMessages);
        }


        backButton = (Button) findViewById(R.id.largeMenuButton);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        recordId = this.getIntent().getExtras().getString("recordId");
        System.out.println("The recordId transferred from the summary screen was : " + recordId);

        final Context context = this;

        retrieveOrder();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent newsIntent = new Intent(this, RetrievePANews.class);
            startActivity(newsIntent);
            finish();
        }
    }


    private void retrieveOrder() {
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("ParAssNews").child(recordId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // attachment1, date,

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
                    linkView.setText(link);
                    System.out.println("linkView: " + linkView.getText());


                    if (dataSnapshot.child("description").exists()) {
                        message = dataSnapshot.child("description").getValue().toString();
                    } else {
                        message = "";
                    }
                    messageView.setText(message);
                    System.out.println("messageView: " + messageView.getText().toString());


                    if (dataSnapshot.child("headline").exists()) {
                        headline = dataSnapshot.child("headline").getValue().toString();
                    } else {
                        headline = "";
                    }
                    subjectView.setText(headline);


                    if (dataSnapshot.child("subheadline").exists()) {
                        subheadline = dataSnapshot.child("subheadline").getValue().toString();
                    } else {
                        subheadline = "";
                    }
                    subheadlineView.setText(subheadline);


                    if (dataSnapshot.child("userName").exists()) {
                        userName = dataSnapshot.child("userName").getValue().toString();
                    } else {
                        userName = "";
                    }
                    sentByView.setText(userName);

                    if (dataSnapshot.child("attachment1").exists()) {
                        attachment1 = dataSnapshot.child("attachment1").getValue().toString();
                    } else {
                        attachment1 = "";
                    }


                    if (dataSnapshot.child("picture1").exists()) {
                        picture1 = dataSnapshot.child("picture1").getValue().toString();
                        if (!picture1.equals("")) {
                            storageRef.child("/files/" + picture1).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    ImageView pictureView = new ImageView(getApplicationContext());
                                    pictureView.setImageBitmap(bmp);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                                    pictureView.setVisibility(View.VISIBLE);
                                    pictureView.setLayoutParams(params);

                                    params.setMargins(50,0,50,0);

                                    if(messageView.getParent() != null) {
                                        ((ViewGroup) messageView.getParent()).removeView(messageView);
                                        System.out.println("Just removed the messageView");
                                    }


                                    TextView descriptionView = new TextView(getApplicationContext());
                                    descriptionView.setLayoutParams(params);
                                    descriptionView.setText(message);

                                    if (Build.VERSION.SDK_INT < 23) {
                                        System.out.println("Setting text to fontForMessages in the OLDER version");
                                        TextViewCompat.setTextAppearance(descriptionView, R.style.fontForMessages);
                                    } else {
                                        System.out.println("Setting text to fontForMessages in the NEWER version");
                                        descriptionView.setTextAppearance(R.style.fontForMessages);
                                    }
                                    linearLayout.addView(pictureView);
                                    linearLayout.addView(descriptionView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    } else {
                        picture1 = "";
                    }


                    if (dataSnapshot.child("organisedDate").exists()) {
                        date1 = dataSnapshot.child("organisedDate").getValue().toString();
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

                        dateView.setText(date + " " + time);
                        System.out.println("dateView: " + dateView.getText());
                    } else {
                        return;
                    }
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
