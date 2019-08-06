package com.salzburg101.nccapp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkAsyncTask extends AsyncTask<Void, Void, Void> {

    // private static final String TAG = "NetworkAsyncTask";

    private static ListenerAddPaymentDetailsEvent mListener;

    String result;


    public static void setPaymentEventListener(ListenerAddPaymentDetailsEvent eventListener) {
        mListener = eventListener;
    }



    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Log.i(TAG, "About to define the url");
            // URL url = new URL("https://us-central1-sunny-9c024.cloudfunctions.net/attachCardToCustomer");
            URL url = new URL("https://us-central1-ncc-pa-app.cloudfunctions.net/attachCardToCustomer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            // Log.i(TAG, "Have set the content type");

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("cardTokenId", GetCustomerDetails.getInstance().getTokenString());
            jsonParam.put("stripeCustomerId", GetCustomerDetails.getInstance().getStripeId());

            // Log.i(TAG, "Have set the paramaters");


            // Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            // Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            // Log.i("MSG", conn.getResponseMessage());

            if (String.valueOf(conn.getResponseCode()).equals("200")) {
                result = "success";
                GetCustomerDetails currentCustomer = GetCustomerDetails.getInstance();
                currentCustomer.setCustomerValidPaymentDetails(true);
            } else {
                result = "failed";
                // Log.i(TAG, "Completion has failed...");
            }

            conn.disconnect();

        }

        catch (Exception e) {
            // Log.i(TAG, "It doesn't seem to have worked then!!!");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // if (result.equals("success")) {
        this.mListener.onEvent(result);
        // }
    }






}
