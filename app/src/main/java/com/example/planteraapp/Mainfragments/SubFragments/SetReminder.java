package com.example.planteraapp.Mainfragments.SubFragments;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.Database.AppDatabase;
import com.example.planteraapp.Activites.LauncherActivity;
import com.example.planteraapp.Mainfragments.NewPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.Other.AlertReceiver;
import com.example.planteraapp.Utilities.Other.AttributeConverters;
import com.example.planteraapp.Model.DAO.PlantDAO;
import com.example.planteraapp.Model.Entities.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

// SetReminder fragment --> One of the additional fragments used to edit or add new reminder
public class SetReminder extends Fragment {
    // Get Today's day of week
    private static final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    // EditText for reminder name, the time & the repeat Interval
    private EditText setReminderName, setTime, repeatInterval;
    // Notification is switched On or Off
    private SwitchCompat switchCompat;
    // Button to test sample notification right now in time
    // Button to save the data & proceed forward
    private AppCompatButton test_notification, btnDone;
    // Choosing the day when the reminder was last completed, Uneditable & inputType="None"
    private AutoCompleteTextView lastCompletedATV;
    // Setting the selectedHour & SelectedMinute to -1 for the time picker clock dialog
    // When user clicks on setTime EditTextField
    private int selectedHour = -1, selectedMinute = -1;
    // Custom Class to see when the reminder was last completed
    // This class has int corresponding to day of the week
    // It is mapping the strings with int --> Example Sunday = 1, Monday = 2...Saturday = 7
    private WeekDay lastCompleted;
    // ArrayAdapter to set lastCompleted Options like a dropdown menu
    // @variable: lastCompletedATV
    private ArrayAdapter<WeekDay> arrayAdapter;
    // Reminder Instance to be edited or leave it as null for new reminder
    private Reminder reminderInstance;
    // PlantDAO App database variable to perform CRUD operations
    PlantDAO DAO;

