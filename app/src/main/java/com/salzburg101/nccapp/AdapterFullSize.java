package com.salzburg101.nccapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class AdapterFullSize extends PagerAdapter {

    Context context;
    // String[] images;
    ArrayList images;
    LayoutInflater inflater;




    // public AdapterFullSize(Context context, String[] images) {
    public AdapterFullSize(Context context, ArrayList images) {
        this.context = context;
        this.images = images;
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        View v = inflater.inflate(R.layout.full_item, null);

        ImageView imageView = (ImageView)v.findViewById(R.id.img);

        Glide.with(context).load(images.get(position).toString()).apply(new RequestOptions().centerInside())
            .into(imageView);

        ViewPager vp = (ViewPager)container;
        vp.addView(v, 0);
        return v;

    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // super.destoyItem(container, position, object);

        ViewPager viewPager = (ViewPager)container;
        View v = (View)object;
        viewPager.removeView(v);

    }





}
