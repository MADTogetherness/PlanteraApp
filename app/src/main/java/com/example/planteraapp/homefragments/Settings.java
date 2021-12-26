package com.example.planteraapp.homefragments;

import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.PickAndReleaseImages;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.Relations.BlogWithImages;
import com.example.planteraapp.entities.Relations.PlantsWithBlogsANDImages;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    private TextView extraTextTV;
    private EditText blogDescET, plantIDET, nameToLoadET;
    private Button loadData, saveData, pickImages;
    private List<Images> blogImagesForUpload;
    private List<String> imageSource, imageName;
    private ShapeableImageView blogimg;
    private final long blogID = -1;


    private ArrayList<Uri> imageUris;

    private static final int PICK_IMAGES_CODE = 0;

    int pos = 0;

    private View view;
    private PickAndReleaseImages pickAndReleaseImages;
    private PlantDAO DAO;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        init();
    }

    public void init() {
        blogDescET = view.findViewById(R.id.blog_description);
        loadData = view.findViewById(R.id.btn_load_data);
        saveData = view.findViewById(R.id.btn_save_data);
        extraTextTV = view.findViewById(R.id.extra_text);
        nameToLoadET = view.findViewById(R.id.name_to_load);
        plantIDET = view.findViewById(R.id.plant_id);
        pickImages = view.findViewById(R.id.btn_pick_img);
        imageUris = new ArrayList<>();
        imageName = new ArrayList<>();
        imageSource = new ArrayList<>();
        blogimg = view.findViewById(R.id.profile_image);
        pickAndReleaseImages = new PickAndReleaseImages(requireContext(), DAO, requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(pickAndReleaseImages);
        pickImages.setOnClickListener(view -> {
            pickAndReleaseImages.pickImages();
        });

        saveData.setOnClickListener(view -> {
            extraTextTV.setText("");
            String desc = blogDescET.getText().toString().trim(),
                    plantName = plantIDET.getText().toString().trim();
            if (plantName.equals(""))
                return;
            Blog newBlog = new Blog(plantName, desc);
            try {
                long s = DAO.insertNewBlog(new Blog(plantName, desc));
                Log.d("insertB", String.valueOf(s));
                Toast.makeText(requireContext(), "NEW BLOG Inserted : " + newBlog.toString(), Toast.LENGTH_SHORT).show();
                if (pickAndReleaseImages.ImagePathsAvailable()) {
                    List<Long> success = pickAndReleaseImages.addBlogsAndImages(s);
                    for (long ss : success) Log.d("insertI", String.valueOf(ss));

                }
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        });

        loadData.setOnClickListener(view -> {
            extraTextTV.setText("");
            String name = nameToLoadET.getText().toString().trim();
            if (name.equals("")) return;
            Plant plant = DAO.getSinglePlantInstance(name);
            if (plant == null) {
                Toast.makeText(requireContext(), "INVALID NAME", Toast.LENGTH_SHORT).show();
                return;
            }
            PlantsWithBlogsANDImages plantBlogsWithImages = DAO.getPlantWithBlogsANDImages(plant.plantName);
            if (plantBlogsWithImages == null) {
                Toast.makeText(requireContext(), "INVALID NAME", Toast.LENGTH_SHORT).show();
                return;
            }
            List<BlogWithImages> all_blog = plantBlogsWithImages.blogs;
            for (BlogWithImages b : all_blog)
                extraTextTV.append("Blogs: " + b.blog.blogID + " : " + plant.plantName + " : " + b.blog.description + "\n");
            blogimg.setImageBitmap(AttributeConverters.StringToBitMap(all_blog.get(0).images.get(0).imageData));
        });


    }
}