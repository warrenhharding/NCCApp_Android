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
import com.google.firebase.database.ChildEventListener;
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

public class RetrieveDocuments extends AppCompatActivity implements View.OnClickListener  {


    ProcessBadges instance = new ProcessBadges(this);

    ArrayList<StructureForRetrieveAllDocuments> docSummary = new ArrayList<StructureForRetrieveAllDocuments>();
    ArrayList<String> alertsArray = new ArrayList<String>();



//    private static final String TAG = "RetrieveAllAlerts";
//
//    public static final String readAlerts = "readAlerts" ;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoticesDatabaseReference;
    private DatabaseReference mRef;
    private ValueEventListener mValueEventListener;
    private ChildEventListener mChildEventListener;

    // private ListView mListView;
    private SwipeMenuListView mListView;
    private String[] listItems;

    // This code is used to create a listener for a new Alert which will then inform all other activities when it's triggered.
    private static ListenerNewDocReceived mListener;

    public static void setListenerNewDocument(ListenerNewDocReceived eventListener) {
        mListener = eventListener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_all_documents);

        // populateAlertsArray();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // This is the code that is executed once the listener tells us that a new Alert has been created.
        RetrieveClassNews.setListenerNewNews(new ListenerNewNewsReceived() {
            @Override
            public void onEvent() {
                // System.out.println("Message receievd to update the newsBadge...");
                updateNewsBadge();
            }
        });


        // This is the code that is executed once the listener tells us that a new Event has been created.
        RetrieveEvents.setListenerNewEvent(new ListenerNewEventReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the newsBadge...");
                setOtherBadges();
            }
        });

        // This is the code that is executed once the listener tells us that a new Event has been created.
        RetrieveAlerts.setListenerNewAlert(new ListenerNewAlertReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the alertBadge...");
                setOtherBadges();
            }
        });

        // This is the code that is executed once the listener tells us that a new PANews has been created.
        RetrievePANews.setListenerNewPANews(new ListenerNewPANewsReceived() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to update the paNewsBadge...");
                setOtherBadges();
            }
        });

        setOtherBadges();

        docSummary = DefineDocumentSummary.getInstance().getArrayList();
        alertsArray = DefineAlertsArray.getInstance().getArrayList();

        retrievePreviousOrderSummary();

        mListView = (SwipeMenuListView) findViewById(R.id.order_list_view);

        AdapterDocumentSummary adapter = new AdapterDocumentSummary(this, docSummary);

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

                StructureForRetrieveAllDocuments selectedOrder = docSummary.get(position);

//                Intent detailIntent = new Intent(context, PreviousOrderDetailActivity.class);
//
//                detailIntent.putExtra("orderRef", selectedOrder.getOrderRef());
//
//                startActivity(detailIntent);
            }
        });


        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                StructureForRetrieveAllDocuments selectedOrder = docSummary.get(position);
                String recordId = selectedOrder.getRecordId();

                AdapterDocumentSummary adapter = new AdapterDocumentSummary(getApplicationContext(), docSummary);

                switch (index) {
                    case 0:
                        // System.out.println("JUST PRESSED BUTTON 0");
                        System.out.println("Inserting the item in the read list");
                        addReadRecord(recordId);
                        mListView.setAdapter(adapter);
                        alertsArray.add(recordId);
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
                        mListView.setAdapter(adapter);
                        alertsArray.remove(recordId);
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
                        // Toast.makeText(RetrieveDocuments.this, "Alerts", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_news:
                        Intent newsIntent = new Intent(context, RetrieveClassNews.class);
                        startActivity(newsIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveDocuments.this, "News", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_events:
                        Intent eventIntent = new Intent(context, RetrieveEvents.class);
                        startActivity(eventIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveDocuments.this, "Events", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_documents:
//                        finish();
//                        Intent docIntent = new Intent(context, RetrieveDocuments.class);
//                        startActivity(docIntent);
//                        overridePendingTransition(0, 0);
//                        Toast.makeText(RetrieveDocuments.this, "Documents", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_pa:
                        Intent paNewsIntent = new Intent(context, RetrievePANews.class);
                        startActivity(paNewsIntent);
                        finish();
                        // overridePendingTransition(0, 0);
                        // Toast.makeText(RetrieveDocuments.this, "PA News", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_documents);
    }


    private void setListenerForRowTapped(Integer position) {
        if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
            ConnectionDownAlert.connectionDown(this);
            return;
        }

        final Context context = this;
        StructureForRetrieveAllDocuments selectedOrder = docSummary.get(position);

        String recordId = selectedOrder.getRecordId();
        String filename = selectedOrder.getFilename();
        alertsArray.add(recordId);
        addReadRecord(recordId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getBadgeNumber();
            }
        }, 500);

        AdapterDocumentSummary adapter = new AdapterDocumentSummary(this, docSummary);
        mListView.setAdapter(adapter);

        Intent detailDoc = new Intent(context, DetailDocument.class);
        detailDoc.putExtra("recordId", filename);
        startActivity(detailDoc);
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
        // Log.i(TAG, "Starting retrievePreviousOrderSummary");
        docSummary.clear();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNoticesDatabaseReference = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mNoticesDatabaseReference.child("PostDetailsDatabase");

        // query.addListenerForSingleValueEvent(new ValueEventListener() {
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    docSummary.clear();
//                    AlertSummaryAdapter adapter = new AlertSummaryAdapter(getApplicationContext(), alertSummary);
//                    adapter.notifyDataSetChanged();
                    System.out.println("We've just had an update.....");
                    System.out.println("The alertSummary arrayList shoud now be empty: " + docSummary);
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The Documents table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String date, date1, message, timeInterval, recordId, read, filename;
                        Date date2 = new Date();
                        Date date3 = new Date();


                        recordId = alerts.getKey();
                        System.out.println("recordId is: " + recordId);

                        if (alerts.child("subject").exists()) {
                            message = alerts.child("subject").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }


                        if (alerts.child("fileName").exists()) {
                            filename = alerts.child("fileName").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }


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

                            timeInterval = TimeAgoHelper.getTimeAgo(seconds);

                            if (timeInterval.equals("NULL")) {
                                timeInterval = outputDate2.format(date2);
                            }

                        } else {
                            return;
                        }




                        System.out.println("Now adding the record to alertSummary");
                        docSummary.add(new StructureForRetrieveAllDocuments(message, date, recordId, timeInterval, filename));
                        System.out.println("The docSummary arrayList should now have a new entry: " + docSummary);

