package com.example.planteraapp.Model.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.planteraapp.Model.Entities.Blog;
import com.example.planteraapp.Model.Entities.Plant;

import java.util.List;

public class PlantsWithBlogsANDImages {
    @Embedded
    public Plant plant;
    @Relation(
            entity = Blog.class,
            parentColumn = "plantName",
            entityColumn = "plantName"
    )
    public List<BlogWithImages> blogs;
}
