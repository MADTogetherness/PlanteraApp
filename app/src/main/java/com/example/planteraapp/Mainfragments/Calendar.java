package com.example.planteraapp.Mainfragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment {
    private PlantDAO DAO;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Calendar() {
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
    public static Calendar newInstance(String param1, String param2) {
        Calendar fragment = new Calendar();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        Plant plant = DAO.getSinglePlantInstance("XYZ");
        if (plant == null) {
            DAO.insertPlantTypes(new PlantType("Vascular"));
            DAO.insertPlantLocations(new PlantLocation("Roof"));
            DAO.insertNewPlant(new Plant("XYZ", "sdfgfdfjkdfjvdjdkfj", "Vascular", "Roof", 0, "smddf"));
            plant = DAO.getSinglePlantInstance("XYZ");
        }
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).edit();
        Button light = view.findViewById(R.id.light_theme), dark = view.findViewById(R.id.dark_theme), d = view.findViewById(R.id.default_theme);
        Plant finalPlant = plant;
        light.setOnClickListener(v -> {
            finalPlant.selectedTheme = R.style.Theme_PlanteraApp_Chiffon_Purple;
            DAO.updateTheme(finalPlant);
            editor.putInt("mode", 1);
            editor.apply();
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
        dark.setOnClickListener(v -> {
            finalPlant.selectedTheme = R.style.Theme_PlanteraApp_Accent_Dark;
            DAO.updateTheme(finalPlant);
            editor.putInt("mode", 2);
            editor.apply();
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        });
        d.setOnClickListener(v -> {
            finalPlant.selectedTheme = R.style.Theme_PlanteraApp_Monochromatic_Brown;
            DAO.updateTheme(finalPlant);
            editor.putInt("mode", -1);
            editor.apply();
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });
    }
}