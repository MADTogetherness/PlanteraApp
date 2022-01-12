package com.example.planteraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {
    public static String SharedFile = "LaunchFiles";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * @param: m = -1 : Follow System Theme
         * @param: m = 1 : Follow Light Mode Theme
         * @param m = 2 : Follow Dark Mode Theme
         * TODO: INSIDE THE SETTINGS FRAGMENT
         * TODO: Change the mode value in settings as well as change theme. Follow the code below
         * This is actually the app theme, we ae storing in preferences
         * SharedPreferences.Editor editor = requireActivity().getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).edit();
         * editor.putInt("mode", 1);
         * editor.apply();
         * TODO: Then ReCreate the activity following below code
         * recreate();
         */
        int m = getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("mode", 10);
        if (m != 10) {
            AppCompatDelegate.setDefaultNightMode(m);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        FrameLayout logo_image = findViewById(R.id.image_logo);
        TextView logo_text = findViewById(R.id.text_logo);
        logo_image.animate().translationY(-3200).setDuration(800).setStartDelay(2500);
        logo_text.animate().translationY(1400).setDuration(800).setStartDelay(2500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startActivity(new Intent(LauncherActivity.this, checkNewUser()));
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    public Class<? extends AppCompatActivity> checkNewUser() {
        Log.d("IsOld", String.valueOf(getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("IsOld", 0)));
        return getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("IsOld", 0) == 0 ? Intro_Activity.class : Home.class;
    }

    public static String getThemeName(int id) {
        switch (id) {
            case R.style.Theme_PlanteraApp_Chiffon_Purple:
                return "Chiffon Purple";
            case R.style.Theme_PlanteraApp_Monochromatic_Brown:
                return "Monochromatic Brown";
            case R.style.Theme_PlanteraApp_Accent_Dark:
                return "Accent Dark";
            case R.style.Theme_PlanteraApp_Dracula_Light:
                return "Dracula Light";
            default:
                return "Default App Theme";
        }
    }

    public static int getColour(String name) {
        switch (name) {
            case "Water":
            case "water":
            case "Aqua":
            case "aqua":
                return R.color.Reminder_Water;
            case "Soil":
            case "Fertile":
            case "soil":
            case "fertile":
                return R.color.Reminder_Soil;
            default:
                return R.color.Reminder_Other;
        }
    }
}