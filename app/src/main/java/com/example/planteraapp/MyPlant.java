package com.example.planteraapp;

import static com.example.planteraapp.Utilities.AttributeConverters.StringToBitMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.SubFragments.SetReminder;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.BlogImagesCrossRef;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.Relations.BlogWithImages;
import com.example.planteraapp.entities.Reminder;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyPlant extends AppCompatActivity {
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", };
    private Plant plant;
    private PlantDAO DAO;
    private String plantName;
    Button deleteBtn, editBtn, editThemeBtn;
    ImageButton closeBtn, item2editreminderBtn, add_new_timeline;
    TextView themenameTV, nextreminderTV, itemdateCreatedTV, plantNameTV, plantdescriptionTV, item2remindernameTV, item2reminderdescTV;
    ImageView plantImage, task_doneIV;

    private LinearLayout reminderlinearlayout;
    private List<Reminder> reminders;
    private LinearLayout timelinelinearlayout;
    private List<Blog> timelines;

    // Get the bitmap of image user has just selected from gallery
    private Bitmap singleBitMap;
    // The thread to load the image
    private Thread thread;
    // The image path is in this variable - get imagePath & store in the profile image field
    // Always check if(thread.isAlive()), if alive then toast user to try again later after image loads
    private String imagePath;
    // The mGetSingleContent Variable
    // Call mGetSingleContent.launch("image/*")
    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> mGetSingleContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
                    Toast success = Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT);
                    try {
                        singleBitMap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "Image loading failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    thread = new Thread(() -> {
                        imagePath = AttributeConverters.BitMapToString(singleBitMap);
                        success.show();
                    });
                    thread.start();
                }
            });





    private boolean isActivityRecreated = false;
    private int selectedTheme = R.style.Theme_PlanteraApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: GET THE PLANT NAME FROM PREVIOUS ACTIVITY NOT DATABASE
        plantName = getIntent().getStringExtra("plantName");
        DAO = AppDatabase.getInstance(this).plantDAO();
        plant = DAO.getSinglePlantInstance(plantName);
        ChangeThemeFromDB(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plant);
        init();
    }

    public void init(){
        Context context = this;

        deleteBtn = findViewById(R.id.delete_btn);
        editBtn = findViewById(R.id.edit_btn);
        themenameTV = findViewById(R.id.themenameTV);
        plantImage = findViewById(R.id.plant_image);
        plantNameTV = findViewById(R.id.plantNameTV);
        plantdescriptionTV = findViewById(R.id.plantdescriptionTV);
        nextreminderTV = findViewById(R.id.nextReminder);
        add_new_timeline = findViewById(R.id.add_new_timeline);

        reminderlinearlayout = findViewById(R.id.reminders);
        reminders = new ArrayList<>();

        editThemeBtn = findViewById(R.id.editPlantTheme);

        timelinelinearlayout= findViewById(R.id.timelineLayout);
        timelines = new ArrayList<>();






        item2remindernameTV = findViewById(R.id.reminder_name);
        item2reminderdescTV = findViewById(R.id.reminder_desc);


        //itemdateCreatedTV.setText(getDate(plant.dateOfCreation));
        plantNameTV.setText(plant.plantName);
        plantdescriptionTV.setText(plant.description);
        plantImage.setImageBitmap(StringToBitMap(plant.profile_image));

        reminders = DAO.getRemindersOfPlant(plant.plantName);
        addRemindersToList(reminders);

        timelines = DAO.getAllBlogsPlantID(plant.plantName);
        Collections.reverse(timelines);
        //timelines.add(0, new Blog(plant.plantName ,"a description"));
        //timelines.add(0, new Blog(plant.plantName ,"a second description"));
        addBlogsToList(timelines, false);






        add_new_timeline.setOnClickListener(v->{
            addBlogsToList(timelines, true);
        });
    }



    @Override
    protected void onResume() {
        if (isActivityRecreated) ChangeThemeFromDB(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActivityRecreated = true;
        super.onPause();
    }

    private void ChangeThemeFromDB(boolean recreate) {
        int theme = DAO.getSelectedThemeOfUser(plantName);
        if (theme != 0 && theme == selectedTheme)
            return;
        setTheme(theme == 0 ? R.style.Theme_PlanteraApp : theme);
        if (recreate) {
            overridePendingTransition(R.anim.fragment_enter_anim, R.anim.fragment_exit_anim);
            isActivityRecreated = false;
            recreate();
        }
    }

    @Override
    public void setTheme(int resId) {
        selectedTheme = resId;
        super.setTheme(resId);
    }

    public String getDate(Long datel){
        Date date = new Date(datel);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dayValue = formatter.format(date).substring(0,2);
        String monthValue = formatter.format(date).substring(3,5);
        String yearValue = formatter.format(date).substring(6,10);

        return dayValue + " " + months[Integer.parseInt(monthValue)] + " " + yearValue;
    }

    public void addRemindersToList(@NonNull List<Reminder> items) {
        reminderlinearlayout.removeAllViews();
        int i = 0;
        if (items.size() == 0){
            View newitem = getLayoutInflater().inflate(R.layout.reminder_item_myplant, reminderlinearlayout, false);
            newitem.setTag(String.valueOf(i));

            TextView tvTitle = newitem.findViewById(R.id.reminder_name);
            TextView tvDesc = newitem.findViewById(R.id.reminder_desc);

            tvTitle.setText("No reminders exist");
            ((RelativeLayout) tvTitle.getParent()).setBackgroundResource(R.drawable.com_round_shape);
        }

        else {
            for (Reminder all_reminders : items) {
                Context context = this;
                View item = getLayoutInflater().inflate(R.layout.reminder_item_myplant, reminderlinearlayout, false);
                item.setTag(String.valueOf(i));

                TextView tvTitle = item.findViewById(R.id.reminder_name);
                TextView tvDesc = item.findViewById(R.id.reminder_desc);

                tvTitle.setText(all_reminders.name);
                tvDesc.setText("Repeat: " + all_reminders.repeatInterval + " days" + "\nTime: " + all_reminders.time);
                ((RelativeLayout) tvTitle.getParent()).setBackgroundResource(getDrawableForReminder(i));

                int finalI = i;
                // TODO: change later
                //item.setOnClickListener(v -> getReminder(finalI, reminders.get(finalI)));
                reminderlinearlayout.addView(item);

                i++;

            }
        }
    }




    /*
    public void getReminder(int position, Reminder... reminder) {
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
            Toast.makeText(requireContext(), "Reminder" + (position < 0 ? " set to " : " edited for ") + reminderName, Toast.LENGTH_SHORT).show();
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

     */


    public int getDrawableForReminder(int i) {
        switch (i) {
            case 0:
                return R.drawable.com_top_item_shape;
            case 1:
                return R.drawable.com_middle_items_shape;
            case 2:
                return R.drawable.com_bottom_item_shape;
            default:
                return R.drawable.com_round_shape;
        }
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
            default:
                return R.color.Reminder_Other;
        }
    }


    public void addBlogsToList(@NonNull List<Blog> items, boolean newBlog) {

        timelinelinearlayout.removeAllViews();

        if(newBlog){
            View itemnew = getLayoutInflater().inflate(R.layout.com_layout_new_timeline, timelinelinearlayout, false);

            TextView dateTV = itemnew.findViewById(R.id.timeline_date);
            TextView descTV = itemnew.findViewById(R.id.timeline_desc);


            itemnew.findViewById(R.id.add_pictureBtn).setOnClickListener(v->{
                mGetSingleContent.launch("image/*");
            });


            itemnew.findViewById(R.id.saveblogBtn).setOnClickListener(v->{
                saveBlog(itemnew);
            });
            timelinelinearlayout.addView(itemnew);
        }

        int i = 0;
        if (items.size() == 0)
            ;
        else {
            int j=0;
            for (Blog all_blogs : items) {

                Context context = this;
                View item = getLayoutInflater().inflate(R.layout.com_layout_timeline, timelinelinearlayout, false);
                item.setTag(String.valueOf(j));

                TextView dateTV = item.findViewById(R.id.timeline_date);
                TextView descTV = item.findViewById(R.id.timeline_desc);
                descTV.setText(all_blogs.description);
                dateTV.setText(String.valueOf(getDate(all_blogs.dateCreated)));

                LinearLayout imageLayout = item.findViewById(R.id.timeline_images);

                List<BlogWithImages> blogWithImages = DAO.getBlogwWithImages(all_blogs.blogID);

                List<Images> images = DAO.getBlogwWithImages(all_blogs.blogID).get(j).images;

                for(Images img: images){
                    Toast.makeText(this, "Image haha", Toast.LENGTH_SHORT).show();

                    View itemimg = getLayoutInflater().inflate(R.layout.com_layout_blogimg, imageLayout, false);
                    ImageView imgtoadd= itemimg.findViewById(R.id.imgview);
                    imgtoadd.setImageBitmap(StringToBitMap(img.imageData));

                    imageLayout.addView(itemimg);
                }

                int finalI = i;
                // TODO: change later
                //item.setOnClickListener(v -> getReminder(finalI, reminders.get(finalI)));
                timelinelinearlayout.addView(item);

            }
        }
    }

    public long saveBlog(View itemnew){
        Blog blogtoAdd = new Blog(plant.plantName, ((TextView)itemnew.findViewById(R.id.timeline_desc)).getText().toString());


        long blogid = DAO.insertBlogs(blogtoAdd)[0];

        Toast.makeText(getApplicationContext(), "blog added " + blogid, Toast.LENGTH_SHORT).show();
        timelines = DAO.getAllBlogsPlantID(plant.plantName);
        Collections.reverse(timelines);
        addBlogsToList(timelines, false);


        long successP = -1;
        if (singleBitMap != null) {
            Images image = new Images("blogimg" + plant.plantName, imagePath);
            try {
                long imgid = DAO.insertImage(image);
                Toast.makeText(this, "Image added to image " + imgid, Toast.LENGTH_SHORT).show();

                BlogImagesCrossRef BIRef = new BlogImagesCrossRef(blogid, imgid);
                long refid = DAO.insertNewBlogImageCrossRef(BIRef);
                Toast.makeText(this, "CrossRef Added " + refid, Toast.LENGTH_SHORT).show();


            } catch (SQLiteConstraintException e) {

                Toast.makeText(this, "Plant with same name already exists", Toast.LENGTH_SHORT).show();
                if (successP == -1) {
                    //DAO.deleteImage(image);
                }
                e.printStackTrace();
            }
        }



        return blogid;

    }
}