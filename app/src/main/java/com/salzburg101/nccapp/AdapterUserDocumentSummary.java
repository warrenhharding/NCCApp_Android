package com.salzburg101.nccapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterUserDocumentSummary extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StructureForRetrieveUserUploadedDocuments> mDataSource;

    ArrayList<String> alertsArray = new ArrayList<String>();

    //////////////////////////////////////////////////
    // This is required so that the adapter can read the alertsArray from the ProcessBadges class.
//    public ProcessBadges processBadges;
//    public AlertSummaryAdapter( ProcessBadges processBadges){
//        this.processBadges = processBadges;
//    }

    // ArrayList<String> alertsArray = new ArrayList<String>();
    //////////////////////////////////////////////////



    public AdapterUserDocumentSummary(Context context, ArrayList<StructureForRetrieveUserUploadedDocuments> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_user_uploaded_documents, parent, false);

        System.out.println("Made it into the adapter");

        TextView docType =
                (TextView) rowView.findViewById(R.id.docTypeTextView);

        TextView date =
                (TextView) rowView.findViewById(R.id.order_date_detail);

        TextView student =
                (TextView) rowView.findViewById(R.id.student);

        TextView filename =
                (TextView) rowView.findViewById(R.id.filename);



        // 1
        StructureForRetrieveUserUploadedDocuments alert = (StructureForRetrieveUserUploadedDocuments) getItem(position);
        String recordId;
        recordId = alert.getRecordId();


// 2
        docType.setText(alert.getMessage());
        System.out.println("docType = " + alert.getMessage());
        date.setText(alert.getDate());
        System.out.println("date = " + alert.getDate());
        student.setText(alert.getStudent());
        System.out.println("student = " + alert.getStudent());
        filename.setText(alert.getFilename());
        System.out.println("fileName = " + alert.getFilename());

        docType.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        date.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        student.setTextColor(mContext.getResources().getColor(R.color.blackColor));
        filename.setTextColor(mContext.getResources().getColor(R.color.blackColor));



//        if (readStatus.equals(false)) {
//            rowView.setBackgroundColor(mContext.getResources().getColor(R.color.unreadMessages));
//            titleTextView.setTypeface(null, Typeface.BOLD);
//            titleTextView.setTextColor(mContext.getResources().getColor(R.color.blackColor));
//        }



        return rowView;
    }



}
