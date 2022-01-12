package com.example.planteraapp.Mainfragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.MyPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.SubFragments.SetReminder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    Button  saveData, resetButton, updateImage;
    private ShapeableImageView plantImage;
    private View view;
    private PlantDAO DAO;
    private PickAndReleaseImages pickAndReleaseImages;
    private LinearLayout reminderlinear;
    RecyclerView rv;
    private List<Reminder> reminders;
    private int plantTheme;

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
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();

        init();
    }

    public void init() {
        reminders = new ArrayList<>();
        reminderlinear = view.findViewById(R.id.reminderlinearlayout);
        imageNameTV =  view.findViewById(R.id.imagenameTV);
        plantNameET = view.findViewById(R.id.plant_name);
        typeATV = view.findViewById(R.id.type_spinner);
        locationATV = view.findViewById(R.id.location_spinner);
        descriptionET = view.findViewById(R.id.plant_description);
        plantImage = view.findViewById(R.id.profile_image);
        saveData = view.findViewById(R.id.save_btn);
        resetButton = view.findViewById(R.id.reset_btn);
        updateImage = view.findViewById(R.id.new_picture);
        List<?> plantTypesInDatabase = DAO.getAllPlantTypes();
        List<?> plantLocationInDatabase = DAO.getAllPlantLocations();
        getType(plantTypesInDatabase);
        getLocation(plantLocationInDatabase);
        pickAndReleaseImages = new PickAndReleaseImages(requireContext(), null, requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(pickAndReleaseImages);

        resetButton.setOnClickListener(view -> {
            int drawableID = getContext().getResources().getIdentifier("img_default_profile_image", "drawable", getContext().getPackageName());
            plantImage.setImageResource(drawableID);
            reminders = new ArrayList<>();
            plantNameET.setEnabled(true);
            plantNameET.setText("");
            typeATV.setText("");
            locationATV.setText("");
            descriptionET.setText("");
            addRemindersToList(reminders);

        });

        updateImage.setOnClickListener(view ->{
            plantImage.setImageBitmap(singleBitMap);
        });

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

            if(singleBitMap == null){
                int drawableID = getContext().getResources().getIdentifier("img_default_profile_image", "drawable", getContext().getPackageName());
                singleBitMap = BitmapFactory.decodeResource(getContext().getResources(), drawableID);
                imagePath =  AttributeConverters.BitMapToString(singleBitMap);
                Toast.makeText(requireContext(), "Default Image used", Toast.LENGTH_SHORT).show();

            }

            if (singleBitMap.getWidth()>=0) {
                Plant plant = new Plant(name, imagePath, newType.type, newLocation.location, 23455, description);
                long successful = DAO.insertNewPlant(plant)[0];
                Log.d("insertP", String.valueOf(successful));
                Toast.makeText(requireContext(), "NEW Plant Inserted : " + plant.toString(), Toast.LENGTH_SHORT).show();

                for (Reminder singleRem: reminders ) {
                    long[] successfulR = DAO.insertReminders(singleRem);
                    Log.d("insertR", "Successful");
                    Toast.makeText(requireContext(), "NEW Reminder Inserted : " + singleRem.name + " for plant " + singleRem.plantName, Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(requireContext(), "Profile Image not set", Toast.LENGTH_SHORT).show();
            }
        });

        addRemindersToList(reminders);

        //addRemindersToList(DAO.getA);

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

        int i =0;
        boolean maxRemFlag = false;

        if (items.size() != 0) {
            plantNameET.setEnabled(false);
            for (Reminder all_reminders : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, reminderlinear, false);
                ImageView imgbell = item.findViewById(R.id.bell);
                android.widget.TextView tvTitle = item.findViewById(R.id.textView);
                android.widget.TextView tvDesc = item.findViewById(R.id.textView2);
                ImageView arrow = item.findViewById(R.id.arrow);
                View bubble = item.findViewById(R.id.bubble);
                item.setTag(String.valueOf(i));

                tvTitle.setText(all_reminders.name);
                tvDesc.setText("Repeat: + "+ all_reminders.repeatInterval +" + days" + ", Time: " + all_reminders.time);
                bubble.setBackgroundTintList(ColorStateList.valueOf(getColour(all_reminders.name)));

                if(i == 0){
                    ((RelativeLayout) imgbell.getParent()).setBackgroundResource(R.drawable.com_top_item_shape);
                } else if(i == 1){
                    ((RelativeLayout) imgbell.getParent()).setBackgroundResource(R.drawable.com_middle_items_shape);
                } else if(i ==2){
                    ((RelativeLayout) imgbell.getParent()).setBackgroundResource(R.drawable.com_bottom_item_shape);
                    maxRemFlag=true;
                }

                int finalI = i;
                item.setOnClickListener(v -> {
                    getReminder(finalI, reminders.get(finalI));
                });

                reminderlinear.addView(item);
                i++;

            }
            Toast.makeText(requireContext(), "asa", Toast.LENGTH_SHORT).show();
            //addNewReminderLayout(R.drawable.com_bottom_item_shape);

            if(!maxRemFlag){
                addNewReminderLayout(R.drawable.com_bottom_item_shape);
            }


            //if(items.size()>0 && items.size() < 3){
            //    addNewReminderLayout(R.drawable.com_bottom_item_shape);
            //}


        } else {
            addNewReminderLayout(R.drawable.com_round_shape_addrem);
        }




    }

    public void addNewReminderLayout(int BackgroundResource){
        View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, reminderlinear, false);
        ImageView imgbell = item.findViewById(R.id.bell);
        android.widget.TextView tvTitle = item.findViewById(R.id.textView);
        android.widget.TextView tvDesc = item.findViewById(R.id.textView2);
        ImageView arrow = item.findViewById(R.id.arrow);
        View bubble = item.findViewById(R.id.bubble);


        imgbell.setImageResource(R.drawable.ic_add_new_icon_48);
        tvTitle.setText("Add New Reminder");
        ((RelativeLayout) imgbell.getParent()).setBackgroundResource(BackgroundResource);


        tvDesc.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        bubble.setVisibility(View.GONE);

        item.setOnClickListener(v -> getReminder(-1));

        reminderlinear.addView(item);
    }


    public int getColour(String name){
        switch (name){
            case "Water":
            case "water":
            case "Aqua":
            case "aqua":
                return R.color.Reminder_Water;
            case "Soil":
            case "Fertile":
            case "soil":
            case "fertile":
                return R.color.Reminder_Soil;
            default: return R.color.Reminder_Other;
        }
    }

    /*
    public void getReminder(){
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            long time = bundle.getLong("time");
            long interval = bundle.getLong("interval");
            boolean togglenoti = bundle.getBoolean("notification");
            String name = bundle.getString("name");

            reminders.add(new Reminder(plantNameET.getText().toString(), name, time, interval));
            addRemindersToList(reminders);
        });
        requireActivity().findViewById(R.id.coordinator_layout).setVisibility(View.GONE);
        Navigation.findNavController(view).navigate(R.id.action_newPlant_fragment_to_setReminder, null,
                LauncherActivity.slide_in_out_fragment_options.build());
    }
     */

    public void getReminder(int position, Reminder... reminder){
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            boolean notificationEnabled = bundle.getBoolean("notificationEnabled");
            String reminderName = bundle.getString("reminderName");
            long time = bundle.getLong("time");
            long interval = bundle.getLong("interval");
            Reminder newReminder = new Reminder(plantNameET.getText().toString(), reminderName, time, interval);
            newReminder.notify = notificationEnabled;
            if(position >= 0) reminders.set(position, newReminder);
            else reminders.add(newReminder);

            addRemindersToList(reminders);

            Toast.makeText(requireContext(), "Reminder"+ (position>=0?" set to ":" edited for ") + reminderName, Toast.LENGTH_SHORT).show();
        });
        Bundle b = null;
        if(position>=0 && reminder!=null){
            b = new Bundle();
            b.putBoolean("notificationEnabled", reminder[0].notify);
            b.putString("reminderName", reminder[0].name);
            b.putLong("time", reminder[0].time);
            b.putLong("interval", reminder[0].repeatInterval);
        }
        requireActivity().findViewById(R.id.coordinator_layout).setVisibility(View.GONE);
        SetReminder setReminder = new SetReminder();
        setReminder.setArguments(b);

        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                .add(R.id.nav_controller, setReminder, "SubFrag")
                .addToBackStack(setReminder.getTag())
                .commit();



    }

}