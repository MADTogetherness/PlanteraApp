package com.example.planteraapp.Model.Entities;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
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
