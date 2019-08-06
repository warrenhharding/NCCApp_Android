package com.salzburg101.nccapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class FullScreenImage extends Activity implements View.OnClickListener {


    ViewPager viewPager;
    // String[] images;
    ArrayList images;
    int position;
    Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        closeButton = (Button) findViewById(R.id.closeButton);
        findViewById(R.id.closeButton).setOnClickListener(this);

        if (savedInstanceState==null) {
            Intent i = getIntent();
            images = i.getStringArrayListExtra("IMAGES");
            // images = i.getStringArrayExtra("IMAGES");
            position = i.getIntExtra("POSITION", 0);
        }


        viewPager = (ViewPager)findViewById(R.id.viewPager);

        AdapterFullSize adapterFullSize = new AdapterFullSize(this, images);
        viewPager.setAdapter(adapterFullSize);
        viewPager.setCurrentItem(position, true);

    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.closeButton) {
            finish();
        }
    }


}
