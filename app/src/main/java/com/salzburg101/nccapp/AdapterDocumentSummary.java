package com.salzburg101.nccapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterDocumentSummary extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveAllDocuments> mDataSource;

    ArrayList<String> alertsArray = new ArrayList<String>();

    //////////////////////////////////////////////////
    // This is required so that the adapter can read the alertsArray from the ProcessBadges class.
//    public ProcessBadges processBadges;
//    public AlertSummaryAdapter( ProcessBadges processBadges){
//        this.processBadges = processBadges;
//    }

    // ArrayList<String> alertsArray = new ArrayList<String>();
    //////////////////////////////////////////////////



    public AdapterDocumentSummary(Context context, ArrayList<StructureForRetrieveAllDocuments> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_documents, parent, false);

        TextView titleTextView =
                (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.message);

        TextView subtitleTextView =
                (TextView) rowView.findViewById(com.salzburg101.nccapp.R.id.order_date_detail);



        // 1
        StructureForRetrieveAllDocuments alert = (StructureForRetrieveAllDocuments) getItem(position);
        String recordId;
        recordId = alert.getRecordId();

        Boolean readStatus = false;

        System.out.println("About to find the alertsArray");

        alertsArray = DefineAlertsArray.getInstance().getArrayList();

        if (alertsArray.contains(recordId)) {
            readStatus = true;
        }


// 2
        // titleTextView.setText(alert.getUserName());
        subtitleTextView.setText(alert.getTimeInterval());
        titleTextView.setText(alert.getMessage());



        if (readStatus.equals(false)) {
            rowView.setBackgroundColor(mContext.getResources().getColor(R.color.unreadMessages));
            titleTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        }



        return rowView;
    }



}
