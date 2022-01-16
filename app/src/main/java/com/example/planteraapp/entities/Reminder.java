package com.example.planteraapp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

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
    public long realEpochTime;
    public boolean notify;
    public long repeatInterval;

    /**
     * @param plantName:      Name of the plant on which reminder is set
     * @param name:           Reminder name
     * @param time:           The time of reminder - Fake time (only calculated from 0 milliseconds)
     * @param realEpochTime:  The real time of the day calculated from Unix Epoch
     * @param lastCompleted:  Last completed time, is always less than or equal to #{@realEpochTime}
     * @param repeatInterval: The repeating time in long from 0 milliseconds
     *                        Here the notification is set using realEpochTime, the time is shown using time.
     *                        We can know when was last time notification went off from lastCompleted. Everytime notification goes off
     *                        last completed changes & new realEpochTime set using repeatInterval
     *                        (new lastCompleted = realEpochTime)
     *                        (new realEpochTime = realEpochTime + repeatInterval)
     *
     */
    public Reminder(String plantName, String name, long time, long realEpochTime, long lastCompleted, long repeatInterval) {
        this.plantName = plantName;
        this.lastCompleted = lastCompleted;
        completedReminder = false;
        this.name = name;
        this.time = time;
        this.realEpochTime = realEpochTime;
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
