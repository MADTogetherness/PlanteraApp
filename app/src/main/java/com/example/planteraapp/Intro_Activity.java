package com.example.planteraapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.planteraapp.Utilities.SlideAdapter;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
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

    public void init() {
        Next = findViewById(R.id.next_intro);
        viewPager = findViewById(R.id.intro_view_pager);
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        slideAdapter = new SlideAdapter(this.getApplicationContext());
        viewPager.setAdapter(slideAdapter);
        viewPager.addOnPageChangeListener(viewListener);
        dotsIndicator.setViewPager(viewPager);
        Next.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() + 1 >= slideAdapter.getCount()) NewActivity();
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        });
        TextView skip = findViewById(R.id.skip_intro);
        skip.setOnClickListener(view -> NewActivity());
    }

    public void NewActivity() {
        PlantDAO DAO = AppDatabase.getInstance(this).plantDAO();
        long[] success1 = DAO.insertPlantLocations(
                new PlantLocation("Hallway"),
                new PlantLocation("Bedroom"),
                new PlantLocation("Living room"),
                new PlantLocation("Kitchen")
        );
        long[] success2 = DAO.insertPlantTypes(
                new PlantType("Cactus"),
                new PlantType("Fern"),
                new PlantType("Foliage")
        );
        createNotificationChannel();
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            change_NextButton_Color(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_MAX;
        NotificationChannel channel = new NotificationChannel(LauncherActivity.CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.Button_Primary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
