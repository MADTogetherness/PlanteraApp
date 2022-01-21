package com.example.planteraapp.Mainfragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.planteraapp.SubFragments.ColorTheme;
import com.example.planteraapp.SubFragments.SetReminder;
import com.example.planteraapp.Utilities.AlertReceiver;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NewPlant extends Fragment {
    public static String REMINDER_KEY = "getReminder";
    public static String THEME_KEY = "getTheme";
    private PlantDAO DAO;
    private TextView imageNameTV, themeNameTV;
    private AutoCompleteTextView typeATV, locationATV;
    private EditText plantNameET, descriptionET;
    private AppCompatButton saveData, resetData;
    private LinearLayout getNewPicture, getNewTheme;
    private ImageView plantImage;
    private LinearLayout reminderlinearlayout;
    private List<Reminder> reminders;
    private int plantTheme = R.style.Theme_PlanteraApp;
    private String plantName = "";
    private String oldplantName;
    // Get the bitmap of image user has just selected from gallery
    private Bitmap singleBitMap;
    // The thread to load the image
    private Thread thread = null;
    // The image path is in this variable - get imagePath & store in the profile image field
    // Always check if(thread.isAlive()), if alive then toast user to try again later after image loads
    private String imagePath;
    // The mGetSingleContent Variable
    // Call mGetSingleContent.launch("image/*")
    private PlantsWithEverything PWE;
    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> mGetSingleContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Toast.makeText(requireContext(), "Uploading Image", Toast.LENGTH_SHORT).show();
                    EnableDisable(false);
                    Toast success = Toast.makeText(requireContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT);
                    try {
                        singleBitMap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(uri));
                        plantImage.setImageBitmap(singleBitMap);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(requireContext(), "Image loading failed", Toast.LENGTH_SHORT).show();
                        plantImage.setBackgroundResource(R.drawable.img_default_profile_image);
                        e.printStackTrace();
                    }
                    thread = new Thread(() -> {
                        imagePath = AttributeConverters.BitMapToString(singleBitMap);
                        requireActivity().runOnUiThread(() -> {
                            imageNameTV.setText(uri.getPath().split(":")[1] + ".png");
                            EnableDisable(true);
                        });
                        success.show();
                    });
                    thread.start();
                }
            });


    public NewPlant() {/*Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_plant, container, false);
        init(view);
        setTypeAndLocations(typeATV, DAO.getAllPlantTypes());
        setTypeAndLocations(locationATV, DAO.getAllPlantLocations());
        //TODO: Are you sure you wanna reset? Dialog box
        resetData.setOnClickListener(v -> showDialog());
        getNewPicture.setOnClickListener(v -> getNewImage());
        plantImage.setOnClickListener(v -> getNewImage());
        getNewTheme.setOnClickListener(v -> getTheme());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveData.setOnClickListener(v -> {
            if (!CheckDataVariables())
                return;
            String temp = plantNameET.getText().toString().trim();
            String name = temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase();
            String description = descriptionET.getText().toString().trim();
            String type = typeATV.getText().toString().trim();
            String location = locationATV.getText().toString().trim();
            plantName = PWE != null ? PWE.plant.plantName : name;
            // Change plant Name for all reminders set
            ChangePlantNames();
            if (singleBitMap == null) {
                singleBitMap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.img_default_profile_image);
                imagePath = AttributeConverters.BitMapToString(singleBitMap);
            }
            if (PWE != null) updateData(type, location, name, description);
            else saveNewData(type, location, name, description);
        });
    }

    public boolean CheckDataVariables() {
        if (!saveData.isActivated()) {
            Toast.makeText(requireContext(), "Image is still uploading, Please wait", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (plantNameET.getText().toString().trim().isEmpty()) {
            plantNameET.setError("Name Field is required");
            LauncherActivity.openSoftKeyboard(requireContext(), plantNameET);
            return false;
        } else if (locationATV.getText().toString().trim().isEmpty()) {
            locationATV.setError("Location Field is required");
            LauncherActivity.openSoftKeyboard(requireContext(), locationATV);
            return false;
        } else if (typeATV.getText().toString().trim().isEmpty()) {
            typeATV.setError("Type Field is required");
            LauncherActivity.openSoftKeyboard(requireContext(), typeATV);
            return false;
        } else if (descriptionET.getText().toString().trim().isEmpty()) {
            descriptionET.setError("Type at least 2 words in bio");
            LauncherActivity.openSoftKeyboard(requireContext(), descriptionET);
            return false;
        }
        return true;
    }

    public void updateData(String type, String location, String name, String description) {
        PlantType newType = new PlantType(type);
        PlantLocation newLocation = new PlantLocation(location);
        saveData.setEnabled(false);
        try {
            DAO.updatePlantType(newType);
            Log.d("XX:updateORinsertT", "Successful");
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        try {
            DAO.updatePlantLocation(newLocation);
            Log.d("XX:updateORinsertL", "Successful");
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        Plant plant = new Plant(name, imagePath, newType.type, newLocation.location, plantTheme, description);
        try {
            DAO.updatePlant(plant);
            Log.d("XX:InsertP" + plantName, "Successful");
            for (Reminder singleRem : reminders) {
                int u = DAO.updateReminder(singleRem);
                if (u == 0)
                    DAO.insertReminders(singleRem);
                Log.d("XX:insertR", "Successful");
            }
            Log.d("XX:", " I am here");
            callForMyPlantActivity();
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

    }

    public void saveNewData(String type, String location, String name, String description) {
        long successT = -1, successL = -1, successP = -1;
        PlantType newType = new PlantType(type);
        PlantLocation newLocation = new PlantLocation(location);
        saveData.setEnabled(false);
        try {
            successT = DAO.insertPlantTypes(newType)[0];
            Log.d("XX:updateORinsertT", "Successful");
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        try {
            successL = DAO.insertPlantLocations(newLocation)[0];
            Log.d("XX:updateORinsertL", "Successful");
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        Plant plant = new Plant(name, imagePath, newType.type, newLocation.location, plantTheme, description);
        plant.plantName = name;
        try {
            successP = DAO.insertNewPlant(plant)[0];
            Log.d("XX:InsertP" + plantName, "Successful");
            for (Reminder singleRem : reminders) {
                long[] successfulR = DAO.insertReminders(singleRem);
                Log.d("XX:insertR", "Successful");
            }

            Log.d("XX:", " I am here");
            callForMyPlantActivity();
        } catch (SQLiteConstraintException e) {
            Log.d("XX:", " How did i come here?");
            saveData.setEnabled(true);
            Toast.makeText(requireContext(), "Plant with same name already exists", Toast.LENGTH_SHORT).show();
            if (successP == -1) {
                if (successT != -1) {
                    DAO.deleteType(newType);
                    Log.d("XX:deleteT", "Successful");
                }
                if (successL != -1) {
                    DAO.deleteLocation(newLocation);
                    Log.d("XX:deleteL", "Successful");
                }
            }
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void init(View view) {
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        imageNameTV = view.findViewById(R.id.new_image_name);
        themeNameTV = view.findViewById(R.id.theme_name);
        typeATV = view.findViewById(R.id.type_spinner);
        locationATV = view.findViewById(R.id.location_spinner);
        plantNameET = view.findViewById(R.id.plant_name);
        descriptionET = view.findViewById(R.id.plant_description);
        saveData = view.findViewById(R.id.save_btn);
        resetData = view.findViewById(R.id.reset_btn);
        getNewPicture = view.findViewById(R.id.new_picture);
        getNewTheme = view.findViewById(R.id.new_theme);
        plantImage = view.findViewById(R.id.profile_image);
        EnableDisable(true);
        reminderlinearlayout = view.findViewById(R.id.reminders);
        reminders = new ArrayList<>();
        if (getArguments() != null) {
            PWE = DAO.getAllPlantAttributes(getArguments().getString(LauncherActivity.plantNameKey));
            typeATV.setText(PWE.type.type, false);
            locationATV.setText(PWE.location.location, false);
            plantNameET.setText(PWE.plant.plantName);
            plantNameET.setEnabled(false);
            oldplantName = PWE.plant.plantName;
            descriptionET.setText(PWE.plant.description);
            reminders = PWE.Reminders;
            imageNameTV.setText(PWE.plant.plantName + ".jpg");
            plantTheme = PWE.plant.selectedTheme;
            themeNameTV.setText(LauncherActivity.getThemeName(plantTheme));
            imagePath = PWE.plant.profile_image;
            singleBitMap = AttributeConverters.StringToBitMap(imagePath);
            plantImage.setImageBitmap(singleBitMap);
        }
        addRemindersToList(reminders);
    }

    public void setTypeAndLocations(AutoCompleteTextView atv, List<?> theList) {
        final ArrayAdapter<?> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, theList);
        atv.setAdapter(arrayAdapter);
    }
    public void getNewImage() {
        if (plantImage.isActivated())
            mGetSingleContent.launch("image/*");
        else
            Toast.makeText(requireContext(), "A image is already in process, please wait", Toast.LENGTH_SHORT).show();
    }
    public void ChangePlantNames() {
        for (Reminder rem : reminders)
            rem.plantName = plantName;
    }
    public void EnableDisable(boolean val) {
        plantImage.setActivated(val);
        getNewPicture.setActivated(val);
        saveData.setActivated(val);
    }
    public void showDialog() {
        new AlertDialog.Builder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Reset Changes").setMessage("Are you sure you want to reset plant creation?")
                .setPositiveButton("Yes", (dialog, which) -> resetFields()).setNegativeButton("No", null)
                .show();
    }
    public void resetFields() {
        if (thread != null && thread.isAlive())
            thread.interrupt();
        plantImage.setImageResource(R.drawable.img_default_profile_image);
        imageNameTV.setText(R.string.default_img_name);
        reminders = new ArrayList<>();
        if (PWE == null)
            plantNameET.setText("");
        typeATV.setText("");
        locationATV.setText("");
        descriptionET.setText("");
        plantTheme = R.style.Theme_PlanteraApp;
        saveData.setEnabled(true);
        themeNameTV.setText(R.string.default_theme_name);
        addRemindersToList(reminders);
    }
    public static void setAlarm(Context context, Reminder rem, String location){
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("Title", "Reminder to " + rem.name);
        intent.putExtra("BigText", "Plant " + rem.plantName + " is used to your care and is waiting for you in the " + location);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) rem.reminderID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // TODO: Change the time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, rem.realEpochTime, rem.repeatInterval, pendingIntent);
    }
    public void callForMyPlantActivity() {
        for (Reminder rem : reminders) {
            setAlarm(requireContext(), rem, locationATV.getText().toString().trim());
        }
        resetFields();
        Intent intent = new Intent(requireActivity(), MyPlant.class);
        intent.putExtra("plantName", plantName);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity(), plantImage, "image").toBundle());
        if (PWE != null)
            requireActivity().finish();
    }
    @SuppressLint("SetTextI18n")
    public void addRemindersToList(@NonNull List<Reminder> items) {
        reminderlinearlayout.removeAllViews();
        int i = 0;
        if (items.size() == 0)
            addNewReminderViewPrompt(getDrawableForReminder(-1));
        else {
            for (Reminder all_reminders : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, reminderlinearlayout, false);
                TextView tvTitle = item.findViewById(R.id.reminder_name);
                TextView tvDesc = item.findViewById(R.id.reminder_desc);
                View bubble = item.findViewById(R.id.bubble);

                tvTitle.setText(all_reminders.name);
                tvDesc.setText("Repeat After " + AttributeConverters.toDays(all_reminders.repeatInterval) + " day(s)" + "\nTime: " + AttributeConverters.getReadableTime(all_reminders.time) + "\nReminder is " + (all_reminders.notify ? "on" : "off"));
                bubble.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), LauncherActivity.getColour(all_reminders.name)));
                ((RelativeLayout) tvTitle.getParent()).setBackgroundResource(getDrawableForReminder(i));

                int finalI = i;
                item.setOnClickListener(v -> getReminder(finalI, reminders.get(finalI)));
                reminderlinearlayout.addView(item);

                if (i == items.size() - 1 && items.size() <= 2)
                    addNewReminderViewPrompt(getDrawableForReminder(2));
                i++;

            }
        }
    }
    public void addNewReminderViewPrompt(int BackgroundResource) {
        View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, reminderlinearlayout, false);
        ImageView imgBell = item.findViewById(R.id.bell);
        TextView tvTitle = item.findViewById(R.id.reminder_name);
        TextView tvDesc = item.findViewById(R.id.reminder_desc);
        View bubble = item.findViewById(R.id.bubble);
        imgBell.setImageResource(R.drawable.ic_add_new_icon_24);
        tvTitle.setText("Add New Reminder");
        tvDesc.setVisibility(View.GONE);
        bubble.setVisibility(View.GONE);
        item.setBackgroundResource(BackgroundResource);
        item.setOnClickListener(v -> getReminder(-1));
        reminderlinearlayout.addView(item);
    }
    public int getDrawableForReminder(int i) {
        switch (i) {
            case 0:
                return R.drawable.com_top_item_shape;
            case 1:
                return R.drawable.com_middle_items_shape;
            case 2:
                return R.drawable.com_bottom_item_shape;
            default:
                return R.drawable.com_round_color_section;
        }
    }
    public void getReminder(int position, Reminder... reminder) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            Reminder newReminder = AttributeConverters.getGsonParser().fromJson(bundle.getString(NewPlant.REMINDER_KEY), Reminder.class);
            if (position >= 0 && PWE != null) newReminder.reminderID = reminder[0].reminderID;
            if (position >= 0) reminders.set(position, newReminder);
            else reminders.add(newReminder);
            addRemindersToList(reminders);
            Toast.makeText(requireContext(), "Reminder" + (position < 0 ? " set to " : " edited for ") + newReminder.name, Toast.LENGTH_SHORT).show();
        });
        Bundle b = new Bundle();
        if(position>=0 && reminder!=null)
            b.putString(NewPlant.REMINDER_KEY, AttributeConverters.getGsonParser().toJson(reminder[0]));
        openSubFragment(new SetReminder(), b, fm);
    }

    public void getTheme() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            plantTheme = bundle.getInt(THEME_KEY);
            themeNameTV.setText(LauncherActivity.getThemeName(plantTheme));
            Toast.makeText(requireContext(), "Theme set to " + LauncherActivity.getThemeName(plantTheme), Toast.LENGTH_SHORT).show();
        });
        Bundle b = new Bundle();
        b.putInt(THEME_KEY, plantTheme);
        openSubFragment(new ColorTheme(), b, fm);
    }
    public void openSubFragment(Fragment fg, Bundle bn, FragmentManager fm) {
        requireActivity().findViewById(R.id.coordinator_layout).setVisibility(View.GONE);
        fg.setArguments(bn);
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                .add(R.id.nav_controller, fg, "SubFrag")
                .addToBackStack(fg.getTag())
                .commit();
    }

}