package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.Images;

import java.util.List;

public class BlogWithImages {
    @Embedded
    public Blog blog;
    @Relation(
            parentColumn = "blogID",
            entityColumn = "imageID",
            associateBy = @Junction(BlogImagesCrossRef.class)
    )
    public List<Images> images;
}
