package com.example.planteraapp.entities;

import androidx.room.Entity;

import com.example.planteraapp.entities.Blog;

@Entity(primaryKeys = {"bID", "iID"})
public class BlogImagesCrossRef {
    public long bID;
    public long iID;
}
