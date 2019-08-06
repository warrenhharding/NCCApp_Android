package com.salzburg101.nccapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ExpandedMenu extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;

    private TextView customerName;
    private Button logout, backButton;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_menu);

        String fullName = GetCustomerDetails.getInstance().getCustomerFirstName() + " " + GetCustomerDetails.getInstance().getCustomerSurname();

        // Code here to deal with new customers who are not with a registered company

        // customerName.setText(fullName);

        mAuth = FirebaseAuth.getInstance();
        TextView titleText = (TextView)findViewById(R.id.titleText);
        titleText.setText("Logged in as: " + mAuth.getCurrentUser().getEmail());

        findViewById(R.id.leftarrowwhite).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        findViewById(R.id.alertPreferences).setOnClickListener(this);
        findViewById(R.id.viewPhotoGallery).setOnClickListener(this);
        findViewById(R.id.uploadPhoto).setOnClickListener(this);
        findViewById(R.id.paymentTypes).setOnClickListener(this);
        findViewById(R.id.myPayments).setOnClickListener(this);
        findViewById(R.id.uploadFile).setOnClickListener(this);
        findViewById(R.id.viewUploadedDocument).setOnClickListener(this);
        findViewById(R.id.signOut).setOnClickListener(this);






    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent alertsIntent = new Intent(this, RetrieveAlerts.class);
            startActivity(alertsIntent);
            finish();
        }


        if (i == R.id.signOut) {
            mAuth.getInstance().signOut();
            Intent loginScreen = new Intent(ExpandedMenu.this, MainActivity.class);
            startActivity(loginScreen);
            finish();
        }

        if (i == R.id.viewPhotoGallery) {
            Intent photoGallery = new Intent(ExpandedMenu.this, GalleryActivity.class);
            startActivity(photoGallery);
            finish();
        }

        if (i == R.id.uploadPhoto) {
            Intent uploadImage = new Intent(ExpandedMenu.this, UploadImageForGallery.class);
            startActivity(uploadImage);
            finish();
        }

        if (i == R.id.paymentTypes) {
            Intent paymentTypes = new Intent(ExpandedMenu.this, RetrievePaymentTypes.class);
            startActivity(paymentTypes);
            finish();
        }

        if (i == R.id.myPayments) {
            Intent myPayments = new Intent(ExpandedMenu.this, RetrieveMyPayments.class);
            startActivity(myPayments);
            finish();
        }

        if (i == R.id.uploadFile) {
            Intent uploadFile = new Intent(ExpandedMenu.this, UploadMyFile.class);
            startActivity(uploadFile);
            finish();
        }

        if (i == R.id.viewUploadedDocument) {
            Intent viewUploads = new Intent(ExpandedMenu.this, RetrieveMyUserDocuments.class);
            startActivity(viewUploads);
            finish();
        }

        if (i == R.id.alertPreferences) {
            Intent alertPrefs = new Intent(ExpandedMenu.this, RetrieveAlertPreferences.class);
            startActivity(alertPrefs);
            finish();
        }




    }
}
