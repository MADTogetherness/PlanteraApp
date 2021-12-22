package com.example.planteraapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Images.class,
                        parentColumns = "imageID",
                        childColumns = "profile_image_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
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
    @PrimaryKey(autoGenerate = true)
    //Primary Key
    public long plantID;
    //Foreign Key
    @ColumnInfo(index = true)
    public String plantType;
    //Foreign Key
    @ColumnInfo(index = true)
    public String plantLocation;
    //Foreign Key
    @ColumnInfo(index = true)
    public long profile_image_id;

    public int selectedTheme;
    public String name;
    public String description;

    public Plant(String plantType, String plantLocation, long profile_image_id, int selectedTheme, String name, String description) {
        this.plantType = plantType;
        this.plantLocation = plantLocation;
        this.profile_image_id = profile_image_id;
        this.selectedTheme = selectedTheme;
        this.name = name;
        this.description = description;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
