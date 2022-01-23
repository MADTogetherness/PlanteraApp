package com.example.planteraapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.planteraapp.Utilities.Other.AttributeConverters;
import com.example.planteraapp.Model.Entities.Blog;
import com.example.planteraapp.Model.Entities.BlogImagesCrossRef;
import com.example.planteraapp.Model.DAO.PlantDAO;
import com.example.planteraapp.Model.Entities.Images;
import com.example.planteraapp.Model.Entities.Plant;
import com.example.planteraapp.Model.Entities.PlantLocation;
import com.example.planteraapp.Model.Entities.PlantType;
import com.example.planteraapp.Model.Entities.Reminder;

@Database(entities = {
        Reminder.class,
        PlantType.class,
        PlantLocation.class,
        Plant.class,
        Images.class,
        Blog.class,
        BlogImagesCrossRef.class
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
