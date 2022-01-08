package com.example.planteraapp.Mainfragments;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPlant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPlant<TextView> extends Fragment {

    private TextView imageNameTV, extraTextTV;
    private AutoCompleteTextView typeATV, locationATV;
    private EditText plantNameET, descriptionET, nameToLoadET;
    private ShapeableImageView plantImage;
    private View view;
    private PlantDAO DAO;
    private PickAndReleaseImages pickAndReleaseImages;

    RecyclerView rv;

    String name[], time[], interval[], lastComp[];



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewPlant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPlant.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPlant newInstance(String param1, String param2) {
        NewPlant fragment = new NewPlant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_plant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        rv = view.findViewById(R.id.RVReminder);
        name = getResources().getStringArray(R.array.name);
        time = getResources().getStringArray(R.array.time);
        interval = getResources().getStringArray(R.array.interval);
        lastComp = getResources().getStringArray(R.array.last);


        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        init();
    }

    public void init() {
        imageNameTV = (TextView) view.findViewById(R.id.imageTV);
        plantNameET = view.findViewById(R.id.plant_name);
        typeATV = view.findViewById(R.id.type_spinner);
        locationATV = view.findViewById(R.id.location_spinner);
        descriptionET = view.findViewById(R.id.plant_description);
        plantImage = view.findViewById(R.id.profile_image);
        Button saveData = view.findViewById(R.id.save_btn);
        List<?> plantTypesInDatabase = DAO.getAllPlantTypes();
        List<?> plantLocationInDatabase = DAO.getAllPlantLocations();
        getType(plantTypesInDatabase);
        getLocation(plantLocationInDatabase);
        pickAndReleaseImages = new PickAndReleaseImages(requireContext(), null, requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(pickAndReleaseImages);
        plantImage.setOnClickListener(view -> {
            pickAndReleaseImages.pickSingleImage();
        });

        saveData.setOnClickListener(view -> {
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

            Log.d("iamge", String.valueOf(pickAndReleaseImages.SingleImageAvailable()));

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