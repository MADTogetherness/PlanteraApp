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

    private List<List<String>> imagePaths;
    private List<String> getSingleImage;
    private final Context context;
    private final PlantDAO DAO;
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetMultipleContent, mGetSingleContent;
    private Thread thread;

    public void onCreate(@NonNull LifecycleOwner owner) {
        mGetMultipleContent = mRegistry.register("key", owner, new ActivityResultContracts.GetMultipleContents(),
                uri -> {
                    if (uri == null || uri.size() == 0) return;
                    imagePaths = new ArrayList<>();
                    Toast.makeText(context, "Uploading " + uri.size() + " Images", Toast.LENGTH_SHORT).show();
                    Toast success = Toast.makeText(context, "Images Uploaded Successfully", Toast.LENGTH_SHORT);
                    thread = new Thread(() -> {
                        String imageName;
                        String imageSource;
                        for (Uri URI : uri) {
                            try {
                                imageName = URI.getPath().split(":")[1] + ".png";
                                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(URI));
                                imageSource = AttributeConverters.BitMapToString(bitmap);
                                Log.d("image", "name: " + imageName);
                                imagePaths.add(Arrays.asList(imageName, imageSource));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        success.show();
                    });
                    thread.start();
                });

        mGetSingleContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        getSingleImage = new ArrayList<>();
                        Toast.makeText(context, "Uploading 1 Image", Toast.LENGTH_SHORT).show();
                        Toast success = Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT);
                        thread = new Thread(() -> {
                            String imageName = "";
                            String imageSource = "";
                            try {
                                imageName = uri.getPath().split(":")[1] + ".png";
                                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                                imageSource = AttributeConverters.BitMapToString(bitmap);
                                Log.d("image", "name: " + imageName);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            getSingleImage.add(imageName);
                            getSingleImage.add(imageSource);
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

    private List<Long> addMultipleImages() {
        if (imagePaths == null || thread == null) {
            return null;
        } else if (thread.isAlive()) {
            return null;
        }
        List<Long> imageIDs = new ArrayList<>();
        for (List<String> paths : imagePaths) {
            try {
                assert DAO != null;
                imageIDs.add(DAO.insertImage(new Images(paths.get(0), paths.get(1))));
            } catch (SQLiteConstraintException ex) {
                imageIDs.add(null);
                ex.printStackTrace();
            }
        }
        return imageIDs;
    }

    //    Call to pick images
    public void pickImages() {
        mGetMultipleContent.launch("image/*");
    }

    public void pickSingleImage() {
        mGetSingleContent.launch("image/*");
    }

    public boolean ImagePathsAvailable() {
        return imagePaths != null;
    }

    public List<String> getGetSingleImage() {
        return getSingleImage;
    }

    public boolean SingleImageAvailable() {
        return getSingleImage != null;
    }

    //Make sure you have blog already inserted before proceeding here
    //Function used to capture images from gallery & save into database
    //Function will return BlogCrossReference successful inserts otherwise null
    public List<Long> addBlogsAndImages(long blogID) {
        List<Long> ids = addMultipleImages();
        if (ids == null) {
            Toast.makeText(context, "Images are still uploading. Please Wait", Toast.LENGTH_SHORT).show();
            return null;
        }


        List<Long> successIds = new ArrayList<>();
        for (long id : ids) {
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
