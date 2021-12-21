package com.example.planteraapp.Utilities;

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

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ImageView Slide_image;

    public SlideAdapter(Context context){
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.img_intro1_image,
            R.drawable.img_intro2_image,
            R.drawable.img_intro3_image
    };


    public String[] headings = {
            "Care more, Forget less",
            "Record your progress",
            "Manage efficiently",
    };

    public String[] descriptions = {
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, ",
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, ",
            "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Nihil magnm atque necessitatibus quibusdam consequuntur velit dolore, "
    };

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

        Slide_image = view.findViewById(R.id.intro_image);
        Slide_image.setImageResource(slide_images[position]);
        head.setText(headings[position]);
        description.setText(descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


}
