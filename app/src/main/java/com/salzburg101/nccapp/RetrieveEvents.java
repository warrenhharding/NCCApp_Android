package com.salzburg101.nccapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class RetrieveEvents extends AppCompatActivity implements View.OnClickListener  {

    ProcessBadges instance = new ProcessBadges(this);

    ArrayList<StructureForRetrieveAllEvents> eventSummary = new ArrayList<StructureForRetrieveAllEvents>();
    ArrayList<String> alertsArray = new ArrayList<String>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoticesDatabaseReference;
    private DatabaseReference mRef;
    private ValueEventListener mValueEventListener;

    // private ListView mListView;
    private SwipeMenuListView mListView;
    private String[] listItems;


    // This code is used to create a listener for a new Alert which will then inform all other activities when it's triggered.
    private static ListenerNewEventReceived mListener;

    public static void setListenerNewEvent(ListenerNewEventReceived eventListener) {
        mListener = eventListener;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_all_events);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();



        // This is the code that is executed once the listener tells us that a new Alert has been created.
        RetrieveAlerts.setListenerNewAlert(new ListenerNewAlertReceived() {
            @Override
            public void onEvent() {
                // updateAlertBadge();
                setOtherBadges();
            }
        });

        // This is the code that is executed once the listener tells us that a new Alert has been created.
        RetrieveClassNews.setListenerNewNews(new ListenerNewNewsReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the newsBadge...");
                // updateNewsBadge();
                setOtherBadges();
            }
        });


        // This is the code that is executed once the listener tells us that a new Event has been created.
        RetrieveDocuments.setListenerNewDocument(new ListenerNewDocReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the docBadge...");
                setOtherBadges();
            }
        });


        // This is the code that is executed once the listener tells us that a new Event has been created.
        RetrievePANews.setListenerNewPANews(new ListenerNewPANewsReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the paNewsBadge...");
                setOtherBadges();
            }
        });



        setOtherBadges();

        eventSummary = DefineEventSummary.getInstance().getArrayList();
        alertsArray = DefineAlertsArray.getInstance().getArrayList();

        retrievePreviousOrderSummary();

        mListView = (SwipeMenuListView) findViewById(R.id.order_list_view);

        AdapterEventSummary adapter = new AdapterEventSummary(this, eventSummary);

        findViewById(R.id.menuButton).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        // findViewById(R.id.largeQButton).setOnClickListener(this);

        // This is required so that ???
        final Context context = this;

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "read" item
                SwipeMenuItem readItem = new SwipeMenuItem(
                        getApplicationContext());
                readItem.setBackground(new ColorDrawable(getResources().getColor(R.color.brandColor)));
                readItem.setWidth(160);
                readItem.setTitle("Mark as Read");
                readItem.setTitleSize(14);
                readItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(readItem);

                // create "unread" item
                SwipeMenuItem unreadItem = new SwipeMenuItem(
                        getApplicationContext());
                unreadItem.setBackground(new ColorDrawable(getResources().getColor(R.color.brandColor)));
                unreadItem.setWidth(160);
                unreadItem.setTitle("Mark as Unread");
                unreadItem.setTitleSize(14);
                unreadItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(unreadItem);
            }
        };


        mListView.setMenuCreator(creator);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setListenerForRowTapped(position);

                StructureForRetrieveAllEvents selectedOrder = eventSummary.get(position);

