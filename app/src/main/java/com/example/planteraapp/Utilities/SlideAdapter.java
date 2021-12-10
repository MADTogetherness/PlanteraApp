package com.example.planteraapp.Utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.planteraapp.R;

import java.util.Objects;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SlideAdapter(Context context){
        this.context = context;
    }
    public int[] slide_images ={
            R.drawable.img_intro1_image,
            R.drawable.img_intro2_image,
            R.drawable.img_intro3_image
    };

    public int[] slide_images_backgroundTint ={
            R.color.image1,
            R.color.image2,
            R.color.image3
    };

    public String[] headings = {
            "Manage efficiently",
            "Care more, Forget less",
            "Record your progress"
    };

    public String[] descriptions = {
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, ",
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, ",
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, "
    };

    public int[] nextButton_backgroundTint ={
            R.color.image1_Button,
            R.color.image2_Button,
            R.color.image3_Button
    };

    public int getNextButton_backgroundTint(int pos){
        return nextButton_backgroundTint[pos];
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d("OBJECT_VIEW", String.valueOf(position));
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.intro_slide_layout, container, false);
        TextView head = view.findViewById(R.id.intro_head),
                description = view.findViewById(R.id.intro_description);

        ImageView Slide_image = view.findViewById(R.id.intro_image);
        View Slide_image_back = view.findViewById(R.id.intro_image_background);
        Slide_image_back.setBackgroundTintList(ContextCompat.getColorStateList(context, slide_images_backgroundTint[position]));
        Slide_image.setImageResource(slide_images[position]);
        head.setText(headings[position]);
        description.setText(descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}
