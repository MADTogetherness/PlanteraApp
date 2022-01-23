package com.example.planteraapp.Utilities.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.planteraapp.R;

import java.util.Objects;

// Custom Adapter for Intro_Activity.java to setup slides using same layout (intro_slide_layout.xml)
// design but different contents on runtime
// Total number of slides = 3
public class SlideAdapter extends PagerAdapter {
    // Context of the corresponding activity
    Context context;
    // Used to inflate the layout - intro_slide_layout.xml in the corresponding activity
    LayoutInflater layoutInflater;

    // Constructor
    public SlideAdapter(Context context) {
        this.context = context;
    }

    // Constants: Image for each slide
    public int[] slide_images = {
            R.drawable.img_intro1_image,
            R.drawable.img_intro2_image,
            R.drawable.img_intro3_image
    };

    // Constants: Headings for each slide
    public String[] headings = {
            "Care more, Forget less",
            "Record your progress",
            "Manage efficiently",
    };

    // Constants: Descriptions for each slide
    public String[] descriptions = {
            "Keep forgetting to water plants? Never let another plant dry out",
            "Keep record of all the events that takes place in your plant's life, such as the day it bears its first fruit ",
            "Store records of your plants based on location as well as type of plant"
    };

    // Get total number of slides = 3
    @Override
    public int getCount() {
        return headings.length;
    }

    // Default method from PageAdapter class (extends)
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    // Initialise||Inflate slide & change contents
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d("OBJECT_VIEW", String.valueOf(position));
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate layout
        View view = layoutInflater.inflate(R.layout.intro_slide_layout, container, false);
        // Get Views - Heading (TextView), description (TextView) & images (ImageView)
        TextView head = view.findViewById(R.id.intro_head),
                description = view.findViewById(R.id.intro_description);
        ImageView Slide_image = view.findViewById(R.id.intro_image);
        // Set the content from Constants defined before
        Slide_image.setImageResource(slide_images[position]);
        head.setText(headings[position]);
        description.setText(descriptions[position]);
        // Add slide PageViewer
        container.addView(view);
        return view;
    }

    // Destroy slide
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


}
