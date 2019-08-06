package com.salzburg101.nccapp;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class DetailEvent extends AppCompatActivity implements View.OnClickListener {

    // private static final String TAG = "PreviousOrderDetail";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private ValueEventListener mValueEventListener;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("/files/");

    private com.google.api.services.calendar.Calendar mService = null;

    private String recordId, subject, subheadline, userName, link, starts, ends, message, picture1, date, topic;
    private Button backButton;
    // ImageView pictureView;

    public TextView subjectView, subheadlineView, sentByView, startDateView, endDateView, dateView, linkView, messageView, fromView, linkTitleView, dateTitleView, startsTitleView, startsView, endsTitleView, endsView;

    LinearLayout linearLayout;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        subjectView = (TextView) findViewById(R.id.subjectView);
        subheadlineView = (TextView) findViewById(R.id.subheadlineView);
        sentByView = (TextView) findViewById(R.id.sentByView);
        dateView = (TextView) findViewById(R.id.dateView);
        linkView = (TextView) findViewById(R.id.linkView);
        messageView = (TextView) findViewById(R.id.messageView);
        // messageView.setMovementMethod(new ScrollingMovementMethod());
        startsView = (TextView) findViewById(R.id.startsView);
        endsView = (TextView) findViewById(R.id.endsView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        startsTitleView = (TextView) findViewById(R.id.startsTitleView);

        fromView = (TextView) findViewById(R.id.fromView);
        linkTitleView = (TextView) findViewById(R.id.linkTitleView);
        dateTitleView = (TextView) findViewById(R.id.dateTitleView);
        startsTitleView = (TextView) findViewById(R.id.startsTitleView);
        endsTitleView = (TextView) findViewById(R.id.endsTitleView);

        findViewById(R.id.addToCalendarButton).setOnClickListener(this);
        findViewById(R.id.alertSignup).setOnClickListener(this);

        if (Build.VERSION.SDK_INT < 23) {
            TextViewCompat.setTextAppearance(subjectView, R.style.fontForTitles);
            TextViewCompat.setTextAppearance(subheadlineView, R.style.fontForSubtitles);
            TextViewCompat.setTextAppearance(fromView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(sentByView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(startsTitleView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(startsView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(endsTitleView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(endsView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(linkTitleView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(linkView, R.style.fontForMessages);
            TextViewCompat.setTextAppearance(messageView, R.style.fontForMessages);
        } else {
            subjectView.setTextAppearance(R.style.fontForTitles);
            subheadlineView.setTextAppearance(R.style.fontForSubtitles);
            fromView.setTextAppearance(R.style.fontForMessages);
            sentByView.setTextAppearance(R.style.fontForMessages);
            startsTitleView.setTextAppearance(R.style.fontForMessages);
            endsTitleView.setTextAppearance(R.style.fontForMessages);
            startsView.setTextAppearance(R.style.fontForMessages);
            endsView.setTextAppearance(R.style.fontForMessages);
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


    public void initialize(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent eventIntent = new Intent(this, RetrieveEvents.class);
            startActivity(eventIntent);
            finish();
        }

        if (i == R.id.addToCalendarButton) {
//            System.out.println("Starting the calendar update now...");
            DateTime startDateTime = new DateTime("2019-10-10T16:00:00-07:00");
            DateTime endDateTime = new DateTime("2019-10-10T17:00:00-07:00");
//            try {
//                insertEvent("This is the summary.","This is the location.","This is the description.",  startDateTime, endDateTime); // Here I try to call this method
//            } catch (IOException dfg) {
//                System.out.println("Updating the calendar has thrown an exception...");
//                Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_LONG).show();
//            }

            try {
                insertEvent("summary", "location", "des", startDateTime, endDateTime);
            } catch (IOException e) {
                System.out.println("Caught the exception.");
            }



        }

        if (i == R.id.alertSignup) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = getString(R.string.msg_subscribed);
                            System.out.println("About to call dialog for signup for alerts for: " + topic);
                            subSuccess();

                            if (!task.isSuccessful()) {
                                msg = getString(R.string.msg_subscribe_failed);
                            }
                            Toast.makeText(DetailEvent.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    private void subSuccess() {
        System.out.println("subSuccess fialog has been called and is active.");
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("You have now subscribed to updates on this event.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog topicSub = builder1.create();
        topicSub.show();
    }


    private void retrieveOrder() {
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("Events").child(recordId);

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
                    linkView.setText(link);
                    System.out.println("linkView: " + linkView.getText());


                    if (dataSnapshot.child("eventDescription").exists()) {
                        message = dataSnapshot.child("eventDescription").getValue().toString();
                    } else {
                        message = "";
                    }
                    messageView.setText(message);
                    System.out.println("messageView: " + messageView.getText().toString());


                    if (dataSnapshot.child("eventHeadline").exists()) {
                        subject = dataSnapshot.child("eventHeadline").getValue().toString();
                    } else {
                        subject = "";
                    }
                    subjectView.setText(subject);


                    if (dataSnapshot.child("eventSubHeadline").exists()) {
                        subheadline = dataSnapshot.child("eventSubHeadline").getValue().toString();
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

                    if (dataSnapshot.child("eventDateStart").exists()) {
                        starts = dataSnapshot.child("eventDateStart").getValue().toString();
                    } else {
                        starts = "";
                    }
                    System.out.println("starts = " + starts.toString());
                    startsView.setText(starts);


                    if (dataSnapshot.child("eventDateEnd").exists()) {
                        ends = dataSnapshot.child("eventDateEnd").getValue().toString();
                    } else {
                        ends = "";
                    }
                    endsView.setText(ends);


                    if (dataSnapshot.child("eventTopic").exists()) {
                        topic = dataSnapshot.child("eventTopic").getValue().toString();
                    } else {
                        topic = "";
                    }


                    if (dataSnapshot.child("eventPicture1").exists()) {
                        picture1 = dataSnapshot.child("eventPicture1").getValue().toString();
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

                        // dateView.setText(date + " " + time);
                        // System.out.println("dateView: " + dateView.getText());
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








    private void insertEvent(String summary, String location, String des, DateTime startDate, DateTime endDate) throws IOException {
        Event event = new Event()
                .setSummary("Google I/O 2015")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail("lpage@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = mService.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }








}