//                        for (StructureForRetrieveAllAlerts elem_ : alertSummary) {
//                            // System.out.println(elem_.getUserName());
//                        }

                    }
                    System.out.println("Now going to sort the arrayList docSummary");
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

        Collections.sort(docSummary, new Comparator<StructureForRetrieveAllDocuments>() {
            DateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            @Override
            public int compare(StructureForRetrieveAllDocuments t1, StructureForRetrieveAllDocuments structureForRetrieveAllDocuments) {
                try {
                    return f.parse(structureForRetrieveAllDocuments.getDate()).compareTo(f.parse(t1.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        AdapterDocumentSummary adapter = new AdapterDocumentSummary(this, docSummary);
        if (docSummary.size() > 0) {
            mListView.setAdapter(adapter);
        }


    }



    private void getBadgeNumber() {
        docSummary = DefineDocumentSummary.getInstance().getArrayList();
        alertsArray = DefineAlertsArray.getInstance().getArrayList();
        // System.out.println("alertsArray = " + alertsArray.toString());
        final BottomNavigationView navBar = this.findViewById(R.id.bottom_navigation);
        // alertSummary = retrieveAlerts.alertSummary;
        System.out.println("docSummary has " + docSummary.size() + " entries.");
        final Integer n = docSummary.size();

        System.out.println("readAlerts should total " + alertsArray.size() + " entries.");
        Integer unreadCount = 0;
        for (int i = 0; i < n; i++) {
            StructureForRetrieveAllDocuments docs = docSummary.get(i);
            String recordId;
            recordId = docs.getRecordId();
            if (!alertsArray.contains(recordId)) {
                unreadCount = unreadCount + 1;
            }

            if (unreadCount > 0) {
                navBar.showBadge(R.id.action_documents).setNumber(unreadCount);
                GlobalVariables.docCount = unreadCount;
                // ((createGlobalVariables) this.getApplication()).setUnreadAlerts(unreadCount);
                // Here we trigger the listener which lets the other activities know that an event has occurred.
                if(mListener!=null) mListener.onEvent();
            } else {
                navBar.removeBadge(R.id.action_documents);
                GlobalVariables.docCount = unreadCount;
                // ((createGlobalVariables) this.getApplication()).setUnreadAlerts(unreadCount);
                // Here we trigger the listener which lets the other activities know that an event has occurred.
                if(mListener!=null) mListener.onEvent();
            }
        }
    }


    public void populateAlertsArray() {
        alertsArray = DefineAlertsArray.getInstance().getArrayList();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        Query query = mRef.child("MessagesRead").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateNewsBadge() {
        System.out.println("Starting to update the newsBadge...");
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Integer unreadNews = GlobalVariables.newsCount;
        if (unreadNews > 0) {
            navBar.showBadge(R.id.action_news).setNumber(unreadNews);
        } else {
            navBar.removeBadge(R.id.action_news);
        }

    }


    private void setOtherBadges() {
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // Integer unreadNews = ((createGlobalVariables) this.getApplication()).getUnreadNews();

        Integer unreadAlert = GlobalVariables.alertCount;
        if (unreadAlert > 0) {
            navBar.showBadge(R.id.action_alerts).setNumber(unreadAlert);
        } else {
            navBar.removeBadge(R.id.action_alerts);
        }

        Integer unreadNews = GlobalVariables.newsCount;
        if (unreadNews > 0) {
            navBar.showBadge(R.id.action_news).setNumber(unreadNews);
        } else {
            navBar.removeBadge(R.id.action_news);
        }

        Integer unreadEvent = GlobalVariables.eventCount;
        if (unreadEvent > 0) {
            navBar.showBadge(R.id.action_events).setNumber(unreadEvent);
        } else {
            navBar.removeBadge(R.id.action_events);
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