//                Intent detailIntent = new Intent(context, RetrieveAlerts.class);
//
//                detailIntent.putExtra("orderRef", selectedOrder.getRecordId());
//                detailIntent.putExtras(newsSummary);
//
//                startActivity(detailIntent);
            }
        });


        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                StructureForRetrieveAllEvents selectedOrder = eventSummary.get(position);
                String recordId = selectedOrder.getRecordId();

                AdapterEventSummary adapter = new AdapterEventSummary(getApplicationContext(), eventSummary);

                switch (index) {
                    case 0:
                        // System.out.println("JUST PRESSED BUTTON 0");
                        System.out.println("Inserting the item in the read list");
                        addReadRecord(recordId);
                        alertsArray.add(recordId);
                        mListView.setAdapter(adapter);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getBadgeNumber();
                            }
                        }, 500);
                        break;
                    case 1:
                        // System.out.println("JUST PRESSED BUTTON 1");
                        System.out.println("Removing the item from the read list");
                        addUnreadRecord(recordId);
                        alertsArray.remove(recordId);
                        mListView.setAdapter(adapter);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getBadgeNumber();
                            }
                        }, 500);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });



        // VERY VERY IMPORTANT - WILL SAVE YOU LOTS OF TIME
        // Make sure to leave the 'break' statement in for each button or it will cycle through the different screens.
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_alerts:
                        Intent alertsIntent = new Intent(context, RetrieveAlerts.class);
                        startActivity(alertsIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveEvents.this, "Alerts", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_news:
                        Intent newsIntent = new Intent(context, RetrieveClassNews.class);
                        startActivity(newsIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveEvents.this, "News", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_events:
//                        finish();
//                        Intent eventIntent = new Intent(context, RetrieveEvents.class);
//                        startActivity(eventIntent);
//                        overridePendingTransition(0, 0);
//                        Toast.makeText(RetrieveEvents.this, "Events", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_documents:
                        Intent docIntent = new Intent(context, RetrieveDocuments.class);
                        startActivity(docIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveEvents.this, "Documents", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_pa:
                        Intent paNewsIntent = new Intent(context, RetrievePANews.class);
                        startActivity(paNewsIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveEvents.this, "PA News", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_events);

    }





    private void setListenerForRowTapped(Integer position) {
        if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
            ConnectionDownAlert.connectionDown(this);
            return;
        }

        final Context context = this;
        StructureForRetrieveAllEvents selectedOrder = eventSummary.get(position);

        String recordId = selectedOrder.getRecordId();
        alertsArray.add(recordId);
        addReadRecord(recordId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getBadgeNumber();
            }
        }, 500);

        AdapterEventSummary adapter = new AdapterEventSummary(this, eventSummary);
        mListView.setAdapter(adapter);

        Intent eventIntent = new Intent(context, DetailEvent.class);
        eventIntent.putExtra("recordId", recordId);
        startActivity(eventIntent);
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



    private void retrievePreviousOrderSummary() {
        // Log.i(TAG, "Starting retrievePreviousOrderSummary");
        eventSummary.clear();

        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("Events");

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventSummary.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        // System.out.println("The Notices table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String date, date1, headline, userName, timeInterval, recordId, startDate, time, sortDate;
                        Date date2 = new Date();
                        Date date3 = new Date();
                        Date dateStart;


                        recordId = alerts.getKey();
                        // System.out.println("recordId is: " + recordId);

                        if (alerts.child("eventHeadline").exists()) {
                            headline = alerts.child("eventHeadline").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }

                        if (alerts.child("userName").exists()) {
                            userName = alerts.child("userName").getValue().toString();
                        } else {
                            return;
                        }



                        if (alerts.child("eventDateStart").exists()) {
                            startDate = alerts.child("eventDateStart").getValue().toString();
                            try {
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
                                date3 = dateFormat.parse(startDate);


                                System.out.println("startDate " + startDate + " has been translated to " + date3 + " for " + headline);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }




                        if (alerts.child("organisedDate").exists()) {
                            date1 = alerts.child("organisedDate").getValue().toString();
                            SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            SimpleDateFormat outputDate2 = new SimpleDateFormat("dd-MMM-yyyy");
                            SimpleDateFormat outputDateTime = new SimpleDateFormat("HH:mm");

                            try {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000", Locale.ENGLISH);
                                date2 = dateFormat.parse(date1);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date2);
                            date = outputDate.format(date2);
                            sortDate = outputDate.format(date3);
                            time = outputDateTime.format(date3);

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

                        startDate = (DateFormat.getDateInstance(DateFormat.FULL).format(date3)).toString() + " at " + time;




                        eventSummary.add(new StructureForRetrieveAllEvents(userName, headline, date, recordId, timeInterval, startDate, sortDate));
                        System.out.println("eventSummary contains: " + eventSummary.toString());

//                        for (StructureForRetrieveMyClassNews elem_ : newsSummary) {
//                            // System.out.println(elem_.getUserName());
//                        }

                    }

                    sortArrayList();
                    getBadgeNumber();
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

        Collections.sort(eventSummary, new Comparator<StructureForRetrieveAllEvents>() {
            DateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            @Override
            public int compare(StructureForRetrieveAllEvents t1, StructureForRetrieveAllEvents structureForRetrieveAllEvents) {
                try {
                    return f.parse(structureForRetrieveAllEvents.getSortDate()).compareTo(f.parse(t1.getSortDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        AdapterEventSummary adapter = new AdapterEventSummary(this, eventSummary);
        if (eventSummary.size() > 0) {
            mListView.setAdapter(adapter);
        }



    }


    private void getBadgeNumber() {
        eventSummary = DefineEventSummary.getInstance().getArrayList();
        alertsArray = DefineAlertsArray.getInstance().getArrayList();
        // System.out.println("alertsArray = " + alertsArray.toString());
        final BottomNavigationView navBar = this.findViewById(R.id.bottom_navigation);
        // alertSummary = retrieveAlerts.alertSummary;
        System.out.println("alertSummary has " + eventSummary.size() + " entries.");
        final Integer n = eventSummary.size();

        System.out.println("readAlerts should total " + alertsArray.size() + " entries.");
        Integer unreadCount = 0;
        for (int i = 0; i < n; i++) {
            StructureForRetrieveAllEvents event = eventSummary.get(i);
            String recordId;
            recordId = event.getRecordId();
            if (!alertsArray.contains(recordId)) {
                unreadCount = unreadCount + 1;
            }

            if (unreadCount > 0) {
                navBar.showBadge(R.id.action_events).setNumber(unreadCount);
                GlobalVariables.eventCount = unreadCount;
                // ((createGlobalVariables) this.getApplication()).setUnreadNews(unreadCount);
                // Here we trigger the listener which lets the other activities know that an event has occurred.
                if(mListener!=null) mListener.onEvent();
            } else {
                navBar.removeBadge(R.id.action_events);
                GlobalVariables.eventCount = unreadCount;
                // ((createGlobalVariables) this.getApplication()).setUnreadNews(unreadCount);
                // Here we trigger the listener which lets the other activities know that an event has occurred.
                if(mListener!=null) mListener.onEvent();
            }
        }
    }



    private void updateAlertBadge() {
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // Integer unreadAlerts = ((createGlobalVariables) this.getApplication()).getUnreadAlerts();
        Integer unreadAlerts = GlobalVariables.alertCount;
        if (unreadAlerts > 0) {
            navBar.showBadge(R.id.action_alerts).setNumber(unreadAlerts);
        } else {
            navBar.removeBadge(R.id.action_alerts);
        }

    }

    private void updateNewsBadge() {
        System.out.println("Starting to update the newsBadge...");
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Integer unreadNews = GlobalVariables.newsCount;
        if (unreadNews > 0) {
            System.out.println("updateNewsBadge() - The count is greater than 0, so placing a badge the on MyClass. The number of unread is: " + unreadNews);
            navBar.showBadge(R.id.action_news).setNumber(unreadNews);
        } else {
            System.out.println("updateNewsBadge() - The count is less than 1, so placing NO badge on MyClass.");
            navBar.removeBadge(R.id.action_news);
        }

    }



    private void setOtherBadges() {
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // Integer unreadAlerts = ((createGlobalVariables) this.getApplication()).getUnreadAlerts();

        Integer unreadAlerts = GlobalVariables.alertCount;
        if (unreadAlerts > 0) {
            System.out.println("The count is greater than 0, so placing a badge the on Alerts. The number of unread is: " + unreadAlerts);
            navBar.showBadge(R.id.action_alerts).setNumber(unreadAlerts);
        } else {
            System.out.println("The count is less than 1, so placing NO badge on Alerts.");
            navBar.removeBadge(R.id.action_alerts);
        }

        Integer unreadNews = GlobalVariables.newsCount;
        if (unreadNews > 0) {
            System.out.println("setOtherBadges() - The count is greater than 0, so placing a badge the on MyClass. The number of unread is: " + unreadNews);
            navBar.showBadge(R.id.action_news).setNumber(unreadNews);
        } else {
            System.out.println("setOtherBadges() - The count is less than 1, so placing NO badge on MyClass.");
            navBar.removeBadge(R.id.action_news);
        }

        Integer unreadDoc = GlobalVariables.docCount;
        if (unreadDoc > 0) {
            navBar.showBadge(R.id.action_documents).setNumber(unreadDoc);
        } else {
            navBar.removeBadge(R.id.action_documents);
        }
        Integer unreadPANews = GlobalVariables.paNewsCount;
        if (unreadPANews > 0) {
            navBar.showBadge(R.id.action_pa).setNumber(unreadPANews);
        } else {
            navBar.removeBadge(R.id.action_pa);
        }
    }


    private void addReadRecord(String recordId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNoticesDatabaseReference = mFirebaseDatabase.getReference();
        mNoticesDatabaseReference.child("MessagesRead").child(uid).child(recordId).setValue(true);
    }

    private void addUnreadRecord(String recordId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNoticesDatabaseReference = mFirebaseDatabase.getReference();
        mNoticesDatabaseReference.child("MessagesRead").child(uid).child(recordId).removeValue();
    }






}
