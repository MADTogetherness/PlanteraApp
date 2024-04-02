package com.example.planteraapp.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.planteraapp.Database.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.Adapters.SlideAdapter;
import com.example.planteraapp.Model.DAO.PlantDAO;
import com.example.planteraapp.Model.Entities.PlantLocation;
import com.example.planteraapp.Model.Entities.PlantType;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

// Intro_Activity inflates R.layout.activity_intro layout
public class Intro_Activity extends AppCompatActivity {
    /* Viewpager container view is used to contain different slides for the initial introduction to app.
       Viewpager allows sliding between previous and next slides. User will be able to swipe on hand
       rather than pressing a button to go to next slide */
    private ViewPager viewPager;
    /*Custom Adapter to inflate the different slides using same  LAYOUT - intro_slide_layout.xml
      but different contents (Contents are changed on runtime) */
    private SlideAdapter slideAdapter;
    // Button View to proceed to next slides (Same Layout file different contents inflated on runtime)
    private Button Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        // Initialise Views & Setup Listeners
        init();
    }

    public void init() {
        Next = findViewById(R.id.next_intro);
        viewPager = findViewById(R.id.intro_view_pager);
        // DotsIndicator to indicate number of dots (Total number of slides)
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

    // Called after user presses NEXT on last slides page of slideAdapter
    public void NewActivity() {
        PlantDAO DAO = AppDatabase.getInstance(this).plantDAO();
        // Inserting Default PlantLocations for users to use later from the dropdown
        // Users will also be able to type custom locations other than these mentioned below
        DAO.insertPlantLocations(
                new PlantLocation("Hallway"),
                new PlantLocation("Bedroom"),
                new PlantLocation("Living room"),
                new PlantLocation("Kitchen")
        );
        // Inserting Default PlantType for users to use later from the dropdown
        // Users will also be able to type custom Plant Types other than these mentioned below
        DAO.insertPlantTypes(
                new PlantType("Cactus"),
                new PlantType("Fern"),
                new PlantType("Foliage")
        );
        // Create channel for notification & Insert Broadcast receiver in manifest
        createNotificationChannel();
        //Edit the preference for the user to know whether - Is it a new user Or an old user?
        // This preference is checked on Launcher.java activity, inorder to prevent going through introduction activity again.
        SharedPreferences.Editor editor = getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).edit();
        editor.putInt("IsOld", 1);
        editor.apply();
        // Start the Home activity fresh (No data is contained here)
        startActivity(new Intent(Intro_Activity.this, Home.class));
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        finish();
    }

    /* A simple "Stub function" to change the test of "Next" Button to "Get Started" when user
       reaches the last slide of view pager */
    public void change_NextButton_Color(int pos) {
        Next.setText(slideAdapter.getCount() - 1 == pos ? "Get Started" : "Next");
    }

    // OnPageChangedLister for viewpager, used to change "NEXT" button color
    // Each Slide has different color for different slides
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

    // Create channel for notification & Insert Broadcast receiver in manifest
    // Create a single notification Channel for entire app notifications
    // Used to initialise the notification priority, sounds, turn on lights, notification visibility
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
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
