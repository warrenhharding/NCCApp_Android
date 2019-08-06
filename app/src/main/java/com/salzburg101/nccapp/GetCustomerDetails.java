package com.salzburg101.nccapp;

public class GetCustomerDetails {

    private static GetCustomerDetails instance = new GetCustomerDetails();

    //Getter-Setters
    public static GetCustomerDetails getInstance() {
        return instance;
    }

    public static void setInstance(GetCustomerDetails instance) {
        GetCustomerDetails.instance = instance;
    }

    private String customer_email;
    private String customer_first_name;
    private String customer_surname;
    private String customer_phone;
    private String customer_company;
    private String customer_device_token;
    private Boolean customer_valid_payment_details;
    private String customer_address1;
    private String customer_address2;
    private String customer_address3;
    private String customer_address;
    private String customer_standard_discount;
    private Integer customer_number_of_orders;
    private String tokenString;
    private String stripeId;
    private String stripePaymentMethodId;
    private String paymentDetailsAvail;
    private Boolean marketingAgree;



    private GetCustomerDetails() { }

    public String getCustomerEmail() { return customer_email; }
    public void setCustomerEmail(String customer_email) { this.customer_email = customer_email; }

    public String getCustomerFirstName() { return customer_first_name; }
    public void setCustomerFirstName(String customer_first_name) { this.customer_first_name = customer_first_name; }

    public String getCustomerSurname() { return customer_surname; }
    public void setCustomerSurname(String customer_surname) { this.customer_surname = customer_surname; }

    public String getCustomerPhone() { return customer_phone; }
    public void setCustomerPhone(String customer_phone) { this.customer_phone = customer_phone; }

    public String getCustomerCompany() { return customer_company; }
    public void setCustomerCompany(String customer_company) { this.customer_company = customer_company; }

    public String getCustomerDeviceToken() { return customer_device_token; }
    public void setCustomerDeviceToken(String customer_device_token) { this.customer_device_token = customer_device_token; }

    public Boolean getCustomerValidPaymentDetails() { return customer_valid_payment_details; }
    public void setCustomerValidPaymentDetails(Boolean customer_valid_payment_details) { this.customer_valid_payment_details = customer_valid_payment_details; }

    public String getCustomerAddress1() { return customer_address1; }
    public void setCustomerAddress1(String customer_address1) { this.customer_address1 = customer_address1; }

    public String getCustomerAddress2() { return customer_address2; }
    public void setCustomerAddress2(String customer_address2) { this.customer_address2 = customer_address2; }

    public String getCustomerAddress3() { return customer_address3; }
    public void setCustomerAddress3(String customer_address3) { this.customer_address3 = customer_address3; }

    public String getCustomerAddress() { return customer_address; }
    public void setCustomerAddress(String customer_address) { this.customer_address = customer_address; }

    public String getCustomerStandardDiscount() { return customer_standard_discount; }
    public void setCustomerStandardDiscount(String customer_standard_discount) { this.customer_standard_discount = customer_standard_discount; }

    public Integer getCustomerNumberOfOrders() { return customer_number_of_orders; }
    public void setCustomerNumberOfOrders(Integer customer_number_of_orders) { this.customer_number_of_orders = customer_number_of_orders; }

    public String getTokenString() { return tokenString; }
    public void setTokenString(String tokenString) { this.tokenString = tokenString; }

    public String getStripeId() { return stripeId; }
    public void setStripeId(String stripeId) { this.stripeId = stripeId; }

    public String getStripePaymentMethodId() { return stripePaymentMethodId; }
    public void setStripePaymentMethodId(String stripePaymentMethodId) { this.stripePaymentMethodId = stripePaymentMethodId; }

    public String getPaymentDetailsAvail() { return paymentDetailsAvail; }
    public void setPaymentDetailsAvail(String paymentDetailsAvail) { this.paymentDetailsAvail = paymentDetailsAvail; }

    public Boolean getMarketingAgree() { return marketingAgree; }
    public void setMarketingAgree(Boolean marketingAgree) { this.marketingAgree = marketingAgree; }





}
