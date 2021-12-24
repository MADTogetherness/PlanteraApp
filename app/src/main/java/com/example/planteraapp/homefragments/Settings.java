package com.example.planteraapp.homefragments;

import android.database.sqlite.SQLiteConstraintException;
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
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantsWithBlogsANDImages;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    private TextView extraTextTV;
    private EditText blogDescET, plantIDET, nameToLoadET;
    private Button loadData, saveData;

    private View view;
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

        saveData.setOnClickListener(view -> {
            extraTextTV.setText("");
            String desc = blogDescET.getText().toString().trim();
            long pid = Long.parseLong(plantIDET.getText().toString().trim());
            if (desc.equals(""))
                return;

            Blog newBlog = new Blog(pid, desc);
            //PlantType newType = new PlantType(type);
            //Images image = new Images(imageName, imageSource);

            try {
                long s = DAO.InsertNewBlog(newBlog)[0];
                Log.d("insertB", String.valueOf(s));
                Toast.makeText(requireContext(), "NEW BLOG Inserted : " + newBlog.toString(), Toast.LENGTH_SHORT).show();
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }


        });

        loadData.setOnClickListener(view -> {

            extraTextTV.setText("");
            String name = nameToLoadET.getText().toString().trim();
            //long name = Long.parseLong(nameToLoadET.getText().toString().trim());

            if (name.equals("")) return;

            long tempid;



            PlantsWithEverything plant = DAO.getAllPlantAttributes(name);
            if (plant == null) {
                Toast.makeText(requireContext(), "INVALID NAME", Toast.LENGTH_SHORT).show();
                return;
            }

            List<PlantsWithBlogsANDImages> plantbi = DAO.getPlantsWithBlogsANDImages(plant.plant.plantID);
            if (plantbi == null) {
                Toast.makeText(requireContext(), "INVALID NAME", Toast.LENGTH_SHORT).show();
                return;
            }



            List<Blog> all_blogsplantID = DAO.getAllBlogsPlantID(plant.plant.plantID);
            for (Blog b : all_blogsplantID) {
                tempid = b.plantID;
                extraTextTV.append("Blogs: " + b.blogID + " : " + b.plantID + " : " + b.description + "\n");
            }

            extraTextTV.append("" + plantbi.get(0).plant.plantID);
            ////retrieve singular blog only via ID
            //List<Blog> all_blogs = DAO.getAllBlogs(Long.parseLong(name));

            //for (Blog b : all_blogs) {
            //    extraTextTV.append("Blogs: " + b.blogID + " : " + b.plantID + " : " + b.description);
            //}



        });
    }
}