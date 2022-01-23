package com.example.planteraapp.Mainfragments;
import static android.content.Context.ACTIVITY_SERVICE;
import static com.example.planteraapp.Activites.LauncherActivity.SharedFile;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.planteraapp.Activites.LauncherActivity;
import com.example.planteraapp.R;

public class Settings extends Fragment {
    SwitchCompat darkModeSwitch;
    AppCompatSpinner fontSizeSpinner;
    AppCompatButton aboutButton, helpButton, clearDataButton;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean initializedSpinner;

    public Settings() {/*Required empty public constructor*/}

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        fontSizeSpinner = view.findViewById(R.id.font_size_spinner);
        aboutButton = view.findViewById(R.id.about_button);
        helpButton = view.findViewById(R.id.help_button);
        darkModeSwitch.setChecked((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        fontSizeSpinner.setSelection(getScale(requireActivity().getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).getFloat("font", 1f)));
        clearDataButton = view.findViewById(R.id.clear_data);
        initializedSpinner = false;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        aboutButton.setOnClickListener(v -> redirectToGithub());
        helpButton.setOnClickListener(v -> redirectToGithub());
        clearDataButton.setOnClickListener(v -> showDialog());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? 2 : 1;
            edit("mode", mode);
            AppCompatDelegate.setDefaultNightMode(mode == 2 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!initializedSpinner) {
                    initializedSpinner = true;
                    return;
                }
                float scale = 0.75f + 0.25f * position;
                edit("font", scale);
                requireActivity().recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void redirectToGithub() {
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://github.com/MADTogetherness/PlanteraApp"));
        startActivity(httpIntent);
    }

    private void showDialog() {
        new AlertDialog.Builder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Delete App Data").setMessage("Are you sure you want to delete your entire application data?")
                .setPositiveButton("Yes", (dialog, which) -> clearAppData()).setNegativeButton("No", null)
                .show();
    }

    private void clearAppData() {
        try {
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) requireActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            } else {
                String packageName = requireContext().getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit(String key, Object value) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(SharedFile, Context.MODE_PRIVATE).edit();
        if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);

        editor.apply();
    }

    public int getScale(float scale) {
        if (scale == 0.75f) {
            return 0;
        } else if (scale == 1f) {
            return 1;
        } else if (scale == 1.25f) {
            return 2;
        }
        return 1;
    }
}