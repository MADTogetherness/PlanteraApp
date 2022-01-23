package com.example.planteraapp.Model.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Blog.class,
                        parentColumns = "blogID",
                        childColumns = "blogID",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Images.class,
                        parentColumns = "imageID",
                        childColumns = "imageID",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }, indices = {@Index(value = "blogID"), @Index(value = "imageID")}
)
public class BlogImagesCrossRef {
    @PrimaryKey
    public long id;
    public long blogID;
    public long imageID;

    public BlogImagesCrossRef(long blogID, long imageID) {
        this.id = System.currentTimeMillis();
        this.blogID = blogID;
        this.imageID = imageID;
    }
}
