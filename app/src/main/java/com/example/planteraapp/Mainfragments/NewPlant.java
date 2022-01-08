package com.example.planteraapp.Mainfragments;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.MyPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.Utilities.PickAndReleaseImages;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantAndReminders;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPlant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPlant<TextView> extends Fragment {

    private android.widget.TextView imageNameTV, extraTextTV, addNewReminderTV;
    private AutoCompleteTextView typeATV, locationATV;
    private EditText plantNameET, descriptionET, nameToLoadET;
    private ShapeableImageView plantImage;
    private View view;
    private PlantDAO DAO;
    private PickAndReleaseImages pickAndReleaseImages;
    private LinearLayout reminderlinear;
    RecyclerView rv;

    String name[], time[], interval[], lastComp[];

    // Get the bitmap of image user has just selected from gallery
    private Bitmap singleBitMap;
    // The thread to load the image
    private Thread thread;
    // The image path is in this variable - get imagePath & store in the profile image field
    // Always check if(thread.isAlive()), if alive then toast user to try again later after image loads
    private String imagePath;
    // The mGetSingleContent Vairable
    // Call mGetSingleContent.launch("image/*")
    private ActivityResultLauncher<String> mGetSingleContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Toast.makeText(requireContext(), "Uploading Image", Toast.LENGTH_SHORT).show();
                    Toast success = Toast.makeText(requireContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT);
                    try {
                        singleBitMap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    thread = new Thread(() -> {
                        imagePath =  AttributeConverters.BitMapToString(singleBitMap);
                        success.show();
                    });
                    thread.start();
                }
            });

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
        name = getResources().getStringArray(R.array.name);
        time = getResources().getStringArray(R.array.time);
        interval = getResources().getStringArray(R.array.interval);
        lastComp = getResources().getStringArray(R.array.last);


        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        init();
    }

    public void init() {
        reminderlinear = view.findViewById(R.id.reminderlinearlayout);
        addNewReminderTV =  view.findViewById(R.id.new_reminder);
        imageNameTV =  view.findViewById(R.id.imagenameTV);
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
            mGetSingleContent.launch("image/*");
            plantImage.setImageBitmap(singleBitMap);
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

            if (singleBitMap.getWidth()>=0) {
                Plant plant = new Plant(name, imagePath, newType.type, newLocation.location, 23455, description);
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


    public void addRemindersToList(@NonNull List<Reminder> items) {
        reminderlinear.removeAllViews();
        if (items.size() != 0) {
            for (Reminder all_reminders : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, reminderlinear, false);
                android.widget.TextView remindername = item.findViewById(R.id.TVTitle);
                android.widget.TextView reminderdesc = item.findViewById(R.id.TVDesc);

                remindername.setText(all_reminders.name);
                reminderdesc.setText("Repeat: + "+ all_reminders.repeatInterval +" + days" + ", Time: " + all_reminders.time);
                reminderlinear.addView(item);
                //item.setOnClickListener(v -> {
                //    Intent intent = new Intent(requireContext().getApplicationContext());
                //    intent.putExtra("reminder name", all_reminders.name);
                //    startActivity(intent);
                //    requireActivity().overridePendingTransition(R.anim.fragment_enter_anim, R.anim.fragment_exit_anim);
                //});
            }
        } else ;
    }

}