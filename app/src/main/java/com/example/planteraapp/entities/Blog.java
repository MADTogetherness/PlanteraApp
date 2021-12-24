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
                )
        }
)
public class Blog {
    @PrimaryKey(autoGenerate = true)
    //Primary Key
    public long blogID;

    //Foreign Key
    @ColumnInfo(index = true)
    public long plantID;

    public long dateCreated;
    public String description;

    public Blog(long blogID, long plantID, long dateCreated, String description) {
        this.blogID = blogID;
        this.plantID = plantID;
        this.dateCreated = dateCreated;
        this.description = description;

    }

    @NonNull
    public String toString() {
        return blogID + " " + plantID;
    }
}
