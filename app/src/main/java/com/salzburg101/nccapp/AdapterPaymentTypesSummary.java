package com.salzburg101.nccapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterPaymentTypesSummary extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveAllPaymentTypes> mDataSource;


    public AdapterPaymentTypesSummary(Context context, ArrayList<StructureForRetrieveAllPaymentTypes> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_paymenttypes, parent, false);

        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.chargeName);

        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.chargeAmount);

        TextView detailTextView =
                (TextView) rowView.findViewById(R.id.chargeDescription);




        // 1
        StructureForRetrieveAllPaymentTypes payment = (StructureForRetrieveAllPaymentTypes) getItem(position);


// 2
        titleTextView.setText(payment.getChargeName());
        subtitleTextView.setText(payment.getChargeAmount());
        detailTextView.setText(payment.getChargeDescription());


        rowView.setBackgroundColor(mContext.getResources().getColor(R.color.unreadMessages));
        titleTextView.setTypeface(null, Typeface.BOLD);
        subtitleTextView.setTypeface(null, Typeface.BOLD);
        detailTextView.setTypeface(null, Typeface.BOLD);

        titleTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        subtitleTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        detailTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));

        return rowView;
    }



}