    public SetReminder() {/*Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We check if the reminder instance is passed through the bundle
        // If yes? then this time reminder instance is being edited
        // No? this time reminder is being added
        if (getArguments() != null && getArguments().getString(NewPlant.REMINDER_KEY) != null)
            reminderInstance = AttributeConverters.getGsonParser().fromJson(getArguments().getString(NewPlant.REMINDER_KEY), Reminder.class);

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_reminder, container, false);
        // Initialise the views
        DAO = AppDatabase.getInstance(requireActivity()).plantDAO();
        // Initialise the views & view groups
        init(view);
        // Check if the reminderInstance is new or existing
        // If existing then set the properties
        if (reminderInstance != null) {
            ((TextView) view.findViewById(R.id.header)).setText("Edit Reminder");
            long[] time = AttributeConverters.getHoursAndMinutes(reminderInstance.time);
            selectedHour = (int) time[0];
            selectedMinute = (int) time[1];
            setTime.setText(AttributeConverters.getReadableTime(selectedHour, selectedMinute));
            repeatInterval.setText(String.valueOf(AttributeConverters.toDays(reminderInstance.repeatInterval)));
            switchCompat.setChecked(reminderInstance.notify);
            setReminderName.setText(reminderInstance.name);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(reminderInstance.lastCompleted);
            lastCompleted = new WeekDay(c.get(Calendar.DAY_OF_WEEK));
        }
        // Setting the lastCompleted variable to be used later as today's day of the week
        else lastCompleted = arrayAdapter.getItem(arrayAdapter.getCount() - 1);

        // Set the lastCompleted Day in the ATV as well to show changes to User
        lastCompletedATV.setText(lastCompleted.toString(), false);
        lastCompletedATV.setOnItemClickListener((adapterView, v, i, l) -> lastCompleted = arrayAdapter.getItem(i));
        return view;
    }

    //Initialise Views
    public void init(View view) {
        setReminderName = view.findViewById(R.id.set_reminder_name);
        setTime = view.findViewById(R.id.set_reminder_time);
        repeatInterval = view.findViewById(R.id.set_repeat_interval);
        switchCompat = view.findViewById(R.id.notify_enabled);
        lastCompletedATV = view.findViewById(R.id.reminder_last_completed);
        test_notification = view.findViewById(R.id.test_notification);
        btnDone = view.findViewById(R.id.set_reminder_done);
        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, generateList());
        Log.d("opened", "yes3");
        lastCompletedATV.setAdapter(arrayAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // When user clicks setTime, the clock popsUP using popTimePicker function called on it
        // See the function for more details
        setTime.setOnClickListener(v -> popTimePicker());
        // When user closes the reminder or goes back
        view.findViewById(R.id.reminder_close).setOnClickListener(v -> requireActivity().onBackPressed());
        // User wants to test the notification here at this current time
        test_notification.setOnClickListener(v -> {
            // Intent Action after user will actually click on the broad-casted notification
            // AKA after user receives notification, they can click on it & it will take them to AlertReceiver.class
            Intent actionIntent = new Intent(requireContext(), AlertReceiver.class);
            // Telling the class that this is just a test notification & do appropriately with random unimportant value
            actionIntent.putExtra("test", 1999);
            // Broadcasting notification using the pending intent,intent that will be fired only on user click of notification
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1999, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), LauncherActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_my_plants_view_icon_v24)
                    .setContentTitle("Reminder to care")
                    .setContentText("Notification works well")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat test = NotificationManagerCompat.from(requireContext().getApplicationContext());
            // Notify the user
            test.notify(1999, builder.build());
            Toast.makeText(requireContext(), "Testing notification Now", Toast.LENGTH_LONG).show();
        });
        // Call function to finally setUP everything perfectly
        btnDone.setOnClickListener(v -> setReminder());
        view.findViewById(R.id.delete_btn).setOnClickListener(v -> {
            if (getArguments() != null && getArguments().getString("plantName") != null && reminderInstance != null) {
                NewPlant.setAlarm(requireContext(), reminderInstance, getArguments().getString("location"), false);
                DAO.deleteReminder(reminderInstance);

            }
            getParentFragmentManager().setFragmentResult("requestKey", new Bundle());
            Toast.makeText(requireContext(), "Reminder was successfully removed", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }

    public void setReminder() {
        // Check usual text empty & not fitting the criteria mentioned
        if (setReminderName.getText().toString().trim().length() == 0) {
            setReminderName.setError("Reminder Name is required");
            LauncherActivity.openSoftKeyboard(requireContext(), setReminderName);
            return;
        }
        // Check usual time is not set & not fitting the criteria mentioned
        // It will open the clock to set time
        if (selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(requireContext(), "Notification Time is required", Toast.LENGTH_SHORT).show();
            popTimePicker();
            return;
        }
        // Check usual repeat interval is not set & not fitting the criteria mentioned
        if (repeatInterval.getText().toString().trim().length() == 0) {
            repeatInterval.setError("Repeat not provided");
            LauncherActivity.openSoftKeyboard(requireContext(), repeatInterval);
            return;
        }
        // Check usual repeat interval > 0
        if (Integer.parseInt(repeatInterval.getText().toString().trim()) < 1) {
            repeatInterval.setError("Repeat should be greater than 0");
            LauncherActivity.openSoftKeyboard(requireContext(), repeatInterval);
            return;
        }
        //Save the data in temporary variables
        String name = setReminderName.getText().toString().trim().substring(0, 1).toUpperCase() + setReminderName.getText().toString().trim().substring(1).toLowerCase();
        long repeat = AttributeConverters.getMillisFrom(Integer.parseInt(repeatInterval.getText().toString().trim()));
        // Save the previous id
        // We need this id to update reminder & alarms in database & app
        long id = -1;
        // Check plantName exists, if it does --> This shows we are editing the reminder from MyPlant not NewPlant
        if (reminderInstance != null && reminderInstance.plantName != null) {
            id = reminderInstance.reminderID;
        }
        long NextTimeReminder = lastCompleted.getLastCompletedInLong() + repeat;
        while (NextTimeReminder <= System.currentTimeMillis())
            NextTimeReminder += repeat;
        // Create the reminder instance destroying the previous values
        // thus that's why we saved the ids & plant name before
        reminderInstance = new Reminder(
                // PLEASE NOTE NewPlant.java can also edit reminder but it will not be saved here because plantName will be null
                reminderInstance != null ? reminderInstance.plantName : null,                                                                                      // PlantName? null or not
                name,                                                                                           // Reminder Name
                AttributeConverters.getMillisFrom(selectedHour, selectedMinute),                                // Time
                NextTimeReminder,                                                                               // Real Time Epoch
                lastCompleted.getLastCompletedInLong(),                                                         // Last Completed Epoch
                repeat                                                                                          // Repeat interval in days (not epoch)
        );
        reminderInstance.notify = switchCompat.isChecked();
        Bundle b = new Bundle();
        // Now the send the newly created instance back from where it came
        b.putString(NewPlant.REMINDER_KEY, AttributeConverters.getGsonParser().toJson(reminderInstance));
        getParentFragmentManager().setFragmentResult("requestKey", b);
        // If below condition is true, the reminder came from MyPlant & it needs to get updated
        // MyPlant should ensure to pass plantName to edit & update reminder
        if (reminderInstance != null && getArguments() != null && getArguments().getString("plantName") != null) {
            if (reminderInstance.plantName != null) {
                reminderInstance.reminderID = id;
                DAO.updateReminder(reminderInstance);
            } else {
                reminderInstance.plantName = getArguments().getString("plantName");
                DAO.insertReminders(reminderInstance);
            }
            NewPlant.setAlarm(requireContext(), reminderInstance, getArguments().getString("location"), true);
        }
        requireActivity().onBackPressed();
    }

    // Set time through clock to EditText
    public void popTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.themeOverlay_timePicker, (timePicker, selectedHour, selectedMinute) -> {
            this.selectedHour = selectedHour;
            this.selectedMinute = selectedMinute;
            setTime.setText(AttributeConverters.getReadableTime(selectedHour, selectedMinute));
        }, this.selectedHour != -1 ? this.selectedHour : hour, this.selectedMinute != -1 ? this.selectedMinute : minute, false);
        timePickerDialog.show();
    }


    public List<WeekDay> generateList() {
        List<WeekDay> days = new ArrayList<>();
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK), i = (today % 7) + 1; //7
        Log.d("opened", "yes1");
        while (i != today) {
            Log.d("day", String.valueOf(i));
            days.add(new WeekDay(i++));
            if (i - 1 == 7) i = 1;
        }
        Log.d("opened", "yes2");
        days.add(new WeekDay(i));
        return days;
    }

    public String getDay(int d) {
        switch (d) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return null;
    }

    class WeekDay {
        String name;
        int day;

        WeekDay(int day) {
            this.day = day;
            this.name = day == today ? "Today" : getDay(day);
        }

        long getLastCompletedInLong() {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            int d = (today - day) < 0 ? today - day + 7 : today - day;
            return calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(d);

        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

    }

}