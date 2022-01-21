package com.example.planteraapp.entities.DAO;

import android.location.Location;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.BlogImagesCrossRef;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.BlogWithImages;
import com.example.planteraapp.entities.Relations.PlantsWithBlogsANDImages;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

@Dao
public interface PlantDAO {
    @RawQuery
    List<PlantsWithEverything> customFilterPlantsRawQuery(SupportSQLiteQuery query);

    @Insert
    long[] insertPlantLocations(PlantLocation... location);

    @Insert
    long[] insertPlantTypes(PlantType... type);

    @Insert
    long[] insertNewPlant(Plant... plant);

    @Insert
    long[] insertBlogs(Blog... blog);

    @Insert
    long insertImage(Images image);

    @Insert
    void insertNewBlogImageCrossRef(BlogImagesCrossRef blogimagescrossref);

    @Insert
    long[] insertReminders(Reminder... reminder);

    @Transaction
    @Query("SELECT * FROM PlantType")
    List<PlantType> getAllPlantTypes();

    @Delete
    void deletePlant(Plant plant);

    @Transaction
    @Query("SELECT * FROM PlantLocation")
    List<PlantLocation> getAllPlantLocations();

    @Transaction
    @Query("SELECT * FROM Blog WHERE blogID=:id")
    BlogWithImages getBlogWithImagesWithID(long id);

    @Delete
    void deleteType(PlantType type);

    @Transaction
    @Query("DELETE FROM Blog WHERE blogID=:id")
    void deleteBlog(long id);

    @Delete
    void deleteLocation(PlantLocation location);

    @Delete
    void deleteReminder(Reminder reminder);

    @Transaction
    @Query("SELECT * FROM Reminder ORDER BY realEpochTime")
    List<ReminderAndPlant> getRemindersWithPlant();

    @Update
    void updateReminder(Reminder reminder);

    @Update
    void updatePlantLocation(PlantLocation location);

    @Update
    void updatePlantType(PlantType location);

    @Update
    void updatePlant(Plant plant);

    @Update
    void updateTheme(Plant plant);

    @Transaction
    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
    PlantsWithEverything getAllPlantAttributes(String plantName);


    @Transaction
    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
    PlantsWithBlogsANDImages getAllBlogsWithPlantID(String plantName);

    @Transaction
    @Query("SELECT * FROM Plant")
    List<PlantsWithEverything> getAllPlantsWithEverything();
}
