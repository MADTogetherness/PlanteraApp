package com.example.planteraapp;
import static com.example.planteraapp.Utilities.AttributeConverters.StringToBitMap;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.planteraapp.Mainfragments.NewPlant;
import com.example.planteraapp.SubFragments.ColorTheme;
import com.example.planteraapp.SubFragments.SetReminder;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Blog;
import com.example.planteraapp.entities.BlogImagesCrossRef;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Images;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.Relations.BlogWithImages;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyPlant extends AppCompatActivity {
    private boolean fragmentOpened = false;
    private Plant plant;
    private PlantDAO DAO;
    private String plantName;
    TextView themeNameTV, upComingReminderTV, plantNameTV, descriptionTV;
    ImageView plantImage;
    private LinearLayout RemindersContainer;
    private List<Reminder> reminders;
    private LinearLayout TimelineContainer;
    private PlantsWithEverything everyThing;
    private GridLayout newTimeLineImages;
    private List<BlogWithImages> blogs;
    // Get the bitmap of image user has just selected from gallery
    private ArrayList<Bitmap> bitmapList;
    // The thread to load the image
    private Thread thread;
    // The image path is in this variable - get imagePath & store in the profile image field
    // Always check if(thread.isAlive()), if alive then toast user to try again later after image loads
    private HashMap<String, String> newImages;
    // The mGetMultipleContent Variable
    // Call mGetMultipleContent.launch("image/*")
    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> mGetMultipleContent = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
            uri -> {
                if (uri.size() != 0) {
                    Toast.makeText(this, "Uploading " + uri.size() + " Image(s)", Toast.LENGTH_SHORT).show();
                    Toast success = Toast.makeText(this, "Images Uploaded Successfully", Toast.LENGTH_SHORT);
                    for (Uri URI : uri) {
                        try {
                            bitmapList.add(BitmapFactory.decodeStream(this.getContentResolver().openInputStream(URI)));
                        } catch (FileNotFoundException e) {
                            Toast.makeText(this, "1 image failed to upload", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    inflateNewTimeImagesInsideBottomSheet();
                    thread = new Thread(() -> {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                        for (Bitmap bitmap : bitmapList)
                            newImages.put(AttributeConverters.BitMapToString(bitmap), "IMG" + df.format(Calendar.getInstance().getTime()) + "P.png");
                        success.show();
                    });
                    thread.start();
                }
            });


    private int selectedTheme = R.style.Theme_PlanteraApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: GET THE PLANT NAME FROM PREVIOUS ACTIVITY NOT DATABASE
        plantName = getIntent().getStringExtra("plantName");
        DAO = AppDatabase.getInstance(this).plantDAO();
        plant = DAO.getPlant(plantName);
        selectedTheme = plant.selectedTheme;
        ChangeThemeFromDB(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plant);
        init();
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        themeNameTV = findViewById(R.id.themenameTV);
        plantImage = findViewById(R.id.plant_image);
        plantNameTV = findViewById(R.id.plantNameTV);
        descriptionTV = findViewById(R.id.plantdescriptionTV);
        upComingReminderTV = findViewById(R.id.nextReminder);
        RemindersContainer = findViewById(R.id.reminders);
        TimelineContainer = findViewById(R.id.timelineLayout);
        plantImage.setImageBitmap(StringToBitMap(plant.profile_image));
        plantNameTV.setText(plant.plantName);
        descriptionTV.setText(plant.description);
        themeNameTV.setText("Theme : " + LauncherActivity.getThemeName(plant.selectedTheme));
        bitmapList = new ArrayList<>();
        newImages = new HashMap<>();
        reminders = new ArrayList<>();
        new Thread(() -> {
            everyThing = DAO.getAllPlantAttributes(plantName);
            blogs = DAO.getAllBlogsWithPlantID(plant.plantName).blogs;
            reminders = everyThing.Reminders.size() == 0 ? new ArrayList<>() : everyThing.Reminders;
            reminders.sort(Reminder.COMPARE_BY_TIME);
            runOnUiThread(() -> {
                upComingReminderTV.setText((reminders.size() == 0) ? "No reminders set" : "Next Reminder is " + AttributeConverters.getRemainingTime(reminders.get(0).realEpochTime));
                addRemindersToList(reminders);
                addBlogsToList(blogs);
            });
        }).start();
        findViewById(R.id.close).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.delete_btn).setOnClickListener(v -> showDialog());
        findViewById(R.id.add_new_timeline).setOnClickListener(v -> SlideUpBottomSheetForBlogging());
        findViewById(R.id.editPlantTheme).setOnClickListener(v -> getNewTheme());
        findViewById(R.id.edit_btn).setOnClickListener(v -> {
            Intent i = new Intent(this, Home.class);
            i.putExtra(LauncherActivity.navigateToKey, R.id.action_calendar_to_new_plant);
            Bundle b = new Bundle();
            b.putString(LauncherActivity.plantNameKey, plantName);
            i.putExtra(LauncherActivity.BundleKey, b);
            startActivity(i);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Declare Dead?").setMessage("Are you sure you want to declare the plant dead?")
                .setPositiveButton("Yes", (dialog, which) -> deletePlant()).setNegativeButton("No", null)
                .show();
    }

    public void deletePlant() {
        DAO.deletePlant(plant);
        Toast.makeText(this, plant.plantName + " will miss you so much...Good bye now!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, Home.class);
        i.putExtra(LauncherActivity.navigateToKey, R.id.action_calendar_to_new_plant);
        finish();
        startActivity(i);
    }

    private void ChangeThemeFromDB(boolean recreate) {
        if (recreate) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            recreate();
        } else setTheme(selectedTheme);
    }

    @SuppressLint("SetTextI18n")
    public void addRemindersToList(@NonNull List<Reminder> items) {
        RemindersContainer.removeAllViews();
        int i = 0;
        if (items.size() == 0)
            addNewReminderViewPrompt(getDrawableForReminder(-1));
        else {
            for (Reminder all_reminders : items) {
                View item = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, RemindersContainer, false);
                TextView tvTitle = item.findViewById(R.id.reminder_name);
                TextView tvDesc = item.findViewById(R.id.reminder_desc);
                View bubble = item.findViewById(R.id.bubble);
                ImageView arrow = item.findViewById(R.id.arrow);
                tvTitle.setText(all_reminders.name);
                tvDesc.setText("Repeat After " + AttributeConverters.toDays(all_reminders.repeatInterval) + " day(s)" + "\nTime: " + AttributeConverters.getReadableTime(all_reminders.time) + "\nReminder is " + (all_reminders.notify ? "on" : "off"));
                bubble.setBackgroundTintList(ContextCompat.getColorStateList(this, LauncherActivity.getColour(all_reminders.name)));
                ((RelativeLayout) tvTitle.getParent()).setBackgroundResource(getDrawableForReminder(i));
                arrow.setImageResource(R.drawable.ic_pencil);
                int finalI = i;
                item.setOnClickListener(v -> getReminder(finalI, reminders.get(finalI)));
                RemindersContainer.addView(item);

                if (i == items.size() - 1 && items.size() <= 2)
                    addNewReminderViewPrompt(getDrawableForReminder(2));
                i++;

            }
        }
    }
    public void addNewReminderViewPrompt(int BackgroundResource) {
        View item = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reminder_item, RemindersContainer, false);
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
        RemindersContainer.addView(item);
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
    public void inflateNewTimeImagesInsideBottomSheet() {
        if (newTimeLineImages == null)
            return;
        newTimeLineImages.removeAllViews();
        if (bitmapList.size() != 0) {
            for (Bitmap bitmap : bitmapList) {
                View item = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_layout_blogimg, newTimeLineImages, false);
                ((ImageView) item).setImageBitmap(bitmap);
                newTimeLineImages.addView(item);
            }
        }
    }
    private void SlideUpBottomSheetForBlogging() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.com_layout_new_timeline);
        TextView Date = bottomSheetDialog.findViewById(R.id.timeline_date);
        EditText timeline_desc = bottomSheetDialog.findViewById(R.id.timeline_desc);
        Button insertImages = bottomSheetDialog.findViewById(R.id.add_pictureBtn);
        Button SaveBlog = bottomSheetDialog.findViewById(R.id.saveBlogBtn);
        newTimeLineImages = bottomSheetDialog.findViewById(R.id.gridLayout_images);
        assert newTimeLineImages != null;
        assert timeline_desc != null;
        assert SaveBlog != null;
        assert insertImages != null;
        assert Date != null;
        Date.setText(new SimpleDateFormat("EEE, dd MMM").format(Calendar.getInstance().getTime()));
        insertImages.setOnClickListener(v -> mGetMultipleContent.launch("image/*"));
        SaveBlog.setOnClickListener(v -> {
            if (timeline_desc.getText().toString().trim().isEmpty()) {
                timeline_desc.setError("Please enter some timeline description");
                LauncherActivity.openSoftKeyboard(this, timeline_desc);
                return;
            }
            if (thread != null && thread.isAlive()) {
                Toast.makeText(this, "Please wait for the images to upload", Toast.LENGTH_LONG).show();
                return;
            }
            List<Long> ImageIDS = new ArrayList<>();
            Toast.makeText(this, "Inserting New Blog... Please wait!", Toast.LENGTH_LONG).show();
            new Thread(() -> {
                long blogID = -1;
                try {
                    blogID = DAO.insertBlogs(new Blog(plantName, timeline_desc.getText().toString().trim()))[0];
                } catch (SQLiteException ex) {
                    Toast.makeText(this, "Failed to insert blog", Toast.LENGTH_LONG).show();
                }
                if (blogID != -1) {
                    try {
                        newImages.forEach((src, name) -> ImageIDS.add(DAO.insertImage(new Images(name, src))));
                    } catch (SQLiteException ex) {
                        DAO.deleteBlog(blogID);
                        Toast.makeText(this, "Failed to insert images", Toast.LENGTH_LONG).show();
                    }
                }
                for (long imgID : ImageIDS)
                    DAO.insertNewBlogImageCrossRef(new BlogImagesCrossRef(blogID, imgID));
                BlogWithImages BWI = DAO.getBlogWithImagesWithID(blogID);
                runOnUiThread(() -> addBlogsToList(Collections.singletonList(BWI)));
            }).start();
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setOnDismissListener(dialog -> resetBottomSheet());
        bottomSheetDialog.show();
    }
    public void resetBottomSheet() {
        newImages = new HashMap<>();
        bitmapList = new ArrayList<>();
    }
    public void addBlogsToList(@NonNull List<BlogWithImages> items) {
        findViewById(R.id.empty_timeline).setVisibility(items.size() == 0 ? View.VISIBLE : View.GONE);
        for (BlogWithImages item : items) {
            View view = getLayoutInflater().inflate(R.layout.com_layout_timeline, TimelineContainer, false);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(item.blog.dateCreated);
            GridLayout imagesContainer = view.findViewById(R.id.gridLayout_images);
            ((TextView) view.findViewById(R.id.timeline_desc)).setText(item.blog.description);
            ((TextView) view.findViewById(R.id.timeline_date)).setText(new SimpleDateFormat("EEE, dd MMM").format(c.getTime()));
            for (Images img : item.images) {
                View ImageV = getLayoutInflater().inflate(R.layout.com_layout_blogimg, imagesContainer, false);
                ((ImageView) ImageV).setImageBitmap(AttributeConverters.StringToBitMap(img.imageData));
                imagesContainer.addView(ImageV);
            }
            TimelineContainer.addView(view, 0);
        }
    }
    public void getNewTheme() {
        FragmentManager fm = getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            selectedTheme = bundle.getInt(NewPlant.THEME_KEY);
            if (plant.selectedTheme == selectedTheme)
                return;
            plant.selectedTheme = selectedTheme;
            DAO.updateTheme(plant);
            Toast.makeText(this, "Theme set to " + LauncherActivity.getThemeName(selectedTheme), Toast.LENGTH_SHORT).show();
            ChangeThemeFromDB(true);
        });
        Bundle b = new Bundle();
        b.putInt(NewPlant.THEME_KEY, selectedTheme);
        openSubFragment(new ColorTheme(), b, fm);
    }
    public void getReminder(int position, Reminder... reminder) {
        FragmentManager fm = getSupportFragmentManager();
        fm.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            Reminder newReminder = AttributeConverters.getGsonParser().fromJson(bundle.getString(NewPlant.REMINDER_KEY), Reminder.class);
            if(position >= 0) reminders.set(position, newReminder);
            else reminders.add(newReminder);
            addRemindersToList(reminders);
            Toast.makeText(this, "Reminder" + (position < 0 ? " set to " : " edited for ") + newReminder.name, Toast.LENGTH_SHORT).show();
        });
        Bundle b = new Bundle();
        if (position >= 0 && reminder != null)
            b.putString(NewPlant.REMINDER_KEY, AttributeConverters.getGsonParser().toJson(reminder[0]));
        b.putString("location", everyThing.location.location);
        b.putString("plantName", everyThing.plant.plantName);
        openSubFragment(new SetReminder(), b, fm);
    }
    public void openSubFragment(Fragment fg, Bundle bn, FragmentManager fm) {
        fragmentOpened = true;
        fg.setArguments(bn);
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                .replace(R.id.container, fg, "SubFrag")
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("SubFrag");
        if (frag != null && fragmentOpened) {
            getSupportFragmentManager().beginTransaction().remove(frag).commitNowAllowingStateLoss();
            fragmentOpened = false;
        } else
            super.onBackPressed();
    }
}