package com.example.planteraapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Plant.class,
                        parentColumns = "plantID",
                        childColumns = "plantID",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                //@ForeignKey(
                //        entity = Images.class,
                //        parentColumns = "imageID",
                //        childColumns = "profile_image_id",
                //        onDelete = ForeignKey.CASCADE,
                //        onUpdate = ForeignKey.CASCADE
                //)
        }
)
public class Blog {
    @PrimaryKey(autoGenerate = true)
    //Primary Key
    public long blogID;

    //Foreign Key
    @ColumnInfo(index = true)
    public long plantID;

    //@ColumnInfo(index = true)
    //public long profile_image_id;

    public long dateCreated;
    public String description;

    public Blog(long plantID, String description) {
        this.plantID = plantID;
        this.dateCreated = System.currentTimeMillis();
        this.description = description;
    }

    @NonNull
    public String toString() {
        return plantID + " " + description;
    }
}
