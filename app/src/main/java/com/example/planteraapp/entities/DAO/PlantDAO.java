package com.example.planteraapp.entities.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.planteraapp.entities.BlogImagesCrossRef;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

@Dao
public interface PlantDAO {
    @Transaction
    @Query("SELECT selectedTheme FROM Plant WHERE plantName = :plantName")
    int getSelectedThemeOfUser(String plantName);

    @RawQuery
    List<PlantsWithEverything> customFilterPlantsRawQuery(SupportSQLiteQuery query);

    @Insert
    long[] insertPlantLocations(PlantLocation... location);

    @Insert
    long[] insertPlantTypes(PlantType... type);

    @Insert
    long[] insertNewPlant(Plant... plant);

    @Insert
    long[] insertNewReminder(Reminder... reminder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImage(Images image);

    @Insert
    long insertNewBlogImageCrossRef(BlogImagesCrossRef blogimagescrossref);

    @Transaction
    @Query("SELECT * FROM PlantType")
    List<PlantType> getAllPlantTypes();

    @Transaction
    @Query("SELECT * FROM PlantLocation")
    List<PlantLocation> getAllPlantLocations();

    @Transaction
    @Query("SELECT * FROM Reminder")
    List<ReminderAndPlant> getAllRemindersWithPlant();

//    @Transaction
//    @Query("SELECT * FROM Plant")
//    List<Plant> getAllPlants();
//
//    @Transaction
//    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
//    Plant getSinglePlantInstance(String plantName);

//    @Transaction
//    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
//    PlantAndReminders getAllRemindersOfPlantFromID(String plantName);

//    @Transaction
//    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
//    PlantsWithEverything getAllPlantAttributes(String plantName);

    //    @Transaction
//    @Query("SELECT * FROM Blog WHERE blogID = :ID")
//    List<Blog> getAllBlogs(long ID);
//
//    @Transaction
//    @Query("SELECT * FROM Blog WHERE plantID = :ID")
//    List<Blog> getAllBlogsPlantID(long ID);
//
//    @Transaction
//    @Query("SELECT * FROM Plant WHERE plantName = :plantName")
//    PlantsWithBlogsANDImages getPlantWithBlogsANDImages(String plantName);
//
//    @Transaction
//    @Query("SELECT * FROM Blog WHERE blogID = :ID")
//    public List<BlogWithImages> getBlogwWithImages(long ID);

//    @Transaction
//    @Query("SELECT * FROM Plant")
//    List<PlantsWithEverything> getAllPlantsWithEverything();
}
