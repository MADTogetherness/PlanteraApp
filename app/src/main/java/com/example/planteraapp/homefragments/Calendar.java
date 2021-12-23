package com.example.planteraapp.homefragments;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Calendar extends Fragment {
    private TextView imageNameTV;
    private AutoCompleteTextView typeATV, locationATV;
    private EditText plantNameET, descriptionET;
    private ShapeableImageView plantImage;
    private Button loadData, saveData;
    private View view;
    private PlantDAO DAO;
    private final int imageChanged = 0;
    //VARIABLES TO SAVE
    private String imageSource, imageName;
    private final ActivityResultLauncher<String> mSetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            InputStream is = null;
            try {
                imageName = uri.getPath().split(":")[1];
                is = requireContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageSource = AttributeConverters.BitMapToString(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            imageNameTV.setText(imageName);
            plantImage.setImageURI(uri);
        }
    });

    public Calendar() {/*Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        init();
    }


    public void init() {
        imageNameTV = view.findViewById(R.id.image_name);
        plantNameET = view.findViewById(R.id.plant_name);
        typeATV = view.findViewById(R.id.type_spinner);
        locationATV = view.findViewById(R.id.location_spinner);
        descriptionET = view.findViewById(R.id.plant_description);
        plantImage = view.findViewById(R.id.profile_image);
        loadData = view.findViewById(R.id.btn_load_data);
        saveData = view.findViewById(R.id.btn_save_data);
        List<?> plantTypesInDatabase = DAO.getAllPlantTypes();
        List<?> plantLocationInDatabase = DAO.getAllPlantLocations();
        getType(plantTypesInDatabase);
        getLocation(plantLocationInDatabase);
        plantImage.setOnClickListener(view -> mSetContent.launch("image/*"));

        saveData.setOnClickListener(view -> {
            String name = plantNameET.getText().toString().trim(), description = descriptionET.getText().toString().trim();
            String type = typeATV.getText().toString().trim(), location = locationATV.getText().toString().trim();

            PlantType newType = new PlantType(type);
            PlantLocation newLocation = new PlantLocation(location);
            Images image = new Images(imageName, imageSource);

            try {
                long s = DAO.insertPlantTypes(newType)[0];
                Log.d("insertT", String.valueOf(s));
                Toast.makeText(requireContext(), "NEW TYPE Inserted : " + newType.toString(), Toast.LENGTH_SHORT).show();
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }

            try {
                long s = DAO.insertPlantLocations(newLocation)[0];
                Log.d("insertL", String.valueOf(s));
                Toast.makeText(requireContext(), "NEW Location Inserted : " + newLocation.toString(), Toast.LENGTH_SHORT).show();
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }

            long s = DAO.insertPlantProfileImages(image)[0];
            Plant plant = new Plant(newType.type, newLocation.location, s, 23455, name, description);

            long successfull = DAO.InsertNewPlant(plant)[0];
            Log.d("insertP", String.valueOf(successfull));
            if (successfull != -1) {
                Toast.makeText(requireContext(), "NEW Plant Inserted : " + plant.toString(), Toast.LENGTH_SHORT).show();
            }


        });
    }


    public void getLocation(List<?> plantLocationInDatabase) {
        if (plantLocationInDatabase.size() == 0) {
            plantLocationInDatabase = Arrays.asList("Garden", "Balcony", "Roof");
        }
        ArrayAdapter<?> adapter = new ArrayAdapter<>(requireContext().getApplicationContext(), android.R.layout.select_dialog_item, plantLocationInDatabase);
        locationATV.setAdapter(adapter);
    }

    public void getType(List<?> plantTypesInDatabase) {
        if (plantTypesInDatabase.size() == 0) {
            plantTypesInDatabase = Arrays.asList("Flowering", "Fern", "Vascular");
        }
        ArrayAdapter<?> adapter = new ArrayAdapter<>(requireContext().getApplicationContext(), android.R.layout.select_dialog_item, plantTypesInDatabase);
        typeATV.setAdapter(adapter);
    }


}