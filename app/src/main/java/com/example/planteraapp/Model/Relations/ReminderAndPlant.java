package com.example.planteraapp.Model.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.Model.Entities.Plant;
import com.example.planteraapp.Model.Entities.Reminder;

public class ReminderAndPlant {
    @Embedded
    public Reminder reminder;
    @Relation(parentColumn = "plantName", entityColumn = "plantName")
    public Plant plant;

}
