package com.salzburg101.nccapp;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


// So How does this work?
// On startup, the application will run the populateArrays methods (alerts, news etc etc). This sets up globally available
// ArrayLists for the Alerts, News etc etc, which can then also be used by other activities and classes. These methods will
// only run at startup, to get these ArrayLists initialised and populated before the first screen appears. That's because the same
// methods run within the inidividual activities post launch of the app. That's why they are defined with a singleListenerEvent.
// The ArrayLists which these methods initialise are defined in the classes DefineAlertsSummary, DefineNewsSummary etc.

// So what's next?
// When a database entry is made, the methods in the individual activities, which have permanent listeners running for changes to
// the Firebase database, will run the method to populate the alertSummary or newsSummary ArrayLists. They then kick the list into
// this class.



public class ProcessBadges {


    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    // public SharedPreferences mPreferences;
    // public SharedPreferences.Editor mEditor;

    public static ArrayList<String> myClassesArray = new ArrayList<String>();


//    ArrayList<StructureForRetrieveAllAlerts> alertSummary = new ArrayList<StructureForRetrieveAllAlerts>();
    ArrayList<String> alertsArray = new ArrayList<String>();
    ArrayList<String> newsCount = new ArrayList<String>();
    ArrayList<String> eventCount = new ArrayList<String>();
    ArrayList<String> paNewsCount = new ArrayList<String>();
    ArrayList<String> docCount = new ArrayList<String>();


//    private SharedPreferences sharedPreferences;
//    private static String myClasses = "myClasses";
//    SharedPreferences sp = mContext.getSharedPreferences("myClasses", Context.MODE_PRIVATE);


    // if (sp.contains(recordId)) {
    //     readStatus = true;
    // }

    // AlertSummaryAdapter instance = new AlertSummaryAdapter(this);

    // This code is used to create a listener for a new Alert which will then inform all other activities when it's triggered.
    private static ListenerNewEventReceived mListener;

    public static void setListenerNewEvent(ListenerNewEventReceived eventListener) {
        mListener = eventListener;
    }




    private SwipeMenuListView mListView;


    // This is necessary to get the findViewById to work
    public RetrieveAlerts retrieveAlerts;
    public ProcessBadges( RetrieveAlerts retrieveAlerts){
        this.retrieveAlerts = retrieveAlerts;
    }


    // This is necessary to get the findViewById to work
    public RetrieveClassNews retrieveClassNews ;
    public ProcessBadges( RetrieveClassNews retrieveClassNews){
        this.retrieveClassNews = retrieveClassNews;
    }


    // This is necessary to get the findViewById to work
    public RetrievePANews retrievePANews ;
    public ProcessBadges( RetrievePANews retrievePANews){
        this.retrievePANews = retrievePANews;
    }


    // This is necessary to get the findViewById to work
    public RetrieveDocuments retrieveDocuments ;
    public ProcessBadges( RetrieveDocuments retrieveDocuments){
        this.retrieveDocuments = retrieveDocuments;
    }


    // This is necessary to get the findViewById to work
    public RetrieveEvents retrieveEvents ;
    public ProcessBadges( RetrieveEvents retrieveEvents){
        this.retrieveEvents = retrieveEvents;
    }

    // This is necessary to get the findViewById to work
    public MainActivity mainActivity ;
    public ProcessBadges( MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }



    public void populateAlertsArray() {
        // alertsArray.clear();
        mAuth = FirebaseAuth.getInstance();
        alertsArray = DefineAlertsArray.getInstance().getArrayList();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        Query query = mRef.child("MessagesRead").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("The Snapshot seems to exist alright...");
                    alertsArray.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {
                        dataSnapshot.getChildrenCount();
                        System.out.println("This uid has a total of " + dataSnapshot.getChildrenCount() + " records.");

                        String recordId;

                        recordId = alerts.getKey();
                        alertsArray.add(recordId);
                        System.out.println("alertsArray = " + alertsArray.toString());
                    }
                } else {
                    System.out.println("The Snapshot doesn't seem to exist...");
                    System.out.println("Earlier than expected - Now kicking off the other population activities.");
                    populateBadges();
                    return;
                }
                System.out.println("Now kicking off the other population activities.");
                populateBadges();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void populateBadges() {
        populateEventSummary();
        // populateNewsSummary();
        populateDocumentSummary();
        populatePANewsSummary();
    }



