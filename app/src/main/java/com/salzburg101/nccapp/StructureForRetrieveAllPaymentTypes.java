package com.salzburg101.nccapp;

public class StructureForRetrieveAllPaymentTypes {

    private String mChargeName, mChargeDescription, mChargeAmount, mChargeTopic, mChargeAmountNumbers;
    private Boolean mIncludeLocalCharges;

    public StructureForRetrieveAllPaymentTypes(String chargeName, String chargeDescription, String chargeAmount, Boolean includeLocalCharges, String chargeTopic, String chargeAmountNumbers) {
        mChargeName = chargeName;
        mChargeDescription = chargeDescription;
        mChargeAmount = chargeAmount;
        mIncludeLocalCharges = includeLocalCharges;
        mChargeTopic = chargeTopic;
        mChargeAmountNumbers = chargeAmountNumbers;
    }

    public String getChargeName() {
        return mChargeName;
    }

    public String getChargeDescription() {
        return mChargeDescription;
    }

    public String getChargeAmount() {
        return mChargeAmount;
    }

    public Boolean getIncludeLocalCharges() {
        return mIncludeLocalCharges;
    }

    public String getChargeTopic() {
        return mChargeTopic;
    }

    public String getChargeAmountNumbers() {
        return mChargeAmountNumbers;
    }


}

