package com.example.planteraapp.Model.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Composite Key
@Entity
public class PlantLocation {
    @PrimaryKey
    @NonNull
    //Primary Key
    public String location;

    public PlantLocation(@NonNull String location) {
        this.location = location;
    }

    @Override
    @NonNull
    public String toString() {
        return location;
    }
}
