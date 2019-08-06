package com.salzburg101.nccapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class RetrievePaymentTypes extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "RetrievePaymentTypes";

    private Stripe mStripe;

    ArrayList<StructureForRetrieveAllPaymentTypes> paymentTypesSummary = new ArrayList<StructureForRetrieveAllPaymentTypes>();

    ArrayList<String> students = new ArrayList<String>();
    String[] studentsArray;

    String selectedStudents = "";
    public static String clientSecret = "";
    String paymentMethodId = "";
    String paymentMethodForSaving = "";
    String amountToCharge = "";
    String chargeName = "";
    String reasonForCharge = "";
    String stripeCustomerId = "";


    Boolean paymentDetailsExist;

    TextView selectStudentsTextView;

    private ProgressBar spinner;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_all_payment_types);

        HelperGetStudents.getStudents();

        PaymentConfiguration.init("pk_test_LvYE3jChqV3Zf7Wkffxr7Aa700yABDsZTm");
        mStripe = new Stripe(this,
                PaymentConfiguration.getInstance().getPublishableKey());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // checkPaymentDetails();
        getStudents();
        retrievePreviousOrderSummary();

        mListView = (ListView) findViewById(R.id.order_list_view);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        AdapterPaymentTypesSummary adapter = new AdapterPaymentTypesSummary(this, paymentTypesSummary);

        findViewById(R.id.menuButton).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        // findViewById(R.id.largeQButton).setOnClickListener(this);

        // This is required so that ???
        final Context context = this;



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setListenerForRowTapped(position);

                StructureForRetrieveAllPaymentTypes selectedOrder = paymentTypesSummary.get(position);
                String amount = selectedOrder.getChargeAmountNumbers();
                System.out.println("The amount to be charged is: " + amount);
                Double amtToCharge = (Double.parseDouble(amount)) * 100;
                System.out.println("The real amount to be charged is: " + amtToCharge);
                int amountInt = (int) Math.rint(amtToCharge);
                System.out.println("The integer amount to be charged is: " + amountInt);
                amountToCharge = Integer.toString(amountInt);

                chargeName = selectedOrder.getChargeName();
                reasonForCharge = selectedOrder.getChargeDescription();



//                Intent detailIntent = new Intent(context, PreviousOrderDetailActivity.class);
//
//                detailIntent.putExtra("orderRef", selectedOrder.getOrderRef());
//
//                startActivity(detailIntent);
            }
        });



    }

    @Override
    public void onResume(){
        super.onResume();
        if (GlobalVariables.paymentInProgress == true) {
            GlobalVariables.paymentInProgress = false;
            getPaymentMethodId();
        }


    }


    private void setListenerForRowTapped(Integer position) {
        if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
            ConnectionDownAlert.connectionDown(this);
            return;
        }

        selectStudents();

