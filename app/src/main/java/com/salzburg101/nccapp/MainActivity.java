package com.salzburg101.nccapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProcessBadges instance = new ProcessBadges(this);

    private static final String TAG = "EmailPassword";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCustomerDatabaseReference;
    private DatabaseReference mBusinessDatabaseReference;
    private DatabaseReference mOrderSuppInfoDatabaseReference;
    private ValueEventListener mValueEventListener;

    private String mUserEmailAddress, mUserPassword, token;
    private EditText emailAddress, password;
    private ProgressBar spinner;
    private ProgressDialog progressDialog;
    private TextView passwordForgotten;


    ArrayList<StructureForRetrieveAllAlerts> alertSummary = new ArrayList<StructureForRetrieveAllAlerts>();
    ArrayList<String> alertsArray = new ArrayList<String>();

    public static Context contextOfApplication;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

         // This was required so we could get Context when using SharedPreferences in ProcessBadges class.
         contextOfApplication = getApplicationContext();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        GlobalVariables.deviceToken = token;
                        System.out.println("GlobalVariables.deviceToken: " + GlobalVariables.deviceToken);

                        // Log and toast
                        // String msg = getString(R.string.msg_token_fmt, token);
                        // Log.d(TAG, msg);
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("All")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        // Log.d(TAG, msg);
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });



        alertsArray = DefineAlertsArray.getInstance().getArrayList();
        alertSummary = DefineAlertSummary.getInstance().getArrayList();

        ProcessBadges processBadges = new ProcessBadges(this);
//        processBadges.populateEventSummary();
//        processBadges.populateNewsSummary();
//        processBadges.populatePANewsSummary();




        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        emailAddress=(EditText)findViewById(R.id.userEmailAddress);
        password=(EditText)findViewById(R.id.userPassword);
        passwordForgotten=(TextView)findViewById(R.id.passwordForgotten);


        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.passwordForgotten).setOnClickListener(this);

        Integer imageHeight = (int)((getWindow().getWindowManager().getDefaultDisplay().getHeight()) * 0.02);

        Drawable messageIconImage = getResources().getDrawable(R.drawable.messageicon);
        Integer messageIconHeight = imageHeight;
        double messageIconWidth = ((messageIconHeight / (double)(messageIconImage.getIntrinsicHeight())) * messageIconImage.getIntrinsicWidth()) + 10;

        Drawable passwordIconImage = getResources().getDrawable(R.drawable.lockicon);
        Integer passwordIconHeight = imageHeight;
        double passwordIconWidth = ((passwordIconHeight / (double)(passwordIconImage.getIntrinsicHeight())) * passwordIconImage.getIntrinsicWidth()) + 10;

        messageIconImage.setBounds(10, 0, (int) messageIconWidth, messageIconHeight);
        passwordIconImage.setBounds(10, 0, (int) passwordIconWidth, passwordIconHeight);

        emailAddress.setCompoundDrawables(messageIconImage, null, null, null);
        password.setCompoundDrawables(passwordIconImage, null, null, null);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        System.out.println("mAuth has been defined");
        mFirebaseDatabase = FirebaseDatabase.getInstance();





        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i(TAG, "Upon the first check of the currentUser, it's found to be : " + user);
                if (user != null) {

                    System.out.println("User is not null!");

                    if (mAuth.getCurrentUser().getEmail().equals("user@user.com")) {
                        System.out.println("User seems to be user@user.com!");
                        return;
                    }

                    System.out.println("The current user is " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    System.out.println("Ligging - Should start logging in now!");

                    spinner.setVisibility(View.VISIBLE);
                    ProcessGetCustDetails.getCustData();
                    getAlertsData();
                } else {
                    // user is not logged in
                    System.out.println("User seems to be null!");
                    System.out.println("Not logged in!");
                }
            }
        };



