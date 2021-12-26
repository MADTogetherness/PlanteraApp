package com.example.planteraapp.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

@Entity
public class Images {
    @PrimaryKey(autoGenerate = true)
    public long imageID;
    public long timestamp;
    public String imageName;
    public String imageData;


    public Images(String imageName, String imageData) {
        this.timestamp = System.currentTimeMillis();
        this.imageName = imageName;
        this.imageData = imageData;
    }

    @NonNull
    public String toString() {
        return imageName;
    }
}
