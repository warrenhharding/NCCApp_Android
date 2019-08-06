package com.salzburg101.nccapp;

// import android.support.annotation.NonNull;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.pushwoosh.Pushwoosh;

public class ProcessGetCustDetails {

    private static final String TAG = "EmailPassword";

    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mCustomerDatabaseReference;
    public static DatabaseReference mRef;
    public static DatabaseReference mOrderSuppInfoDatabaseReference;
    public static ValueEventListener mValueEventListener;

    private static GetCustDataEventListener mListener;


    public static void setCustDataEventListener(GetCustDataEventListener eventListener) {
        mListener = eventListener;
    }


    public static void getCustData() {
        System.out.println("Gettong customer details has started");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        // Log.w(TAG, "The current customer Uid is : " + mAuth.getUid());

        final GetCustomerDetails currentCustomer = GetCustomerDetails.getInstance();

        // currentCustomer.setCustomerEmail(mAuth.getCurrentUser().getEmail());

        currentCustomer.setCustomerEmail(user.getEmail());

        final String currentUid = currentCustomer.getCustomerEmail();

        if (currentUid != null) {

            // Log.w(TAG, "The current customer Uid is : " + mAuth.getUid());

            mCustomerDatabaseReference = mFirebaseDatabase.getReference();
            // Log.i(TAG, "Now about to launch the query");

            Query query = mCustomerDatabaseReference.child("CustomerDatabase").orderByChild("id").equalTo(mAuth.getUid());

            // Log.i(TAG, "Query has been defined");

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Log.i(TAG, "Snapshot seems to exist.");
                        for (DataSnapshot customers : dataSnapshot.getChildren()) {

                            if (customers.child("customerEmail").exists()) {
                                currentCustomer.setCustomerEmail(customers.child("customerEmail").getValue().toString());
                            }

                            if (customers.child("customerFirstName").exists()) {
                                currentCustomer.setCustomerFirstName(customers.child("customerFirstName").getValue().toString());
                            }

                            if (customers.child("customerSurname").exists()) {
                                currentCustomer.setCustomerSurname(customers.child("customerSurname").getValue().toString());
                            }

                            if (customers.child("customerPhoneNumber").exists()) {
                                currentCustomer.setCustomerPhone(customers.child("customerPhoneNumber").getValue().toString());
                            }

                            if (customers.child("customerCompany").exists()) {
                                currentCustomer.setCustomerCompany(customers.child("customerCompany").getValue().toString());
                            }

                            if (customers.child("deviceToken").exists()) {
                                currentCustomer.setCustomerDeviceToken(customers.child("deviceToken").getValue().toString());
                                String token = customers.child("deviceToken").getValue().toString();
                                System.out.println("Token stored in Firebase: " + token);

                                if (!GlobalVariables.deviceToken.equals(token)) {
                                    System.out.println("About to update the token in Firebase...");
                                    String customKey = new String();
                                    customKey = mAuth.getUid();
                                    System.out.println("uid = ..." + customKey);
                                    System.out.println("token to be updated to = ..." + GlobalVariables.deviceToken);
                                    mCustomerDatabaseReference = mFirebaseDatabase.getReference().child("CustomerDatabase").child(customKey);
                                    mCustomerDatabaseReference.child("deviceToken").setValue(GlobalVariables.deviceToken);
                                }
                            }



                            if (customers.child("hasValidPaymentDetails").exists()) {
                                currentCustomer.setCustomerValidPaymentDetails(customers.child("hasValidPaymentDetails").getValue().equals(true));
                            }

                            if (customers.child("stripeCustomerId").exists()) {
                                currentCustomer.setStripeId(customers.child("stripeCustomerId").getValue().toString());
                            }

                            if (customers.child("stripePaymentMethod").exists()) {
                                currentCustomer.setStripePaymentMethodId(customers.child("stripePaymentMethod").getValue().toString());
                            }

                            if (customers.child("marketing2").exists()) {
                                currentCustomer.setMarketingAgree(customers.child("marketing2").getValue().equals(true));
                            }

                            if (customers.child("paymentDetailsAvail").exists()) {
                                currentCustomer.setPaymentDetailsAvail(customers.child("paymentDetailsAvail").getValue().toString());
                            }



//                             Log.i(TAG, "The customer snapshot is : " + customers);
//                             Log.i(TAG, "The customer email address is : " + currentCustomer.getCustomerEmail());
//                             Log.i(TAG, "The customer first name is : " + currentCustomer.getCustomerFirstName());
//                             Log.i(TAG, "The customer surname is : " + currentCustomer.getCustomerSurname());
//                             Log.i(TAG, "The customer company is : " + currentCustomer.getCustomerCompany());
//                             Log.i(TAG, "The customer device token is : " + currentCustomer.getCustomerDeviceToken());
//                             Log.i(TAG, "The customer payment details status is : " + currentCustomer.getCustomerValidPaymentDetails());
//                             Log.i(TAG, "The customer Stripe Id is : " + currentCustomer.getStripeId());
//                             Log.i(TAG, "About to head over the next screen now.");

                            System.out.println("Listener activated...");
                            if(mListener!=null) mListener.onEvent();

                        }


//                        if (currentCustomer.getCustomerCompany() != null) {
//
//                            // Log.i(TAG, "Now moving on to get the company related details.");
//
////                            if (currentCustomer.getCustomerCompany().equals("--- My company is not here ---")) {
////                                if(mListener!=null)
////                                    mListener.onEvent();
////                            }
//
//                            mBusinessDatabaseReference = mFirebaseDatabase.getReference();
//
//                            Query queryBusiness = mBusinessDatabaseReference.child("ListOfBusinesses").orderByChild("id").equalTo(currentCustomer.getCustomerCompany());
//
//
//                            queryBusiness.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                    if (dataSnapshot.exists()) {
//                                        for (DataSnapshot business : dataSnapshot.getChildren()) {
//
//                                            String address1 = new String();
//                                            String address2 = new String();
//                                            String address3 = new String();
//
//
//                                            if (business.child("address1").exists()) {
//                                                currentCustomer.setCustomerAddress1(business.child("address1").getValue().toString());
//                                                address1 = business.child("address1").getValue().toString();
//                                            }
//
//                                            if (business.child("address2").exists()) {
//                                                currentCustomer.setCustomerAddress2(business.child("address2").getValue().toString());
//                                                address2 = business.child("address2").getValue().toString();
//                                            }
//
//                                            if (business.child("address3").exists()) {
//                                                currentCustomer.setCustomerAddress3(business.child("address3").getValue().toString());
//                                                address3 = business.child("address3").getValue().toString();
//                                            }
//
//                                            if (business.child("discountPercentage").exists()) {
//                                                currentCustomer.setCustomerStandardDiscount(business.child("discountPercentage").getValue().toString());
//                                            }
//
//                                            if (address1 != null) {
//                                                String add1 = address1;
//                                            } else { String add1 = "";
//                                            }
//
//                                            currentCustomer.setCustomerAddress(address1 + ", " + address2 + ", " + address3);
//
//                                            // Log.i(TAG, "The business snapshot is : " + business);
//                                            // Log.i(TAG, "The customer address1 : " + currentCustomer.getCustomerAddress1());
//                                            // Log.i(TAG, "The customer address2 : " + currentCustomer.getCustomerAddress2());
//                                            // Log.i(TAG, "The customer address3 : " + currentCustomer.getCustomerAddress3());
//                                            // Log.i(TAG, "The customer address : " + currentCustomer.getCustomerAddress());
//                                            // Log.i(TAG, "The customer discountPercentage : " + currentCustomer.getCustomerStandardDiscount());
//
//                                            if(mListener!=null) mListener.onEvent();
//                                        }
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    // Log.w(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
//                                }
//                            });
//                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "There was a problem retrieving the customer data", databaseError.toException());
                }
            });
        }
    }


}

