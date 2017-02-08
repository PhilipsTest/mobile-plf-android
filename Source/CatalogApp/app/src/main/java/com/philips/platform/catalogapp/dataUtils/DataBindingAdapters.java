package com.philips.platform.catalogapp.dataUtils;


import android.databinding.BindingAdapter;
import android.widget.ImageView;

public class DataBindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }
}
