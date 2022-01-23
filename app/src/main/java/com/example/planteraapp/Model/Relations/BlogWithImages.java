package com.example.planteraapp.Model.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.planteraapp.Model.Entities.Blog;
import com.example.planteraapp.Model.Entities.BlogImagesCrossRef;
import com.example.planteraapp.Model.Entities.Images;

import java.util.List;

public class BlogWithImages {
    @Embedded
    public Blog blog;
    @Relation(
            parentColumn = "blogID",
            entityColumn = "imageID",
            associateBy = @Junction(
                    value = BlogImagesCrossRef.class,
                    parentColumn = "blogID",
                    entityColumn = "imageID")
    )
    public List<Images> images;
}
