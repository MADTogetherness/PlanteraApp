package com.example.planteraapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Reminder;

@Database(entities = {
        Reminder.class,
        PlantType.class,
        PlantLocation.class,
        Plant.class,
        Images.class,
        Blog.class
}, version = 1)
@TypeConverters({AttributeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlantDAO plantDAO();

    public static Context AppContext;
    private static final String DatabaseName = "PlanteraDB";
    public static volatile AppDatabase INSTANCE = null;

    public static AppDatabase getInstance(Context context) {
        synchronized (AppDatabase.class) {
            if (INSTANCE == null) {
                AppContext = context.getApplicationContext();
                INSTANCE = Room.databaseBuilder(AppContext, AppDatabase.class, DatabaseName).allowMainThreadQueries().build();
            }
        }
        return INSTANCE;
    }
}
