package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.Reminder;

public class ReminderAndPlant {
    @Embedded
    public Reminder reminder;
    @Relation(parentColumn = "plantName", entityColumn = "plantName")
    public Plant plant;

}
