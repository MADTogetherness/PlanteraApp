package com.example.planteraapp.Model.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Composite Key
@Entity
public class PlantType {
    @PrimaryKey
    @NonNull
    //Primary Key
    public String type;

    public PlantType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }
}
