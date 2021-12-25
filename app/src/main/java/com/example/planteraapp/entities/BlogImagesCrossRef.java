package com.example.planteraapp.entities;

import androidx.room.Entity;

import com.example.planteraapp.entities.Blog;

@Entity(primaryKeys = {"blogID", "imageID"})
public class BlogImagesCrossRef {
    public long blogID;
    public long imageID;

    public BlogImagesCrossRef(long blogID, long imageID){
        this.blogID = blogID;
        this.imageID = imageID;
    }
}
