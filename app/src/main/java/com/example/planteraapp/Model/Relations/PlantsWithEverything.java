package com.example.planteraapp.Model.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.Model.Entities.Plant;
import com.example.planteraapp.Model.Entities.PlantLocation;
import com.example.planteraapp.Model.Entities.PlantType;
import com.example.planteraapp.Model.Entities.Reminder;

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
