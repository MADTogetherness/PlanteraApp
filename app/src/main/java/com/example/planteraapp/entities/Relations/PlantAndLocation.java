package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;

import java.util.List;

// There can be N Plants in 1 location : N - 1 (Plant - location)
public class PlantAndLocation {
    @Embedded
    public PlantLocation location;
    @Relation(parentColumn = "location", entityColumn = "plantLocation")
    public List<Plant> plant;
}
