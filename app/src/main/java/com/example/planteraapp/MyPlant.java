package com.example.planteraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.planteraapp.entities.DAO.PlantDAO;

public class MyPlant extends AppCompatActivity {
    private PlantDAO DAO;
    private String plantName;
    private boolean isActivityRecreated = false;
    private int selectedTheme = R.style.Theme_PlanteraApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        plantName = getIntent().getStringExtra("plantName");
        
        DAO = AppDatabase.getInstance(this).plantDAO();
        ChangeThemeFromDB(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plant);
    }


    @Override
    protected void onResume() {
        if (isActivityRecreated) ChangeThemeFromDB(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActivityRecreated = true;
        super.onPause();
    }

    private void ChangeThemeFromDB(boolean recreate) {
        int theme = DAO.getSelectedThemeOfUser(plantName);
        if (theme != 0 && theme == selectedTheme)
            return;
        setTheme(theme == 0 ? R.style.Theme_PlanteraApp : theme);
        if (recreate) {
            overridePendingTransition(R.anim.fragment_enter_anim, R.anim.fragment_exit_anim);
            isActivityRecreated = false;
            recreate();
        }
    }

    @Override
    public void setTheme(int resId) {
        selectedTheme = resId;
        super.setTheme(resId);
    }
}