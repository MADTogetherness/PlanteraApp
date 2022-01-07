package com.example.planteraapp.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.planteraapp.entities.BlogImagesCrossRef;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickAndReleaseImages implements DefaultLifecycleObserver {

    private List<ImageReferenceObject> multipleImages;
    private List<Bitmap> multipleBitMaps;
    private ImageReferenceObject singleImage;
    private Bitmap singleBitMap;
    private final Context context;
    private final PlantDAO DAO;
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetMultipleContent, mGetSingleContent;
    private List<Long> imageIDs;
    private Thread thread;

    public void onCreate(@NonNull LifecycleOwner owner) {
        mGetMultipleContent = mRegistry.register("key", owner, new ActivityResultContracts.GetMultipleContents(),
                uri -> {
                    if (uri == null || uri.size() == 0) return;
                    multipleImages = new ArrayList<>();
                    multipleBitMaps = new ArrayList<>();
                    Toast.makeText(context, "Uploading " + uri.size() + " Images", Toast.LENGTH_SHORT).show();
                    Toast success = Toast.makeText(context, "Images Uploaded Successfully", Toast.LENGTH_SHORT);
                    for (Uri URI : uri) {
                        try {
                            multipleBitMaps.add(BitmapFactory.decodeStream(context.getContentResolver().openInputStream(URI)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    thread = new Thread(() -> {
                        for (int i = 0; i < uri.size(); i++)
                            multipleImages.add(new ImageReferenceObject(uri.get(i).getPath().split(":")[1] + ".png", AttributeConverters.BitMapToString(multipleBitMaps.get(i))));
                        success.show();
                    });
                    thread.start();
                });

        mGetSingleContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Toast.makeText(context, "Uploading Image", Toast.LENGTH_SHORT).show();
                        Toast success = Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT);
                        try {
                            singleBitMap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        thread = new Thread(() -> {
                            singleImage = new ImageReferenceObject(uri.getPath().split(":")[1] + ".png", AttributeConverters.BitMapToString(singleBitMap));
                            success.show();
                        });
                        thread.start();
                    }
                });
    }

    public PickAndReleaseImages(Context context, @Nullable PlantDAO DAO, @NonNull ActivityResultRegistry mRegistry) {
        this.DAO = DAO;
        this.context = context;
        this.mRegistry = mRegistry;
    }

    private boolean addMultipleImages() {
        if (multipleImages == null || thread == null) {
            Toast.makeText(context, "An error occurred while uploading images", Toast.LENGTH_LONG).show();
            return false;
        } else if (thread.isAlive()) {
            Toast.makeText(context, "Please wait for images to get uploaded", Toast.LENGTH_LONG).show();
            return false;
        }
        imageIDs = new ArrayList<>();
        for (ImageReferenceObject img : multipleImages) {
            try {
                assert DAO != null;
                imageIDs.add(DAO.insertImage(new Images(img.getName(), img.getPath())));
            } catch (SQLiteConstraintException ex) {
                imageIDs.add(null);
                ex.printStackTrace();
            }
        }
        return true;
    }

    //    Call to pick images
    public void pickImages() {
        mGetMultipleContent.launch("image/*");
    }

    public void pickSingleImage() {
        mGetSingleContent.launch("image/*");
    }

    public Bitmap getSingleImageBitMap() {
        return singleBitMap;
    }

    public String getSingleImagePath() {
        return singleImage.getPath();
    }

    public List<Bitmap> getMultipleBitMaps() {
        return multipleBitMaps;
    }

    // Call Thread before then Thread.join() later call getSingleImagePath()
    public Thread getThread() {
        return thread;
    }

    //Make sure you have blog already inserted before proceeding here
    //Function used to capture images from gallery & save into database
    //Function will return BlogCrossReference successful inserts otherwise null
    public List<Long> addBlogsAndImages(long blogID) {
        if (!addMultipleImages())
            return null;

        List<Long> successIds = new ArrayList<>();
        for (long id : imageIDs) {
            try {
                assert DAO != null;
                successIds.add(DAO.insertNewBlogImageCrossRef(new BlogImagesCrossRef(blogID, id)));
            } catch (SQLiteConstraintException ex) {
                successIds.add(null);
                ex.printStackTrace();
            }
        }
        return successIds;
    }
}

class ImageReferenceObject {
    String name;
    String path;

    public ImageReferenceObject(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
