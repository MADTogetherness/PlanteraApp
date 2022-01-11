package com.example.planteraapp.Mainfragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.SearchAdapter;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
TODO:
- Color
- Navigate to plant
 */

public class Search extends Fragment implements SearchAdapter.SearchItemClickListener {

    PlantDAO plantDAO;
    List<PlantsWithEverything> allPlants;

    public Search() {
    }

    public static Search newInstance() {
        Search fragment = new Search();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        plantDAO = AppDatabase.getInstance(getContext()).plantDAO();
        allPlants = plantDAO.getAllPlantsWithEverything();

        EditText searchBar = view.findViewById(R.id.search_bar);
        RecyclerView recyclerView = view.findViewById(R.id.search_list);

        // Filter plants every keystroke instead of pressing enter
        // This way is much smoother and responsive
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Filter by name
                Stream<PlantsWithEverything> filteredPlants = allPlants.stream().filter(
                        p -> p.plant.plantName.toLowerCase()
                                .contains(searchBar.getText().toString().toLowerCase()));

                // Set recycle view adapter
                SearchAdapter adapter = new SearchAdapter(filteredPlants.collect(Collectors.toList()), Search.this);
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }

    @Override
    public void onClick(int position) {
        Log.d("F", Integer.toString(position));
    }
}
