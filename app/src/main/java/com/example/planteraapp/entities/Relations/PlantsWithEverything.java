package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

public class PlantsWithEverything {
    @Embedded
    public Plant plant;
    @Relation(parentColumn = "profile_image_id", entityColumn = "imageID")
    public Images profileImage;
    @Relation(parentColumn = "plantLocation", entityColumn = "location")
    public PlantLocation location;
    @Relation(parentColumn = "plantType", entityColumn = "type")
    public PlantType type;
    @Relation(parentColumn = "plantID", entityColumn = "plantID")
    public List<Reminder> Reminders;
}
