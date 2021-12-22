package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Relation;

import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantType;

import java.util.List;

// There can be N Plants of 1 type : N - 1 (Plant - Type)
public class PlantAndType {
    @Embedded
    public PlantType type;
    @Relation(parentColumn = "type", entityColumn = "plantType")
    public List<Plant> plant;
}
