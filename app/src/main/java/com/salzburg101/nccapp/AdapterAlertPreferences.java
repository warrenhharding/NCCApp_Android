package com.salzburg101.nccapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterAlertPreferences extends BaseAdapter {

    private static final String TAG = "RetrieveAlertPrefs";

    private FirebaseAuth mAuth;

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveAlertPreferences> mDataSource;

    ArrayList<String> alertsArray = new ArrayList<String>();

    //////////////////////////////////////////////////
    // This is required so that the adapter can read the alertsArray from the ProcessBadges class.
//    public ProcessBadges processBadges;
//    public AlertSummaryAdapter( ProcessBadges processBadges){
//        this.processBadges = processBadges;
//    }

    // ArrayList<String> alertsArray = new ArrayList<String>();
    //////////////////////////////////////////////////



    public AdapterAlertPreferences(Context context, ArrayList<StructureForRetrieveAlertPreferences> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2

    // This @Override needed to be entered to stop rows appearing twice
    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    // This @Override needed to be entered to stop rows appearing twice
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        System.out.println("Deep inside the adapter");
        View rowView = mInflater.inflate(R.layout.list_item_alert_prefs, parent, false);

        TextView topic = (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.topic);

        TextView topicDescription = (TextView) rowView.findViewById(R.id.topicDescription);

        final Switch simpleSwitch = (Switch) rowView.findViewById(R.id.simpleSwitch);

        final StructureForRetrieveAlertPreferences alert = (StructureForRetrieveAlertPreferences) getItem(position);


        topic.setText(alert.getTopic());
        topicDescription.setText(alert.getTopicDescription());


        System.out.println("Currently working wiht the: " + alert.getTopicCode());
        System.out.println("codesSubscribedTo : " + RetrieveAlertPreferences.codesSubscribedTo.size());
        // System.out.println("codesSubscribedTo : " + RetrieveAlertPreferences.codesSubscribedTo.get(0));

        if (RetrieveAlertPreferences.codesSubscribedTo.contains(alert.getTopicCode())) {
            simpleSwitch.setChecked(true);
            System.out.println(alert.getTopicCode() + " is in the codesSubscribedTo array");
        } else {
            System.out.println(alert.getTopicCode() + " is NOT in the codesSubscribedTo array");
        }


        topic.setTypeface(null, Typeface.BOLD);
        topic.setTextColor(mContext.getResources().getColor(R.color.blackColor));

        topicDescription.setTypeface(null, Typeface.BOLD);
        topicDescription.setTextColor(mContext.getResources().getColor(R.color.blackColor));





        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Date nowDate = new Date();
            SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000");
            String dateString = outputDate.format(nowDate);

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch.isChecked()) {
                    System.out.println("Switch for " + alert.getTopic() + "has been set to ON");
                    // Was OFF and now set to ON
                    // Update the local array
                    simpleSwitch.setChecked(true);
                    RetrieveAlertPreferences.codesSubscribedTo.add(alert.getTopicCode());

                    // Send the subscription to Firebase Cloud Messaging
                    FirebaseMessaging.getInstance().subscribeToTopic(alert.getTopicCode())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Now subscribed to the topic";
                                    System.out.println("About to call dialog for signup for alerts for: " + alert.getTopic());

                                    if (!task.isSuccessful()) {
                                        System.out.println("About to call dialog for signup for alerts for: " + alert.getTopic());
                                    }
                                }
                            });

                    // Write the subscription to the Subscriptions database
                    FirebaseDatabase database =  FirebaseDatabase.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user =  mAuth.getCurrentUser();
                    String userId = user.getUid();
                    DatabaseReference mRef =  database.getReference().child("Subscriptions").child(userId).push();
                    mRef.child("date").setValue(dateString);
                    mRef.child("topic").setValue(alert.getTopicCode());

                    DatabaseReference logRef = database.getReference().child("SubsLog").push();
                    logRef.child("subscriber").setValue(userId);
                    logRef.child("action").setValue("Sub");
                    logRef.child("topic").setValue(alert.getTopicCode());
                    logRef.child("date").setValue(dateString);


                } else {
                    System.out.println("Switch for " + alert.getTopic() + "has been set to OFF");
                    // Was ON and now set to OFF
                    simpleSwitch.setChecked(false);
                    RetrieveAlertPreferences.codesSubscribedTo.remove(alert.getTopic());

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(alert.getTopicCode())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Now subscribed to the topic";
                                    System.out.println("About to call dialog for signup for alerts for: " + alert.getTopic());

                                    if (!task.isSuccessful()) {
                                        System.out.println("About to call dialog for signup for alerts for: " + alert.getTopic());
                                    }
                                }
                            });


                    FirebaseDatabase database =  FirebaseDatabase.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user =  mAuth.getCurrentUser();
                    String userId = user.getUid();
                    DatabaseReference mRef =  database.getReference().child("Subscriptions").child(userId);

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot subscription: dataSnapshot.getChildren()) {
                                String topic, recordId;

                                recordId = subscription.getKey();

                                if (subscription.child("topic").exists()) {
                                    topic = subscription.child("topic").getValue().toString();
                                } else {
                                    topic = "";
                                }

                                if (topic.equals(alert.getTopicCode())) {
                                    subscription.getRef().removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });

                    DatabaseReference logRef = database.getReference().child("SubsLog").push();
                    logRef.child("subscriber").setValue(userId);
                    logRef.child("action").setValue("Unsub");
                    logRef.child("topic").setValue(alert.getTopicCode());
                    logRef.child("date").setValue(dateString);

                }
            }
        });








        return rowView;
    }



}