    //Starting processing for My Class News
    public void populateNewsSummary() {
        // mPreferences = MainActivity.contextOfApplication.getSharedPreferences("myClasses", Context.MODE_PRIVATE);
        newsCount.clear();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("MyClassNews");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    newsCount.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        String recordId;
                        String classroom = "";
                        recordId = alerts.getKey();

                        if (alerts.child("classroom").exists()) {
                            classroom = alerts.child("classroom").getValue().toString();
                        } else {

                        }
                        // if (mPreferences.contains(classroom)) {
                        if (myClassesArray.contains(classroom)) {
                            newsCount.add(recordId);
                        }
                        }
                } else {

                }
                // More code here
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                alertsArray = DefineAlertsArray.getInstance().getArrayList();
                final Integer n = newsCount.size();
                Integer unreadCount = 0;
                System.out.println("There are " + newsCount.size() + " records in the newsCount arraylist.");
                System.out.println("The (myNews) alertsArray arrayList = " + alertsArray.toString());
                System.out.println("The newsCount arrayList = " + newsCount.toString());
                for (int i = 0; i < n; i++) {
                    String recordId = newsCount.get(i);
                    if (!alertsArray.contains(recordId)) {
                        unreadCount = unreadCount + 1;
                    }
                    GlobalVariables.newsCount = unreadCount;
                }
                    }
                }, 400);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }




    //Starting processing for Events
    public void populateEventSummary() {
        eventCount.clear();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("Events");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventCount.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        String recordId;
                        recordId = alerts.getKey();
                        eventCount.add(recordId);
                    }
                } else {

                }
                // More code here
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertsArray = DefineAlertsArray.getInstance().getArrayList();
                        final Integer n = eventCount.size();
                        Integer unreadCount = 0;
                        System.out.println("There are " + eventCount.size() + " records in the eventCount arraylist.");
                        System.out.println("The alertsArray arrayList = " + alertsArray.toString());
                        System.out.println("The eventCount arrayList = " + eventCount.toString());
                        for (int i = 0; i < n; i++) {
                            String recordId = eventCount.get(i);
                            System.out.println("recordId " + recordId + " is being processed.");
                            if (!alertsArray.contains(recordId)) {
                                System.out.println("alertsArray doesn't contain recordId " + recordId);
                                unreadCount = unreadCount + 1;
                                System.out.println("unreadCount now = " + unreadCount);
                            }
                            GlobalVariables.eventCount = unreadCount;
                        }
                        System.out.println("Message about to be sent to update PANews Badge...");
                        if(mListener!=null) mListener.onEvent();

                    }
                }, 400);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }




    //Starting processing for Documents
    public void populateDocumentSummary() {
        docCount.clear();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("PostDetailsDatabase");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    docCount.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        String recordId;
                        recordId = alerts.getKey();
                        docCount.add(recordId);
                    }
                } else {

                }
                // More code here
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertsArray = DefineAlertsArray.getInstance().getArrayList();
                        final Integer n = docCount.size();
                        Integer unreadCount = 0;
                        System.out.println("There are " + docCount.size() + " records in the docCount arraylist.");
                        System.out.println("The alertsArray arrayList = " + alertsArray.toString());
                        System.out.println("The docCount arrayList = " + docCount.toString());
                        for (int i = 0; i < n; i++) {
                            String recordId = docCount.get(i);
                            System.out.println("recordId " + recordId + " is being processed.");
                            if (!alertsArray.contains(recordId)) {
                                System.out.println("alertsArray doesn't contain recordId " + recordId);
                                unreadCount = unreadCount + 1;
                                System.out.println("unreadCount now = " + unreadCount);
                            }
                            GlobalVariables.docCount = unreadCount;
                        }
                        System.out.println("Message about to be sent to update Documents Badge...");
                        if(mListener!=null) mListener.onEvent();

                    }
                }, 400);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }









    //Starting processing for PANews
    public void populatePANewsSummary() {
        paNewsCount.clear();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("ParAssNews");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    paNewsCount.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        String recordId;
                        recordId = alerts.getKey();
                        paNewsCount.add(recordId);
                    }
                } else {

                }
                // More code here
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertsArray = DefineAlertsArray.getInstance().getArrayList();
                        final Integer n = paNewsCount.size();
                        Integer unreadCount = 0;
                        System.out.println("There are " + paNewsCount.size() + " records in the paNewsCount arraylist.");
                        System.out.println("The alertsArray arrayList = " + alertsArray.toString());
                        System.out.println("The eventCount arrayList = " + paNewsCount.toString());
                        for (int i = 0; i < n; i++) {
                            String recordId = paNewsCount.get(i);
                            System.out.println("recordId " + recordId + " is being processed.");
                            if (!alertsArray.contains(recordId)) {
                                System.out.println("alertsArray doesn't contain recordId " + recordId);
                                unreadCount = unreadCount + 1;
                                System.out.println("unreadCount now = " + unreadCount);
                            }
                            GlobalVariables.paNewsCount = unreadCount;
                        }
                        System.out.println("Message about to be sent to update PANews Badge...");
                        if(mListener!=null) mListener.onEvent();

                    }
                }, 400);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }




    //Starting processing for myClasses
    public void getMyClasses() {
        myClassesArray.clear();
        // mPreferences = MainActivity.contextOfApplication.getSharedPreferences("myClasses", Context.MODE_PRIVATE);
        // mEditor = mPreferences.edit();
        // mEditor.clear();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        System.out.println("The current user email address is: " + mAuth.getCurrentUser().getEmail());
        String uid = mAuth.getCurrentUser().getUid();
        Query query = mRef.child("MyClasses/" + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // mPreferences = MainActivity.contextOfApplication.getSharedPreferences("myClasses", Context.MODE_PRIVATE);
                    // mEditor = mPreferences.edit();



                    for (DataSnapshot classes : dataSnapshot.getChildren()) {
                        dataSnapshot.getChildrenCount();
                        String classroom = "";

                        if (classes.child("classroom").exists()) {
                            System.out.println("Classroom does exist and it's " + classroom);
                            classroom = classes.child("classroom").getValue().toString();
                        }
                        myClassesArray.add(classroom);
                        // mEditor.putString(classroom, classroom);
                        // mEditor.commit();
                        // System.out.println(classroom + "should just have been added to sharedPreferences" + classroom);


                        System.out.println("The contents of the myClasses are: " + myClassesArray.toString());

                    }
                    populateNewsSummary();
                } else {

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });
    }











}
