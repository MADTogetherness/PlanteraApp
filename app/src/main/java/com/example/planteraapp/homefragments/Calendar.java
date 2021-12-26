package com.example.planteraapp.homefragments;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
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
import com.example.planteraapp.Utilities.PickAndReleaseImages;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Calendar extends Fragment {
    private TextView imageNameTV, extraTextTV;
    private AutoCompleteTextView typeATV, locationATV;
    private EditText plantNameET, descriptionET, nameToLoadET;
    private ShapeableImageView plantImage;
    private View view;
    private PlantDAO DAO;
    private PickAndReleaseImages pickAndReleaseImages;

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
        extraTextTV = view.findViewById(R.id.extra_text);
        plantNameET = view.findViewById(R.id.plant_name);
        nameToLoadET = view.findViewById(R.id.name_to_load);
        typeATV = view.findViewById(R.id.type_spinner);
        locationATV = view.findViewById(R.id.location_spinner);
        descriptionET = view.findViewById(R.id.plant_description);
        plantImage = view.findViewById(R.id.profile_image);
        Button loadData = view.findViewById(R.id.btn_load_data);
        Button saveData = view.findViewById(R.id.btn_save_data);
        List<?> plantTypesInDatabase = DAO.getAllPlantTypes();
        List<?> plantLocationInDatabase = DAO.getAllPlantLocations();
        getType(plantTypesInDatabase);
        getLocation(plantLocationInDatabase);
        pickAndReleaseImages = new PickAndReleaseImages(requireContext(), null, requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(pickAndReleaseImages);
        plantImage.setOnClickListener(view -> pickAndReleaseImages.pickSingleImage());

        saveData.setOnClickListener(view -> {
            extraTextTV.setText("");
            String name = plantNameET.getText().toString().trim(), description = descriptionET.getText().toString().trim();
            String type = typeATV.getText().toString().trim(), location = locationATV.getText().toString().trim();
            if (name.equals("") || type.equals("") || description.equals("") || location.equals(""))
                return;
            PlantType newType = new PlantType(type);
            PlantLocation newLocation = new PlantLocation(location);
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
            if (pickAndReleaseImages.SingleImageAvailable()) {
                Plant plant = new Plant(name, pickAndReleaseImages.getGetSingleImage().get(1), newType.type, newLocation.location, 23455, description);
                long successful = DAO.insertNewPlant(plant)[0];
                Log.d("insertP", String.valueOf(successful));
                Toast.makeText(requireContext(), "NEW Plant Inserted : " + plant.toString(), Toast.LENGTH_SHORT).show();

                Reminder[] all_reminders = {
                        new Reminder(name, "Water", System.currentTimeMillis(), 203044456),
                        new Reminder(name, "Fertile", System.currentTimeMillis(), 203044456),
                        new Reminder(name, "Be With Them", System.currentTimeMillis(), 203044456)
                };

                long[] successfulR = DAO.insertReminders(all_reminders);
                Log.d("insertR", "Successful");
            } else {
                Toast.makeText(requireContext(), "Profile Image not set", Toast.LENGTH_SHORT).show();
            }
        });

        loadData.setOnClickListener(view -> {
            extraTextTV.setText("");
            String name = nameToLoadET.getText().toString().trim();
            if (name.equals("")) return;
            PlantsWithEverything plant = DAO.getAllPlantAttributes(name);
            if (plant == null) {
                Toast.makeText(requireContext(), "INVALID NAME", Toast.LENGTH_SHORT).show();
                return;
            }
            plantNameET.setText(plant.plant.plantName);
            descriptionET.setText(plant.plant.description);
            typeATV.setText(plant.type.type);
            locationATV.setText(plant.location.location);
            plantImage.setImageBitmap(AttributeConverters.StringToBitMap(plant.plant.profile_image));
            imageNameTV.setText(plant.plant.plantName + ".png");
            List<Reminder> all_reminders = plant.Reminders;
            for (Reminder r : all_reminders) {
                extraTextTV.append("Reminder : " + r.reminderID + "\n");
                extraTextTV.append(r.name + " plant today " + r.time + " repeat after " + r.repeatInterval + "\n\n");
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