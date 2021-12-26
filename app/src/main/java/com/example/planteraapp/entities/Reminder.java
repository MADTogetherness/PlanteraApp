package com.example.planteraapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Plant.class,
                        parentColumns = "plantName",
                        childColumns = "plantName",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    //Primary Key
    public long reminderID;
    //Foreign Key
    @ColumnInfo(index = true)
    public String plantName;
    public long lastCompleted;
    public boolean completedReminder;
    public String name;
    public long time;
    public boolean notify;
    public long repeatInterval;


    public Reminder(String plantName, String name, long time, long repeatInterval) {
        this.plantName = plantName;
        lastCompleted = System.currentTimeMillis();
        completedReminder = false;
        this.name = name;
        this.time = time;
        notify = true;
        this.repeatInterval = repeatInterval;
    }

    @Override
    @NonNull
    public String toString() {
        return "Reminder{" +
                "reminderID=" + reminderID +
                ", lastCompleted=" + lastCompleted +
                ", completedReminder=" + completedReminder +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", notify=" + notify +
                ", repeatInterval=" + repeatInterval +
                '}';
    }
}
