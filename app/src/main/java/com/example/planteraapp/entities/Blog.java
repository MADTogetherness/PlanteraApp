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
                        parentColumns = "plantName",
                        childColumns = "plantName",
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
    public String plantName;
    public long dateCreated;
    public String description;

    public Blog(String plantName, String description) {
        this.plantName = plantName;
        this.dateCreated = System.currentTimeMillis();
        this.description = description;
    }

    @NonNull
    public String toString() {
        return plantName + " " + description + " " + dateCreated + " ";
    }
}
