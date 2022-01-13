package com.example.planteraapp.SubFragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;

import org.w3c.dom.Attr;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetReminder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetReminder.
     */
    // TODO: Rename and change types and number of parameters

    private EditText setReminderName, setTime, repeatInterval;
    private SwitchCompat switchCompat;
    private View color;
    private AppCompatButton btnDone;
    private int selectedHour = -1, selectedMinute = -1;

    public static SetReminder newInstance(String param1, String param2) {
        SetReminder fragment = new SetReminder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_set_reminder, container, false);
        setReminderName = view.findViewById(R.id.set_reminder_name);
        setTime = view.findViewById(R.id.set_reminder_time);
        repeatInterval = view.findViewById(R.id.set_repeat_interval);
        switchCompat = view.findViewById(R.id.notify_enabled);
        color = view.findViewById(R.id.view_reminder_color);
        btnDone = view.findViewById(R.id.set_reminder_done);
        if (getArguments() != null) {
            ((TextView) view.findViewById(R.id.header)).setText("Edit Reminder");
            long[] time = AttributeConverters.getHoursAndMinutes(getArguments().getLong("time"));
            selectedHour = (int) time[0];
            selectedMinute = (int) time[1];
            setTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            repeatInterval.setText(String.valueOf(AttributeConverters.toDays(getArguments().getLong("interval"))));
            switchCompat.setChecked(getArguments().getBoolean("notificationEnabled"));
            String reminder_name = getArguments().getString("reminderName");
            setReminderName.setText(reminder_name);
            color.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), LauncherActivity.getColour(reminder_name)));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setTime.setOnClickListener(v -> popTimePicker());
        setReminderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    color.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
                    return;
                }
                String temp = charSequence.toString().trim();
                color.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), LauncherActivity.getColour(temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase())));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        view.findViewById(R.id.reminder_close).setOnClickListener(v -> requireActivity().onBackPressed());
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
        String name = setReminderName.getText().toString().trim();
        int repeat = Integer.parseInt(repeatInterval.getText().toString().trim());
        Bundle b = new Bundle();
        b.putLong("time", AttributeConverters.getMillisFrom(selectedHour, selectedMinute));
        b.putLong("interval", AttributeConverters.getMillisFrom(repeat));
        b.putString("reminderName", name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        b.putBoolean("notificationEnabled", switchCompat.isChecked());
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

}