//        if ((user != null) && (!"user@user.com".equals(user))) {
//            System.out.println("Ligging - Should start logging in now!");
//            ProcessGetCustDetails.getCustData();
//            getAlertsData();
//        }

        ProcessGetCustDetails.setCustDataEventListener(new GetCustDataEventListener() {
            @Override
            public void onEvent() {
                System.out.println("Customer details seems to have been retrieved and now I'm off into the app");
                moveToMainMenu();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        getAlertsData();
//    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.loginButton) {
            if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
                ConnectionDownAlert.connectionDown(this);
                return;
            }

            spinner.setVisibility(View.VISIBLE);
            emailAddress.clearFocus();
            password.clearFocus();
            mUserEmailAddress=emailAddress.getText().toString();
            mUserPassword=password.getText().toString();

            if (!mUserEmailAddress.equals("") && !mUserPassword.equals("")) {
                signIn(mUserEmailAddress, mUserPassword);
            } else {
                spinner.setVisibility(View.GONE);
                allFieldsNeedToBeCompleted();
            }



        }


        if (i == R.id.passwordForgotten) {
            Log.d(TAG, "Password Forgotten Button Pressed");
//            Intent forgotPasswordScreen = new Intent(MainActivity.this, ForgotPasswordScreen.class);
//            startActivity(forgotPasswordScreen);
//            finish();
        }

//        if (i == R.id.largeQButton) {
//            showGuide();
//        }

    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d(TAG, "Should have just kicked off getAlertsData");
                    ProcessGetCustDetails.getCustData();
                    getAlertsData();
                } else {
                    // If sign in fails, display a message to the user.
                    spinner.setVisibility(View.GONE);
                    somethingWrong();
                    // Log.w(TAG, "signInWithEmail:failure", task.getException());
                    // Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

                // [START_EXCLUDE]
                // if (!task.isSuccessful()) {
                // mStatusTextView.setText(R.string.auth_failed);
            }
            // [END_EXCLUDE]
            // }
        });
        // [END sign_in_with_email]

    }


    public void moveToMainMenu() {
        Log.d(TAG, "The listener has been activated and now I'm going to the Main Menu - in half a second");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent retrieveAlertsScreen = new Intent(MainActivity.this, RetrieveAlerts.class);
                spinner.setVisibility(View.GONE);
                startActivity(retrieveAlertsScreen);
                finish();
            }
        }, 750);
    }


    public void getAlertsData() {
        System.out.println("Kicking off getAlertsData");
        ProcessBadges processBadges = new ProcessBadges(this);
        processBadges.populateAlertsArray();
        processBadges.getMyClasses();
    }


//    private void getNoOfOrders() {
//        final GetCustomerDetails currentCustomer = GetCustomerDetails.getInstance();
//
//        mOrderSuppInfoDatabaseReference = mFirebaseDatabase.getReference();
//
//        Query query = mOrderSuppInfoDatabaseReference.child("OrderSuppInfo").orderByChild("orderCustomer").equalTo(mAuth.getCurrentUser().getEmail());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Log.d(TAG, "The datasnpshot from OrderSuppInfo does exist.");
//                    // for (DataSnapshot orders : dataSnapshot.getChildren()) {
//                    long count = dataSnapshot.getChildrenCount();
//                    Integer numberOrders = (int)count;
//                    currentCustomer.setCustomerNumberOfOrders(numberOrders);
//                    // Log.d(TAG, "The number of orders in te snapshot is : " + numberOrders);
//                    // Log.d(TAG, "The number of orders set for the customer is : " + currentCustomer.getCustomerNumberOfOrders());
//
//                    // }
//                } else {
//                    // Log.d(TAG, "The datasnapshot from OrderSuppInfo DOES NOT seen to exist.");
//                    currentCustomer.setCustomerNumberOfOrders(1);
//                }
//                // Log.d(TAG, "The number of orders this customer has already placed is : " + currentCustomer.getCustomerNumberOfOrders());
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Log.w(TAG, "There was a problem retrieving the number of orders for this customer : ", databaseError.toException());
//            }
//        });
//    }


