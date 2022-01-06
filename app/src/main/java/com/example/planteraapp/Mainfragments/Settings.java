package com.example.planteraapp.Mainfragments;

import static com.example.planteraapp.LauncherActivity.SharedFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.planteraapp.R;

/*
TODO
- Spinner custom text
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchCompat darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        AppCompatSpinner fontSizeSpinner = view.findViewById(R.id.font_size_spinner);

        // Initial dark mode switch value
        int initialMode = requireActivity().getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("mode", 10);
        if (initialMode != 10) {
            darkModeSwitch.setChecked(initialMode == 2);
        }

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences(SharedFile, Context.MODE_PRIVATE).edit();
            editor.putInt("mode", isChecked ? 2 : 1);
            editor.apply();

            int newMode = requireActivity().getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("mode", 10);
            if (newMode != 10) {
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        });

        return view;
    }
}