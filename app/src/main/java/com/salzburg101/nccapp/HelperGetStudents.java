package com.salzburg101.nccapp;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HelperGetStudents {


    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mRef;

    public static ArrayList<String> students = new ArrayList<String>();
    public static String[] studentsArray;

    public static ArrayList<String> documentTypes = new ArrayList<String>();
    public static String[] docsArray;


    public static void getStudents() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });
    }




    public static void getDocumentTypes() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        Query query = mRef.child("DocumentTypes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    documentTypes.clear();
                    for (DataSnapshot alerts : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        System.out.println("The students table has: " + dataSnapshot.getChildrenCount() + " children.");

                        String description;

                        if (alerts.child("docDescription").exists()) {
                            description = alerts.child("docDescription").getValue().toString();
                            // System.out.println("The message is : " + message);
                        } else {
                            return;
                        }
                        documentTypes.add(description);
                    }
                } else {
                    // TODO
                    return;
                }
                String[] documentsForArray = documentTypes.toArray(new String[documentTypes.size()]);
                for(String s : documentsForArray){
                    System.out.println(s);
                }
                docsArray = documentsForArray;
                System.out.println(documentsForArray.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            }
        });
    }


}