//    private void getCustData() {
//        Log.d(TAG, "Kicked off getCustData()");
//
//        final GetCustomerDetails currentCustomer = GetCustomerDetails.getInstance();
//
//        currentCustomer.setCustomerEmail(mAuth.getCurrentUser().getEmail());
//
//        final String currentUid = currentCustomer.getCustomerEmail();
//
//        if (currentUid != null) {
//
//            // Log.w(TAG, "The current customer Uid is : " + mAuth.getUid());
//
//            mCustomerDatabaseReference = mFirebaseDatabase.getReference();
//
//            Query query = mCustomerDatabaseReference.child("CustomerDatabase").orderByChild("id").equalTo(mAuth.getUid());
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        for (DataSnapshot customers : dataSnapshot.getChildren()) {
//
//                            if (customers.child("customerEmail").exists()) {
//                                currentCustomer.setCustomerEmail(customers.child("customerEmail").getValue().toString());
//                            }
//
//                            if (customers.child("customerFirstName").exists()) {
//                                currentCustomer.setCustomerFirstName(customers.child("customerFirstName").getValue().toString());
//                            }
//
//                            if (customers.child("customerSurname").exists()) {
//                                currentCustomer.setCustomerSurname(customers.child("customerSurname").getValue().toString());
//                            }
//
////                            if (customers.child("customerCompany").exists()) {
////                                currentCustomer.setCustomerCompany(customers.child("customerCompany").getValue().toString());
////                            }
//
//                            if (customers.child("deviceToken").exists()) {
//                                currentCustomer.setCustomerDeviceToken(customers.child("deviceToken").getValue().toString());
//                            }
//
//                            if (customers.child("hasValidPaymentDetails").exists()) {
//                                currentCustomer.setCustomerValidPaymentDetails(customers.child("hasValidPaymentDetails").getValue().equals(true));
//                            }
//
//                            if (customers.child("marketing2").exists()) {
//                                currentCustomer.setMarketingAgree(customers.child("marketing2").getValue().equals(true));
//                            }
//
////                             Log.d(TAG, "The customer snapshot is : " + customers);
////                             Log.d(TAG, "The customer email address is : " + currentCustomer.getCustomerEmail());
////                             Log.d(TAG, "The customer first name is : " + currentCustomer.getCustomerFirstName());
////                             Log.d(TAG, "The customer surname is : " + currentCustomer.getCustomerSurname());
////                             Log.d(TAG, "The customer company is : " + currentCustomer.getCustomerCompany());
////                             Log.d(TAG, "The customer device token is : " + currentCustomer.getCustomerDeviceToken());
////                             Log.d(TAG, "The customer payment details status is : " + currentCustomer.getCustomerValidPaymentDetails());
////                             Log.d(TAG, "The marketing permission is set to : " + currentCustomer.getMarketingAgree());
//
//                        }
//
////                        if (currentCustomer.getCustomerCompany() != null) {
////
////                            mBusinessDatabaseReference = mFirebaseDatabase.getReference();
////
////                            Query queryBusiness = mBusinessDatabaseReference.child("ListOfBusinesses").orderByChild("id").equalTo(currentCustomer.getCustomerCompany());
////
////
////                            queryBusiness.addListenerForSingleValueEvent(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////                                    if (dataSnapshot.exists()) {
////                                        for (DataSnapshot business : dataSnapshot.getChildren()) {
////
////                                            String address1 = new String();
////                                            String address2 = new String();
////                                            String address3 = new String();
////
////
////                                            if (business.child("address1").exists()) {
////                                                currentCustomer.setCustomerAddress1(business.child("address1").getValue().toString());
////                                                address1 = business.child("address1").getValue().toString();
////                                            }
////
////                                            if (business.child("address2").exists()) {
////                                                currentCustomer.setCustomerAddress2(business.child("address2").getValue().toString());
////                                                address2 = business.child("address2").getValue().toString();
////                                            }
////
////                                            if (business.child("address3").exists()) {
////                                                currentCustomer.setCustomerAddress3(business.child("address3").getValue().toString());
////                                                address3 = business.child("address3").getValue().toString();
////                                            }
////
////                                            if (business.child("discountPercentage").exists()) {
////                                                currentCustomer.setCustomerStandardDiscount(business.child("discountPercentage").getValue().toString());
////                                            }
////
////                                            if (address1 != null) {
////                                                String add1 = address1;
////                                            } else { String add1 = "";
////                                            }
////
////
////                                            currentCustomer.setCustomerAddress(address1 + ", " + address2 + ", " + address3);
////
////                                            // currentCustomer.setCustomerAddress(currentCustomer.getCustomerAddress1() + " " + currentCustomer.getCustomerAddress2() + " " + currentCustomer.getCustomerAddress3());
////
////                                            // Log.d(TAG, "The business snapshot is : " + business);
////                                            // Log.d(TAG, "The customer address1 : " + currentCustomer.getCustomerAddress1());
////                                            // Log.d(TAG, "The customer address2 : " + currentCustomer.getCustomerAddress2());
////                                            // Log.d(TAG, "The customer address3 : " + currentCustomer.getCustomerAddress3());
////                                            // Log.d(TAG, "The customer address : " + currentCustomer.getCustomerAddress());
////                                            // Log.d(TAG, "The customer discountPercentage : " + currentCustomer.getCustomerStandardDiscount());
////
////                                        }
////                                        Intent mainMenuScreen = new Intent(MainActivity.this, MainMenuActivity.class);
////                                        spinner.setVisibility(View.GONE);
////                                        startActivity(mainMenuScreen);
////                                        finish();
////                                    }
////
////                                }
////
////                                @Override
////                                public void onCancelled(@NonNull DatabaseError databaseError) {
////                                    // Log.w(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
////                                }
////                            });
////
////                        }
//
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w(TAG, "There was a problem retrieving the customer data", databaseError.toException());
//                }
//
//            });
//
//
//        }
//
//
//    }


    private void allFieldsNeedToBeCompleted() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Complete All Fields");
        builder1.setMessage("Please enter both your Email Address and Password to login.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog guide = builder1.create();
        guide.show();
    }


    private void somethingWrong() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Email or Password Incorrect");
        builder1.setMessage("Please check your Email and Password and try again." + "\n\n" + "To reset your password tap on 'Forgotten?' below.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog guide = builder1.create();
        guide.show();
    }


//    private void showGuide() {
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder1.setTitle("Login Page");
//        builder1.setMessage("Please enter your Email Address and Password to login." + "\n\n" +
//                "If you've forgotten your password, please click on 'Forgotten?' to reset your password." + "\n\n" +
//                "No account yet? Click on the 'Back' button (the white arrow at the top left of the screen) and select 'Signup' on the next screen.");
//        // builder1.setCancelable(true);
//
//        builder1.setPositiveButton(
//                "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        AlertDialog guide = builder1.create();
//        guide.show();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }









}
