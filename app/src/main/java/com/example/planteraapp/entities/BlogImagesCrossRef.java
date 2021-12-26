package com.example.planteraapp.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.planteraapp.entities.Blog;

@Entity(primaryKeys = {"blogID", "imageID"})
public class BlogImagesCrossRef {
    @ColumnInfo(index = true)
    public long blogID;
    @ColumnInfo(index = true)
    public long imageID;

    public BlogImagesCrossRef(long blogID, long imageID){
        this.blogID = blogID;
        this.imageID = imageID;
    }
}
