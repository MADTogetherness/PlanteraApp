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
    @Relation(parentColumn = "plantLocation", entityColumn = "location")
    public PlantLocation location;
    @Relation(parentColumn = "plantType", entityColumn = "type")
    public PlantType type;
    @Relation(parentColumn = "plantName", entityColumn = "plantName")
    public List<Reminder> Reminders;
}
