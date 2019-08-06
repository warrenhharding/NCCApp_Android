package com.salzburg101.nccapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class UploadMyFile extends AppCompatActivity implements View.OnClickListener {

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    String docType = "";
    String selectedStudents = "";
    String fileName = "";
    String comments = "";

    //these are the views
    TextView textViewStatus, docTypeTextView, selectStudentsTextView;

    EditText commentsEditText;

    ProgressBar progressBar;

    Button selectFileForUpload, submitButton, selectStudents, selectDocType, largeMenuButton;

    Intent fileData;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_myfile);

        mAuth = FirebaseAuth.getInstance();

        HelperGetStudents.getStudents();
        HelperGetStudents.getDocumentTypes();


        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //getting the views
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        docTypeTextView = (TextView) findViewById(R.id.docTypeTextView);
        selectStudentsTextView = (TextView) findViewById(R.id.selectStudentsTextView);

        commentsEditText = (EditText) findViewById(R.id.commentsEditText);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        selectFileForUpload = (Button) findViewById(R.id.selectFileForUpload);
        submitButton = (Button) findViewById(R.id.submitButton);
        selectStudents = (Button) findViewById(R.id.selectStudents);
        selectDocType = (Button) findViewById(R.id.selectDocType);
        largeMenuButton = (Button) findViewById(R.id.largeMenuButton);



        //attaching listeners to views
        findViewById(R.id.selectFileForUpload).setOnClickListener(this);
        findViewById(R.id.submitButton).setOnClickListener(this);
        findViewById(R.id.selectStudents).setOnClickListener(this);
        findViewById(R.id.selectDocType).setOnClickListener(this);
        findViewById(R.id.largeMenuButton).setOnClickListener(this);


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectFileForUpload:
                getPDF();
                break;
            case R.id.submitButton:
                submitButtonPressed();
                break;
            case R.id.selectStudents:
                selectStudents();
                break;
            case R.id.selectDocType:
                selectDocType();
                break;
            case R.id.largeMenuButton:
                Intent expandedIntent = new Intent(this, ExpandedMenu.class);
                startActivity(expandedIntent);
                finish();
                break;
        }
    }



    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf, image/jpeg, image/png, text/plain");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //the user has chosen the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                fileData = data;
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                fileName = displayName;
                textViewStatus.setText(fileName);

            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        String uploadPath = mAuth.getCurrentUser().getUid() + "/" + fileName;
        System.out.println("The path we are loading to is: " + uploadPath);
        // StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        StorageReference sRef = mStorageReference.child("userUploadedDocuments/" + uploadPath);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully");

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url = uri.getResult();

                        String userComments = commentsEditText.getText().toString();
                        String userEmail = mAuth.getCurrentUser().getEmail();
                        String uid = mAuth.getCurrentUser().getUid();

                        Date nowDate = new Date();
                        SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000");
                        String dateString = outputDate.format(nowDate);


                        StructureForUserUploadedDocuments upload = new StructureForUserUploadedDocuments(selectedStudents, dateString, userEmail, userComments, docType, url.toString(), fileName);
                        DatabaseReference mRef = mDatabaseReference.child("UploadedDocuments").child(uid);
                        mRef.child(mRef.push().getKey()).setValue(upload);

                        successfulUpload();
                        docTypeTextView.setText("No document selected");
                        selectStudentsTextView.setText("No students selected.");
                        textViewStatus.setText("No file selected");
                        commentsEditText.setText("");
                        docType = "";
                        selectedStudents = "";
                        fileName = "";
                        comments = "";
                        fileData = null;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        errorInUploading();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }



    private void selectDocType() {
        final ArrayList checked = new ArrayList<Integer>();
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select Document Type");

        builder.setSingleChoiceItems(HelperGetStudents.docsArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String result = HelperGetStudents.docsArray[i];
                docTypeTextView.setText(result);
                docType = result;
                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Please Select Document Type");
//        // Add a checkbox list
//        boolean[] checkedItems = {false, false, false, false, false};
//        builder.setMultiChoiceItems(HelperGetStudents.docsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                // The user checked or unchecked a box
//                if (isChecked) {
//                    // if the user checked the item, add it to the selected items
//                    for(int i=0; i<HelperGetStudents.docsArray.length; i++) {
//                        checked.remove(Integer.valueOf(i));
//                    }
//                    checked.add(which);
//                    System.out.println("checked now included " + checked.toString());
//
//                }
//                else if (checked.contains(which)) {
//                    // else if the item is already in the array, remove it
//                    checked.remove(Integer.valueOf(which));
//                    System.out.println("checked now included " + checked.toString());
//                }
//            }
//        });
//        // Add OK and Cancel buttons
//        AlertDialog.Builder ok = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // The user clicked OK
//                if (checked.size() == 0) {
//                    dialog.dismiss();
//                    mustSelectDocumentAlert();
//                    return;
//                }
//                docType = "";
//                for (int i = 0; i < checked.size(); i++) {
//                    String x = HelperGetStudents.docsArray[i].toString();
//                    docType = docType + x;
//                    docTypeTextView.setText(docType);
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        docTypeTextView.setText(docType);
//        // Create and show the alert dialog
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }



    private void selectStudents() {
        selectedStudents = "";
        selectStudentsTextView.setText("No students selected.");

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
                selectStudentsTextView.setText(selectedStudents);
            }
        });
        builder.setNegativeButton("Cancel", null);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void submitButtonPressed() {
        if (docTypeTextView.equals("")) {
            mustSelectDocumentAlert();
            return;
        }

        if (selectedStudents.equals("")) {
            mustSelectStudentAlert();
            return;
        }

        if (fileName.equals("")) {
            mustSelectFileAlert();
            return;
        }




        uploadFile(fileData.getData());
    }



    private void mustSelectDocumentAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Document Type Required");
        builder1.setMessage("You must select a Document Type to proceed.");
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



    private void mustSelectFileAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("File Required");
        builder1.setMessage("You must select a file to proceed.");
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



    private void errorInUploading() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There's been a problem with uploading your file. Please try again.");
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



    private void successfulUpload() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Success");
        builder1.setMessage("Your file has been successfully uploaded.");
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



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


}