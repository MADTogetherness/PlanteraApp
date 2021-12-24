package com.example.planteraapp.entities.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.Plant;

import java.util.List;

public class PlantsWithBlogsANDImages {
    @Embedded
    public Plant plant;
    @Relation(
            entity = Blog.class,
            parentColumn = "plantID",
            entityColumn = "plantID"
    )
    public List<BlogWithImages> blogs;
}
