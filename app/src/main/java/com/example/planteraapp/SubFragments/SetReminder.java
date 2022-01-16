package com.example.planteraapp.SubFragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.Mainfragments.NewPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AlertReceiver;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.Reminder;

import org.w3c.dom.Attr;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SetReminder extends Fragment {
    private static final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private EditText setReminderName, setTime, repeatInterval;
    private SwitchCompat switchCompat;
    private AppCompatButton test_notification, btnDone;
    private AutoCompleteTextView autoCompleteTextView;
    private int selectedHour = -1, selectedMinute = -1;
    private WeekDay lastCompleted;
    private ArrayAdapter<WeekDay> arrayAdapter;
    private Reminder reminderInstance;

    public SetReminder() {/*Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderInstance = getArguments() != null ?
                AttributeConverters.getGsonParser().fromJson(getArguments().getString(NewPlant.REMINDER_KEY), Reminder.class)
                : null;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_reminder, container, false);
        // Initialise the views
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
        } else lastCompleted = arrayAdapter.getItem(arrayAdapter.getCount() - 1);

        // Set the lastCompleted Day
        autoCompleteTextView.setText(lastCompleted.toString(), false);
        autoCompleteTextView.setOnItemClickListener((adapterView, v, i, l) -> lastCompleted = arrayAdapter.getItem(i));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setTime.setOnClickListener(v -> popTimePicker());
        view.findViewById(R.id.reminder_close).setOnClickListener(v -> requireActivity().onBackPressed());
        test_notification.setOnClickListener(v -> {
            Intent actionIntent = new Intent(requireContext(), AlertReceiver.class);
            actionIntent.putExtra("test", 1999);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1999, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), LauncherActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_my_plants_view_icon_v24)
                    .setContentTitle("Reminder to care")
                    .setContentText("Notification works well")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat test = NotificationManagerCompat.from(requireContext().getApplicationContext());
            test.notify(1999, builder.build());
            Toast.makeText(requireContext(), "Testing notification Now", Toast.LENGTH_LONG).show();
        });
        btnDone.setOnClickListener(v -> setReminder());
    }

    public void setReminder() {
        if (setReminderName.getText().toString().trim().length() == 0) {
            setReminderName.setError("Reminder Name is required");
            LauncherActivity.openSoftKeyboard(requireContext(), setReminderName);
            return;
        }
        if (selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(requireContext(), "Notification Time is required", Toast.LENGTH_SHORT).show();
            popTimePicker();
            return;
        }
        if (repeatInterval.getText().toString().trim().length() == 0) {
            repeatInterval.setError("Repeat not provided. Insert 0 for 1 time notification");
            LauncherActivity.openSoftKeyboard(requireContext(), repeatInterval);
            return;
        }
        String name = setReminderName.getText().toString().trim().substring(0, 1).toUpperCase() + setReminderName.getText().toString().trim().substring(1).toLowerCase();
        long repeat = AttributeConverters.getMillisFrom(Integer.parseInt(repeatInterval.getText().toString().trim()));
        reminderInstance = new Reminder(
                null,
                name,                                                                                           // Reminder Name
                AttributeConverters.getMillisFrom(selectedHour, selectedMinute),                                // Time
                lastCompleted.getLastCompletedInLong() + repeat,              // Real Time Epoch
                lastCompleted.getLastCompletedInLong(),                                                         // Last Completed Epoch
                repeat                                                                                          // Repeat interval in days (not epoch)
        );
        reminderInstance.notify = switchCompat.isChecked();
        Log.d("day", AttributeConverters.getGsonParser().toJson(reminderInstance));
        Log.d("day - check", "" + (reminderInstance.lastCompleted <= reminderInstance.realEpochTime));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(reminderInstance.lastCompleted + reminderInstance.repeatInterval);
        Log.d("day - next", AttributeConverters.getReadableTime(c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));
        c = Calendar.getInstance();
        c.setTimeInMillis(reminderInstance.realEpochTime + reminderInstance.repeatInterval);
        Log.d("day - next", getDay(c.get(Calendar.DAY_OF_WEEK)));

        Bundle b = new Bundle();
        b.putString(NewPlant.REMINDER_KEY, AttributeConverters.getGsonParser().toJson(reminderInstance));
        getParentFragmentManager().setFragmentResult("requestKey", b);
        requireActivity().onBackPressed();
    }

    public void popTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.themeOnverlay_timePicker, (timePicker, selectedHour, selectedMinute) -> {
            this.selectedHour = selectedHour;
            this.selectedMinute = selectedMinute;
            setTime.setText(AttributeConverters.getReadableTime(selectedHour, selectedMinute));
        }, this.selectedHour != -1 ? this.selectedHour : hour, this.selectedMinute != -1 ? this.selectedMinute : minute, false);
        timePickerDialog.show();
    }

    public void init(View view) {
        setReminderName = view.findViewById(R.id.set_reminder_name);
        setTime = view.findViewById(R.id.set_reminder_time);
        repeatInterval = view.findViewById(R.id.set_repeat_interval);
        switchCompat = view.findViewById(R.id.notify_enabled);
        autoCompleteTextView = view.findViewById(R.id.reminder_last_completed);
        test_notification = view.findViewById(R.id.test_notification);
        btnDone = view.findViewById(R.id.set_reminder_done);
        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, generateList());
        Log.d("opened", "yes3");
        autoCompleteTextView.setAdapter(arrayAdapter);
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