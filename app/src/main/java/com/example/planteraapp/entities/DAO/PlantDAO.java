package com.example.planteraapp.entities.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.Relations.BlogImagesCrossRef;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.BlogWithImages;
import com.example.planteraapp.entities.Relations.PlantAndReminders;
import com.example.planteraapp.entities.Relations.PlantsWithBlogsANDImages;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

@Dao
public interface PlantDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPlantLocations(PlantLocation... location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPlantTypes(PlantType... type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPlantProfileImages(Images... plant_profile_image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertReminders(Reminder... reminders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] InsertNewPlant(Plant... plant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] InsertNewBlog(Blog... blog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long InsertNewBlogImageCrossRef(BlogImagesCrossRef blogimagescrossref);

    @Transaction
    @Query("SELECT * FROM PlantType")
    List<PlantType> getAllPlantTypes();

    @Transaction
    @Query("SELECT * FROM PlantLocation")
    List<PlantLocation> getAllPlantLocations();

    @Transaction
    @Query("SELECT * FROM Plant WHERE plantID = :ID")
    PlantAndReminders getAllRemindersOfPlantFromID(long ID);

    @Transaction
    @Query("SELECT * FROM Plant WHERE name = :name")
    PlantsWithEverything getAllPlantAttributes(String name);

    @Transaction
    @Query("SELECT * FROM Blog WHERE blogID = :ID")
    List<Blog> getAllBlogs(long ID);

    @Transaction
    @Query("SELECT * FROM Blog WHERE plantID = :ID")
    List<Blog> getAllBlogsPlantID(long ID);

    @Transaction
    @Query("SELECT * FROM Plant WHERE plantID = :ID")
    public List<PlantsWithBlogsANDImages> getPlantsWithBlogsANDImages(long ID);

    @Transaction
    @Query("SELECT * FROM Blog WHERE blogID = :ID")
    public List<BlogWithImages> getBlogwWithImages(long ID);

}