//        final Context context = this;
//        StructureForRetrieveAllPaymentTypes selectedOrder = paymentTypesSummary.get(position);
//
//        String recordId = selectedOrder.getRecordId();
//
//        AdapterPANewsSummary adapter = new AdapterPANewsSummary(this, paNewsSummary);
//        mListView.setAdapter(adapter);
//
//        Intent detailIntent = new Intent(context, DetailPANews.class);
//        detailIntent.putExtra("recordId", recordId);
//        finish();
//        startActivity(detailIntent);
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent expandedIntent = new Intent(this, ExpandedMenu.class);
            startActivity(expandedIntent);
            finish();
        }

        if (i == R.id.cancelButton) {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            finish();
        }

        if (i == R.id.addDetailsButton) {
            hideSoftKeyBoard();
            disableScreen();
            spinner.setVisibility(View.VISIBLE);
            // getCardDetails();
        }
    }



    public void retrievePreviousOrderSummary() {
        // Log.i(TAG, "Starting retrievePreviousOrderSummary");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("Charges");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
        // query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    paymentTypesSummary.clear();
                    System.out.println("The payment Types Summary arrayList shoud now be empty: " + paymentTypesSummary);
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The Payments table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String chargeName, chargeDescription, chargeAmount, chargeTopic, chargeAmountNumbers;
                        Boolean includeLocalCharges;


                        if (alerts.child("chargeName").exists()) {
                            chargeName = alerts.child("chargeName").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }

                        if (alerts.child("chargeDescription").exists()) {
                            chargeDescription = alerts.child("chargeDescription").getValue().toString();
                        } else {
                            return;
                        }


                        if (alerts.child("includeLocalCharges").exists()) {
                            includeLocalCharges = alerts.child("includeLocalCharges").getValue(Boolean.class);
                        } else {
                            return;
                        }


                        if (alerts.child("chargeAmount").exists()) {
                            chargeAmount = alerts.child("chargeAmount").getValue().toString();
                            chargeAmountNumbers = alerts.child("chargeAmount").getValue().toString();
                        } else {
                            return;
                        }


                        Double charge;

                        if (includeLocalCharges == true) {
                            charge = Double.parseDouble(chargeAmount);
                        } else {
                            charge = (((Double.parseDouble(chargeAmount) / 98.8) * 100) + 0.25);
                            chargeAmountNumbers = Double.toString(charge);
                        }


                        chargeAmount = String.format("%.2f", charge);
                        chargeAmount = "€" + chargeAmount;

                        if (alerts.child("chargeTopic").exists()) {
                            chargeTopic = alerts.child("chargeTopic").getValue().toString();
                        } else {
                            return;
                        }



                        System.out.println("Now adding the record to paymentsTypeSummary");
                        paymentTypesSummary.add(new StructureForRetrieveAllPaymentTypes(chargeName, chargeDescription, chargeAmount, includeLocalCharges, chargeTopic, chargeAmountNumbers));
                        System.out.println("The alertSummary arrayList should now have a new entry: " + paymentTypesSummary);

//                        for (StructureForRetrieveAllAlerts elem_ : alertSummary) {
//                            // System.out.println(elem_.getUserName());
//                        }

                    }
                    System.out.println("Now going to sort the arrayList alertSummary");
                    sortArrayList();

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
        System.out.println("Received the list and now sending it to adapter.");

        AdapterPaymentTypesSummary adapter = new AdapterPaymentTypesSummary(this, paymentTypesSummary);
        if (paymentTypesSummary.size() > 0) {
            mListView.setAdapter(adapter);
        }

    }


    private void getStudents() {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        // Query query = mNoticesDatabaseReference.child("ComplexOrders").orderByChild("customer").equalTo(mAuth.getCurrentUser().getEmail());
        Query query = mRef.child("MyStudents/" + mAuth.getCurrentUser().getUid() );

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    students.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The students table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String student, classroom, combine;


                        if (alerts.child("student").exists()) {
                            student = alerts.child("student").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }

                        if (alerts.child("classroom").exists()) {
                            classroom = alerts.child("classroom").getValue().toString();
                        } else {
                            return;
                        }

                        combine = student + " - " + classroom;

                        System.out.println("Now adding the record to students");
                        students.add(combine);
                        System.out.println("The students arrayList should now have a new entry: " + paymentTypesSummary);
                    }

                } else {
                    // TODO
                    return;
                }
                String[] studentsForArray = students.toArray(new String[students.size()]);
                for(String s : studentsForArray){
                    System.out.println(s);
                }
                studentsArray = studentsForArray;
                System.out.println(studentsForArray.toString());
                // checkPaymentDetails();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });

    }


    private void selectStudents() {
        selectedStudents = "";
        // selectStudentsTextView.setText("No students selected.");

        final ArrayList checked = new ArrayList<Integer>();
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select Students");
        // Add a checkbox list


        boolean[] checkedItems = new boolean[HelperGetStudents.students.size()];
        Arrays.fill(checkedItems, false);

        // boolean[] checkedItems = {true, false, false};
        builder.setMultiChoiceItems(HelperGetStudents.studentsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // The user checked or unchecked a box
                if (isChecked) {
                    // if the user checked the item, add it to the selected items
                    checked.add(which);
                    System.out.println("checked now included " + checked.toString());
                }
                else if (checked.contains(which)) {
                    // else if the item is already in the array, remove it
                    checked.remove(Integer.valueOf(which));
                    System.out.println("checked now included " + checked.toString());
                }
            }
        });
        // Add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user clicked OK
                if (checked.size() == 0) {
                    dialog.dismiss();
                    mustSelectStudentAlert();
                    return;
                }
                selectedStudents = "";
                for(int i=0; i<checked.size(); i++) {
                    Integer y = (Integer) checked.get(i);
                    String x = HelperGetStudents.studentsArray[y].toString();

                    selectedStudents = selectedStudents + x + " / ";
                    System.out.println("selectedStudents = " + selectedStudents);

                }
                selectedStudents = selectedStudents.substring(0, selectedStudents.length() - 3);
                checkPaymentDetails();
                // selectStudentsTextView.setText(selectedStudents);
            }
        });
        builder.setNegativeButton("Cancel", null);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void checkPaymentDetails() {
        String stripeCustExists = GetCustomerDetails.getInstance().getStripeId();
        String paymentMethodExists = GetCustomerDetails.getInstance().getStripePaymentMethodId();

        if (stripeCustExists != "" && paymentMethodExists != "" && stripeCustExists != null && paymentMethodExists != null) {
            paymentDetailsExist = true;
            System.out.println("paymentDetailsAvailable is true - calling nothing further now...");
            existingOrNewDetails();
        } else {
            paymentDetailsExist = false;
            if (stripeCustExists != "" && stripeCustExists != null) {
                System.out.println("About to call clientSecret");
                whichPaymentDetails();
            } else {
                System.out.println("Not calling anything...");
            }
        }
        System.out.println("Customer has payment details set-up is " + paymentDetailsExist);
    }


    // Start processing with new card with the intention of storing it.
    private void getClientSecret() {
        System.out.println("Starting getClientSecret - save details for future use.");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("amount", amountToCharge);
            paramJson.put("currency", "eur");
            paramJson.put("setupFutureUsage", "on_session");
        } catch (JSONException e) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-ncc-pa-app.cloudfunctions.net/createStripePaymentIntent",paramJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "The response to this Stripe customer creation request was " + response.toString());
                        String json = response.toString();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        try {
                            clientSecret = jsonObj.getString("client_secret");
                            System.out.println("clientSecret = " + clientSecret);
                            Intent addPayment = new Intent(RetrievePaymentTypes.this, AddPaymentDetailsScreen.class);
                            addPayment.putExtra("retryBool", true);
                            startActivity(addPayment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i(TAG, "The NetworkResponse was " + error.networkResponse.toString());
                        int status = 200;
                        status = networkResponse.statusCode;
                        Log.d(TAG, "The status code for this Stripe customer creation request was :" + status);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    // Start processing with a card which has already been authenticated and saved.
    private void getClientSecretExistingCustomer() {
        System.out.println("Starting getClientSecretExistingCustomer - using previously saved details.");
        String stripeCustomer = GetCustomerDetails.getInstance().getStripeId();
        System.out.println("stripeCustomer (inside existingcustomer) = " + stripeCustomer);
        String uid = mAuth.getUid();
        final String paymentMethodId = GetCustomerDetails.getInstance().getStripePaymentMethodId();


        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("amount", amountToCharge);
            paramJson.put("currency", "eur");
            paramJson.put("stripeCustomer", GetCustomerDetails.getInstance().getStripeId());
            paramJson.put("payment_method", paymentMethodId);

        } catch (JSONException e) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-ncc-pa-app.cloudfunctions.net/createStripePaymentIntentExistingMethod",paramJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "The response to this Stripe customer creation request was " + response.toString());
                        String json = response.toString();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        try {
                            clientSecret = jsonObj.getString("client_secret");
                            System.out.println("clientSecret = " + clientSecret);
                            confirmPayment(paymentMethodId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i(TAG, "The NetworkResponse was " + error.networkResponse.toString());
                        int status = 200;
                        status = networkResponse.statusCode;
                        Log.d(TAG, "The status code for this Stripe customer creation request was :" + status);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    // Start processing wiht a card with no intention of storing it.
    private void getClientSecretSingleTransaction() {
        System.out.println("Starting getClientSecretSingleTransaction");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("amount", amountToCharge);
            paramJson.put("currency", "eur");
        } catch (JSONException e) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-ncc-pa-app.cloudfunctions.net/createStripePaymentIntent",paramJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "The response to this Stripe customer creation request was " + response.toString());
                        String json = response.toString();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        try {
                            clientSecret = jsonObj.getString("client_secret");
                            System.out.println("clientSecret = " + clientSecret);
                            Intent addPayment = new Intent(RetrievePaymentTypes.this, AddPaymentDetailsScreen.class);
                            addPayment.putExtra("retryBool", true);
                            startActivity(addPayment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i(TAG, "The NetworkResponse was " + error.networkResponse.toString());
                        int status = 200;
                        status = networkResponse.statusCode;
                        Log.d(TAG, "The status code for this Stripe customer creation request was :" + status);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    // If a new card is required, then decide whether it's a once off transaction or the card is to be stored.
    private void whichPaymentDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("We'll need your credit / debit card details to proceed. Once you enter your payment details, the charge will be made to your card.\\n\\n \" +\n" +
                "//                \"If you would like us to save your details for future use, please select the 'Save Details for Future Use' option below, otherwise select 'Just This Payment'");
//        builder.setMessage("We'll need your credit / debit card details to proceed. Once you enter your payment details, the charge will be made to your card.\n\n " +
//                "If you would like us to save your details for future use, please select the 'Save Details for Future Use' option below, otherwise select 'Just This Payment'");
//        builder.setCancelable(true);
        builder.setItems(new CharSequence[]
                        {"Just This Payment", "Save Details for Future Use", "Cancel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                spinner.setVisibility(View.VISIBLE);
                                getClientSecretSingleTransaction();
                                break;
                            case 1:
                                spinner.setVisibility(View.VISIBLE);
                                getClientSecret();
                                break;
                            case 2:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    // If we already have a stored card, then decide whether to use this one or a new one.
    private void existingOrNewDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("We can use your stored card details or you can use a different card. Please select below.");
//        builder.setMessage("We'll need your credit / debit card details to proceed. Once you enter your payment details, the charge will be made to your card.\n\n " +
//                "If you would like us to save your details for future use, please select the 'Save Details for Future Use' option below, otherwise select 'Just This Payment'");
//        builder.setCancelable(true);
        builder.setItems(new CharSequence[]
                        {"Existing Card", "New Card", "Cancel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                spinner.setVisibility(View.VISIBLE);
                                getClientSecretExistingCustomer();
                                break;
                            case 1:
                                whichPaymentDetails();
                                break;
                            case 2:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void addPaymentDetails() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Payment Details");
        builder1.setMessage("We'll need your credit / debit card details to proceed. Once you enter your payment details, the charge will be made to your card.\n\n " +
                "If you would like us to save your details for future use, please select the 'Save Details for Future Use' option below, otherwise select 'Just This Payment'");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Just This Payment",
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();

                }
                });

        builder1.setPositiveButton(
                "Save Details for Future Use",
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    getClientSecret();
//                    Intent addPayment = new Intent(RetrievePaymentTypes.this, AddPaymentDetailsScreen.class);
//                    addPayment.putExtra("retryBool", true);
//
//                    startActivity(addPayment);
//
//                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    AddPaymentDetailsScreen.setCustomEventListener(new ListenerPaymentDetailsAdded(){
//                        public void onEvent(){
//                            Log.d(TAG, "Looks like we have the payment details");
//                            Intent mainMenuScreen = new Intent(RetrievePaymentTypes.this, RetrieveAlerts.class);
//                            startActivity(mainMenuScreen);
//                            finish();
//                        }
//                    });
                }
                });


        AlertDialog paymentDetailsAlert = builder1.create();
        paymentDetailsAlert.show();
    }


    private void getPaymentMethodId() {
        PaymentMethodCreateParams.Card paymentMethodParamsCard = AddPaymentDetailsScreen.cardToSave.toPaymentMethodParamsCard();
        PaymentMethodCreateParams cardPaymentMethodCreateParams = PaymentMethodCreateParams.create(paymentMethodParamsCard, null);
        mStripe.createPaymentMethod(cardPaymentMethodCreateParams,
                new ApiResultCallback<PaymentMethod>() {
                    @Override
                    public void onSuccess(@NonNull PaymentMethod paymentMethod) {
                        System.out.println("paymentMerthodCreateParams = " + paymentMethod.toString());
                        paymentMethodId = paymentMethod.id;
                        System.out.println("The paymentMethodId first found is " + paymentMethodId);
                        confirmPayment(paymentMethodId);

                    }
                    @Override
                    public void onError(@NonNull Exception e) {
                    }
                });
    }


    private void confirmPayment(@NonNull String paymentMethodId) {
        System.out.println("Into confirm Payment now...");
        final ConfirmPaymentIntentParams confirmPaymentIntentParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(paymentMethodId, clientSecret);
        // Optional: customize the payment authentication experience
        final PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization = new PaymentAuthConfig.Stripe3ds2UiCustomization.Builder()
                        .build();
        PaymentAuthConfig.init(new PaymentAuthConfig.Builder()
                .set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder()
                        // set a 5 minute timeout for challenge flow
                        .setTimeout(5)
                        // customize the UI of the challenge flow
                        .setUiCustomization(uiCustomization)
                        .build())
                .build());

        mStripe.confirmPayment(this, confirmPaymentIntentParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Into onActivityResult now... ");
        System.out.println("resultCode = " + resultCode);
        mStripe.onPaymentResult(requestCode, data,
                new ApiResultCallback<PaymentIntentResult>() {
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        // If confirmation and authentication succeeded,
                        // the PaymentIntent will have user actions resolved;
                        // otherwise, handle the failure as appropriate
                        // (e.g. the customer may need to choose a new payment
                        // method)
                        final PaymentIntent paymentIntent = result.getIntent();
                        final PaymentIntent.Status status = paymentIntent.getStatus();
                        System.out.println("Status back out is: " + status);
                        System.out.println("Intent is: " + paymentIntent.toString());


                        if (status == PaymentIntent.Status.Succeeded) {
                            System.out.println("The payment was successfully processed");
                            paymentMethodForSaving = paymentIntent.getId();
                            System.out.println("paymentMethodId for saving = " + paymentMethodForSaving);
                            addPaymentMethodToStripeCustomer();
                            paymentSuccessful();

                            System.out.println("About to start writing the payment to Firebase...");

                            Date nowDate = new Date();
                            SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000");
                            String dateString = outputDate.format(nowDate);

                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            mRef = mFirebaseDatabase.getReference();

                            StructureForSuccessfulPayment payment = new StructureForSuccessfulPayment(amountToCharge, chargeName, "EUR", dateString, selectedStudents,
                                    mAuth.getCurrentUser().getEmail(), reasonForCharge, GetCustomerDetails.getInstance().getStripeId()) ;
                            DatabaseReference paymentRef = mRef.child("Payments").child(mAuth.getCurrentUser().getUid());
                            System.out.println("Writing to: " + paymentRef.toString());
                            paymentRef.child(paymentRef.push().getKey()).setValue(payment);
                            System.out.println("Should have written the payment to Firebase...");




                        }
//                        else if (paymentIntent.requiresConfirmation()) {
//                            System.out.println("The payment seems to require confirmation");
//                            paymentFailed();
//                            // handle confirmation
//                        }

                        else {
                            System.out.println("The payment seems to require confirmation");
                            paymentFailed();
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                    }
                });
    }


    private void addPaymentMethodToStripeCustomer() {
        String stripeCustomer = GetCustomerDetails.getInstance().getStripeId();
        String uid = mAuth.getUid();

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("stripeCustomer", stripeCustomer);
            paramJson.put("paymentIntent", paymentMethodId);
            paramJson.put("uid", uid);
        } catch (JSONException e) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-ncc-pa-app.cloudfunctions.net/attachPaymentMethodToExistingCustomer",paramJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "The response to adding this PaymentMethodId to this customer was: " + response.toString());
                        String json = response.toString();
                        System.out.println("The response String was: " + json);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(json);
                            GetCustomerDetails.getInstance().setStripePaymentMethodId(paymentMethodId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i(TAG, "The NetworkResponse was " + error.networkResponse.toString());
                        int status = 200;
                        status = networkResponse.statusCode;
                        Log.d(TAG, "The status code for this Stripe customer creation request was :" + status);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }




    private void mustSelectStudentAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Student Required");
        builder1.setMessage("You must select a student to proceed. Please try again.");
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


    private void paymentSuccessful() {
        spinner.setVisibility(View.GONE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Success");
        builder1.setMessage("Your payment has been received.\n\n Thank you.");
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


    private void paymentFailed() {
        spinner.setVisibility(View.GONE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Payment Failed");
        builder1.setMessage("We're sorry, but the payment has failed. Please try again or try a different card.");
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


    public void createStripeCustomer() {
        String email = mAuth.getCurrentUser().getEmail();
        String uid = mAuth.getCurrentUser().getUid();

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject paramJson = new JSONObject();

        try {
            paramJson.put("userEmail", email);
            paramJson.put("userUid", uid);
        } catch (JSONException e) {

        }

        // JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-sunny-9c024.cloudfunctions.net/createStripeCustomer",paramJson,
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"https://us-central1-ncc-pa-app.cloudfunctions.net/createStripeCustomer",paramJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // response.toString();
                        Log.i(TAG, "The response to this Stripe customer creation request was " + response.toString());
                        // spinner.setVisibility(View.GONE);
                        whichPaymentDetails();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        // Log.i(TAG1, "The NetworkResponse was " + error.networkResponse.toString());
                        int status = 200;
                        status = networkResponse.statusCode;
                        Log.d(TAG, "The status code for this Stripe customer creation request was :" + status);

                    }

                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }


    private void disableScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void enableScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }






}
