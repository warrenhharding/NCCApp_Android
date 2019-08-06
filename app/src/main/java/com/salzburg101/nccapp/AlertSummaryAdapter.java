package com.salzburg101.nccapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AlertSummaryAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveAllAlerts> mDataSource;

    ArrayList<String> alertsArray = new ArrayList<String>();

    //////////////////////////////////////////////////
    // This is required so that the adapter can read the alertsArray from the ProcessBadges class.
//    public ProcessBadges processBadges;
//    public AlertSummaryAdapter( ProcessBadges processBadges){
//        this.processBadges = processBadges;
//    }

    // ArrayList<String> alertsArray = new ArrayList<String>();
    //////////////////////////////////////////////////



    public AlertSummaryAdapter(Context context, ArrayList<StructureForRetrieveAllAlerts> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_alerts, parent, false);

        TextView titleTextView =
                (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.message);

        TextView subtitleTextView =
                (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.order_date_detail);

        TextView detailTextView =
                (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.userName);



        // 1
        StructureForRetrieveAllAlerts alert = (StructureForRetrieveAllAlerts) getItem(position);
        String recordId;
        recordId = alert.getRecordId();

        Boolean readStatus = false;
        // SharedPreferences sp = mContext.getSharedPreferences("readAlerts", Context.MODE_PRIVATE);
        // if (sp.contains(recordId)) {
        //     readStatus = true;
        // }

        System.out.println("About to find the alertsArray");

//        if (processBadges.alertsArray == null) {
//            ArrayList<String> list = new ArrayList<String>();
//        } else {
//            ArrayList<String> alertsArray = this.processBadges.alertsArray;
//        }


        alertsArray = DefineAlertsArray.getInstance().getArrayList();

        if (alertsArray.contains(recordId)) {
            readStatus = true;
        }


// 2
        titleTextView.setText(alert.getUserName());
        subtitleTextView.setText(alert.getTimeInterval());
        detailTextView.setText(alert.getMessage());



        if (readStatus.equals(false)) {
            rowView.setBackgroundColor(mContext.getResources().getColor(R.color.unreadMessages));
            titleTextView.setTypeface(null, Typeface.BOLD);
            detailTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
            detailTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        }



        return rowView;
    }



}
