package com.example.planteraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        FrameLayout logo_image = findViewById(R.id.image_logo);
        TextView logo_text = findViewById(R.id.text_logo);

        logo_image.animate().translationY(-3200).setDuration(800).setStartDelay(4000);
        logo_text.animate().translationY(1400).setDuration(800).setStartDelay(4000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                startActivity(new Intent(LauncherActivity.this, checkNewUser()));
                overridePendingTransition(0, 0);
                finish();
            }
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    public Class<? extends AppCompatActivity> checkNewUser(){
        return this.getPreferences(Context.MODE_PRIVATE).getInt("IsOld", 0) == 0 ? Intro_Activity.class: Home.class;
    }
}