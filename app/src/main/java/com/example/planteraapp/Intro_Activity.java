package com.example.planteraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.planteraapp.Utilities.SlideAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class Intro_Activity extends AppCompatActivity {
    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    private  Button Next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        init();
    }

    public void init(){
        Next = findViewById(R.id.next_intro);
        viewPager = findViewById(R.id.intro_view_pager);
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        slideAdapter = new SlideAdapter(this.getApplicationContext());
        viewPager.setAdapter(slideAdapter);
        viewPager.addOnPageChangeListener(viewListener);
        dotsIndicator.setViewPager(viewPager);
        Next.setOnClickListener(view -> {
            if(viewPager.getCurrentItem() + 1 >= slideAdapter.getCount()) NewActivity();
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        });
        TextView skip = findViewById(R.id.skip_intro);
        skip.setOnClickListener(view -> NewActivity());
    }

    public void NewActivity(){
        SharedPreferences.Editor editor = getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).edit();
        editor.putInt("IsOld", 1);
        editor.apply();
        startActivity(new Intent(Intro_Activity.this, Home.class));
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        finish();
    }
    public void change_NextButton_Color(int pos){
        Next.setText(slideAdapter.getCount() - 1 == pos ? "Get Started": "Next");
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {change_NextButton_Color(position);}

        @Override
        public void onPageScrollStateChanged(int state) { }
    };
}