package com.salzburg101.nccapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<String> images = new ArrayList<String>();

    // private static ListenerRecyclerViewClick mListener;

//    public static void setListenerRecyclerClick(ListenerRecyclerViewClick eventListener) {
//        mListener = eventListener;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_gallery);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        // layoutManager = new GridLayoutManager(this, 2);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.largeMenuButton).setOnClickListener(this);

        createGallery();

        Random random = new Random();



        // final String[] images = new String[10];

        // for(int i=0; i<images.size(); i++) {
//        for(int i=0; i<10; i++) {
//            // images[i] = "https://picsum.photos/600?image="+random.nextInt(1000+1);
//            images.add(i, "https://picsum.photos/600?image=" + random.nextInt(1000+1));
//            System.out.println("The image is now: " + images.get(i));
//        }




        final ListenerRecyclerViewClick listener = new ListenerRecyclerViewClick() {
            @Override
            public void onClick(View view, int position) {
                // open full screen activity with image clicked
                Intent i = new Intent(getApplicationContext(), FullScreenImage.class);
                i.putExtra("IMAGES", images);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        };



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                System.out.println("Now about to call the adapter");
                AdapterGalleryImage adapterGalleryImage = new AdapterGalleryImage(getApplicationContext(), images, listener);
                recyclerView.setAdapter(adapterGalleryImage);


            }
        }, 1000);



//                System.out.println("Now about to call the adapter");
//                AdapterGalleryImage adapterGalleryImage = new AdapterGalleryImage(this, images, listener);
//                recyclerView.setAdapter(adapterGalleryImage);




    }



    private void createGallery() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Query query = mRef.child("gallery/Family Album");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("Snapshot exists");
                    for (DataSnapshot urls : dataSnapshot.getChildren()) {

                        dataSnapshot.getChildrenCount();
                        String url = "";
                        url = urls.getValue().toString();
                        System.out.println("url String = " + url);

                        images.add(url);
                        System.out.println("The images ArrayList contains" + images.toString());
                    }
                } else {
                    System.out.println("Snapshot doesn't seem to exist");
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(TAG, "There was a problem retrieving the business data for this customer : ", databaseError.toException());
            };
        });

    }


//    private void callImages() {
//        AdapterGalleryImage adapter = new AdapterGalleryImage(this, images, mListener);
//        recyclerView.setAdapter(adapter);
//    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            Intent alertsIntent = new Intent(this, ExpandedMenu.class);
            startActivity(alertsIntent);
            finish();
        }
    }











}
