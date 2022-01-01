package com.example.planteraapp.Mainfragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllPlants#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPlants extends Fragment {
    private ImageButton filter_btn;
    private TextView filter_txt_label;
    GridLayout gridLayout;
    LinearLayout default_item_layout;
    PlantDAO DAO;
    List<PlantsWithEverything> grid_items_list;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllPlants() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllPlants.
     */
    // TODO: Rename and change types and number of parameters
    public static AllPlants newInstance(String param1, String param2) {
        AllPlants fragment = new AllPlants();
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
        View v = inflater.inflate(R.layout.fragment_all_plants, container, false);
        filter_btn = v.findViewById(R.id.filter_btn);
        filter_txt_label = v.findViewById(R.id.filter_text_label);
        gridLayout = v.findViewById(R.id.gridLayout);
        default_item_layout = v.findViewById(R.id.default_all_plants_item);
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        new Thread(() -> addViews(DAO.getAllPlantsWithEverything())).start();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void addViews(List<PlantsWithEverything> items) {
        gridLayout.removeAllViews();
        if (items.size() != 0) {
            default_item_layout.setVisibility(View.GONE);
            for (PlantsWithEverything all_plants : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_all_plants_grid_item, gridLayout, false);
                TextView plantTag = item.findViewById(R.id.plant_tag);
                ShapeableImageView imageView = item.findViewById(R.id.image);
                imageView.setImageBitmap(AttributeConverters.StringToBitMap(all_plants.plant.profile_image));
                plantTag.setText(all_plants.plant.plantName);
                gridLayout.addView(item);
            }
        }
    }
}