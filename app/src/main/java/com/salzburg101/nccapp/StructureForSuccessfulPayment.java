package com.salzburg101.nccapp;

public class StructureForSuccessfulPayment {

    private String mAmount, mChargeName, mCurrency, mDate, mDescription, mEmail, mReasonForCharge, mStripeCustomerId;

    public StructureForSuccessfulPayment(String amount, String chargeName, String currency, String date, String description, String email, String reasonForCharge, String stripeCustomerId) {
        mAmount = amount;
        mChargeName = chargeName;
        mCurrency = currency;
        mDate = date;
        mDescription = description;
        mEmail = email;
        mReasonForCharge = reasonForCharge;
        mStripeCustomerId = stripeCustomerId;

    }

    public String getAmount() { return mAmount; }

    public String getChargeName() { return mChargeName; }

    public String getCurrency() { return mCurrency; }

    public String getDate() { return mDate; }

    public String getDescription() { return mDescription; }

    public String getEmail() { return mEmail; }

    public String getReasonForCharge() { return mReasonForCharge; }

    public String getStripeCustomerId() { return mStripeCustomerId; }






}

