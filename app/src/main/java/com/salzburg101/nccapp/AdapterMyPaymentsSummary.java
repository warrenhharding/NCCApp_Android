package com.salzburg101.nccapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterMyPaymentsSummary extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveMyPayments> mDataSource;

    ArrayList<String> alertsArray = new ArrayList<String>();

    //////////////////////////////////////////////////
    // This is required so that the adapter can read the alertsArray from the ProcessBadges class.
//    public ProcessBadges processBadges;
//    public AlertSummaryAdapter( ProcessBadges processBadges){
//        this.processBadges = processBadges;
//    }

    // ArrayList<String> alertsArray = new ArrayList<String>();
    //////////////////////////////////////////////////



    public AdapterMyPaymentsSummary(Context context, ArrayList<StructureForRetrieveMyPayments> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_mypayments, parent, false);

        System.out.println("Made it into the adapter");

        TextView chargeDescription =
                (TextView) rowView.findViewById(R.id.chargeDescription);

        TextView date =
                (TextView) rowView.findViewById(R.id.order_date_detail);

        TextView amount =
                (TextView) rowView.findViewById(R.id.amount);



        // 1
        StructureForRetrieveMyPayments alert = (StructureForRetrieveMyPayments) getItem(position);
        String recordId;
        recordId = alert.getRecordId();


// 2
        chargeDescription.setText(alert.getChargeDescription());
        System.out.println("chargeDescription = " + alert.getChargeDescription());
        date.setText(alert.getDate());
        System.out.println("date = " + alert.getDate());
        amount.setText(alert.getAmount());


        chargeDescription.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        date.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        amount.setTextColor(mContext.getResources().getColor(R.color.blackColor));

        return rowView;
    }



}
