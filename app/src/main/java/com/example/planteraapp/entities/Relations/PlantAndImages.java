package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

// There can be 1 profile image of 1 plant : 1 - 1 (Plant - image)
public class PlantAndImages {
    @Embedded
    public Plant plant;
    @Relation(parentColumn = "profile_image_id", entityColumn = "imageID")
    public Images profileImage;
}
