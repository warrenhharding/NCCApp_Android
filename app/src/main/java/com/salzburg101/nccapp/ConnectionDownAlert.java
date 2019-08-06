package com.salzburg101.nccapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConnectionDownAlert {

    public static void connectionDown(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("We're Having a Problem");
        builder1.setMessage("It looks like we can't communicate with our database. Please check your connection and try again.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.show();
    }

}
