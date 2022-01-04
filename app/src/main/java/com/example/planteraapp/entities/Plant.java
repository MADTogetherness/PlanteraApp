package com.example.planteraapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = PlantType.class,
                        parentColumns = "type",
                        childColumns = "plantType",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = PlantLocation.class,
                        parentColumns = "location",
                        childColumns = "plantLocation",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Plant {
    @NonNull
    @PrimaryKey
    //Primary Key
    public String plantName;
    //Foreign Key
    @ColumnInfo(index = true)
    public String plantType;
    //Foreign Key
    @ColumnInfo(index = true)
    public String plantLocation;

    public long dateOfCreation;

    //NOT RELATED TO Images Table
    public String profile_image;

    public int selectedTheme;
    public String description;

    public Plant(String plantName, String profile_image, String plantType, String plantLocation, int selectedTheme, String description) {
        this.plantName = plantName;
        this.plantType = plantType;
        this.plantLocation = plantLocation;
        this.profile_image = profile_image;
        this.selectedTheme = selectedTheme;
        this.description = description;
        this.dateOfCreation = System.currentTimeMillis();
    }

    @NonNull
    public String toString() {
        return plantName;
    }
}
