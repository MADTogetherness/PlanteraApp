package com.example.planteraapp;

import static com.example.planteraapp.Utilities.AttributeConverters.StringToBitMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class MyPlant extends AppCompatActivity {
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", };
    private Plant plant;
    private PlantDAO DAO;
    private String plantName;
    Button deleteBtn, editBtn, editThemeBtn;
    ImageButton closeBtn, item2editreminderBtn;
    TextView themenameTV, nextreminderTV, itemdateCreatedTV, itemnameplantTV, itemdescriptionTV, item2remindernameTV, item2reminderdescTV;
    ImageView itemplantimageIV, task_doneIV;
    private boolean isActivityRecreated = false;
    private int selectedTheme = R.style.Theme_PlanteraApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: GET THE PLANT NAME FROM PREVIOUS ACTIVITY NOT DATABASE
        plantName = getIntent().getStringExtra("plantName");
        DAO = AppDatabase.getInstance(this).plantDAO();
        plant = DAO.getSinglePlantInstance(plantName);
        ChangeThemeFromDB(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plant);
        init();
    }

    public void init(){
        deleteBtn = findViewById(R.id.deleteBtn);
        editBtn = findViewById(R.id.editBtn);
        closeBtn = findViewById(R.id.closeBtn);
        editThemeBtn = findViewById(R.id.editThemeBtn);
        themenameTV = findViewById(R.id.themenameTV);
        nextreminderTV = findViewById(R.id.nextreminderTV);
        item2editreminderBtn = findViewById(R.id.editreminderBtn);
        itemdateCreatedTV = findViewById(R.id.item_dateCreatedTV);
        itemnameplantTV = findViewById(R.id.name_plant);
        itemdescriptionTV = findViewById(R.id.description);
        item2remindernameTV = findViewById(R.id.reminder_name);
        item2reminderdescTV = findViewById(R.id.reminder_desc);
        itemplantimageIV = findViewById(R.id.item_image);
        task_doneIV = findViewById(R.id.task_done);

        itemdateCreatedTV.setText(getDate(plant.dateOfCreation));
        itemnameplantTV.setText(plant.plantName);
        itemdescriptionTV.setText(plant.description);
        itemplantimageIV.setImageBitmap(StringToBitMap(plant.profile_image));
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

    public String getDate(Long datel){
        Date date = new Date(datel);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dayValue = formatter.format(date).substring(0,2);
        String monthValue = formatter.format(date).substring(3,5);
        String yearValue = formatter.format(date).substring(6,10);

        return dayValue + " " + months[Integer.parseInt(monthValue)] + " " + yearValue;
    }
}