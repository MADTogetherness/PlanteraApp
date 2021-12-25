package com.example.planteraapp.entities.Relations;

import androidx.room.Entity;

import com.example.planteraapp.entities.Blog;

@Entity(primaryKeys = {"blogID", "imageID"})
public class BlogImagesCrossRef {
    public long blogID;
    public long imageID;


}
