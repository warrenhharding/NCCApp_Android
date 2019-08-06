package com.salzburg101.nccapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;


public class AddPaymentDetailsScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddPaymentDetails";

    // private static final String TAG = "AddPaymentDetails";
    private ProgressBar spinner;
    public static ListenerPaymentDetailsAdded mListener;


    CardInputWidget mCardInputWidget;
    public static Card cardToSave;
    String tokenString;
    Boolean retryBool;

    public static void setCustomEventListener(ListenerPaymentDetailsAdded eventListener) {
        mListener = eventListener;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Made it to the Payments Screen...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_details_screen);

        Boolean retryBool = getIntent().getBooleanExtra("retryBool", false);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button addDetailsButton = (Button) findViewById(R.id.addDetailsButton);

        // Card cardToSave = new Card("4242-4242-4242-4242", 12, 2020, "123");

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.addDetailsButton).setOnClickListener(this);

        NetworkAsyncTask.setPaymentEventListener(new ListenerAddPaymentDetailsEvent(){
            public void onEvent(String result){
                // Log.i(TAG, "OK - so the listener seems to be working now....");
                if (result.equals("success")) {
                    detailsSuccessfulDialog();
                }

                if (result.equals("failed")) {
                    cardDeclined();
                }

            }
        });


        if (!retryBool) {
            detailsWillBeOverwrittenDialog();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.cancelButton) {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            finish();
        }

        if (i == R.id.addDetailsButton) {
            hideSoftKeyBoard();
            disableScreen();
            spinner.setVisibility(View.VISIBLE);
            getCardDetails();
        }


    }



    private void getCardDetails() {

        final GetCustomerDetails currentCustomer = GetCustomerDetails.getInstance();
        // Stripe stripe = new Stripe(this, "pk_test_JYB2m76Hk0OhwyjD06oURdKE"); // Test Key
        Stripe stripe = new Stripe(this, "pk_test_LvYE3jChqV3Zf7Wkffxr7Aa700yABDsZTm"); // Test Key

        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        if (mCardInputWidget.getCard() == null) {
            // Toast.makeText(this, "The data entered is invalid", Toast.LENGTH_LONG).show();
            // spinner.setVisibility(View.GONE);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Invalid Details");
            builder1.setMessage("Please check your credit card details again.");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // spinner.setVisibility(View.GONE);
                        getIntent().putExtra("retryBool", true);
                        startActivity(getIntent());
                        finish();
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                    });
            AlertDialog paymentDetailsAlert = builder1.create();
            paymentDetailsAlert.show();

            return;

        }

        cardToSave = mCardInputWidget.getCard();
        GlobalVariables.paymentInProgress = true;
        System.out.println("cardToSave = " + cardToSave.getLast4());

        finish();
    }




    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }



    private void detailsWillBeOverwrittenDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Payment Details");
        builder1.setMessage("Please note that any existing payment details will be overwritten.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });


        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        finish();
                    }
                });

        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.setCanceledOnTouchOutside(false);
        paymentDetailsAlert.show();
    }



    private void noTokenErrorMessage() {
        retryBool = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        enableScreen();
        builder1.setTitle("Error");
        builder1.setMessage("We're having trouble storing your payment details. Would you like to try again now or come back later?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "I'll Try Again",
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    spinner.setVisibility(View.GONE);
                    getIntent().putExtra("retryBool", true);
                    startActivity(getIntent());
                    finish();
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                });


        builder1.setNegativeButton(
                "I'll Come Back Later",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        finish();
                    }
                });

        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.setCanceledOnTouchOutside(false);
        paymentDetailsAlert.show();
    }



    private void cardDeclined() {
        retryBool = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        enableScreen();
        builder1.setTitle("Card Declined");
        builder1.setMessage("Your card has been declined." + "\n\n" + "Please check with your bank for further information." + "\n\n"
                + "You can continue to use the app, but payment details will be required before you can process a transaction.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    spinner.setVisibility(View.GONE);
                    finish();
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
                });
        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.setCanceledOnTouchOutside(false);
        paymentDetailsAlert.show();
    }



    private void detailsSuccessfulDialog() {
        spinner.setVisibility(View.GONE);
        enableScreen();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Update Successful");
        builder1.setMessage("Your payment details have now been updated.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        if (mListener != null) {
                            mListener.onEvent();
                        }

                    }
                });

        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.setCanceledOnTouchOutside(false);
        paymentDetailsAlert.show();
    }



    private void disableScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }



    private void enableScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }




}
