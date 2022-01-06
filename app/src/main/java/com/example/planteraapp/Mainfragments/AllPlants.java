package com.example.planteraapp.Mainfragments;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.MyPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AllPlants extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private ImageButton filterImageBtnOnHost;
    private TextView filtersAppliedLabelOnHost;
    GridLayout gridLayout;
    LinearLayout default_item_layout;
    private List<PlantType> all_plant_types;
    private List<PlantLocation> all_plant_locations;
    // To see if the particular filters are active
    private boolean isTypeFilterOnBottomSheetActive, isLocationFilterOnBottomSheetActive;
    private boolean isBottomSheetOpen;
    // What sorting method is selected, default by name
    private int SortAllPlantsBy;
    // Active type of each filters on host
    private int locationFilterCurrentlyAppliedOnHost, typeFilterCurrentlyAppliedOnHost;
    PlantDAO DAO;
    RadioGroup locations_radio_group, type_radio_group;
    private Thread getFilterThread;
    private static final String query = "SELECT * FROM Plant";
    private String filterQuery = query;
    // Prevent Multiple Queries when creating & inserting filters Initially
    private boolean preventMultipleCheckedChange = false;

    public AllPlants() {/*REQUIRE EMPTY CONSTRUCTOR*/}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_plants, container, false);
        init(v, savedInstanceState);
        Log.d("runtime", "Waiting for all plants to load");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filterImageBtnOnHost.setOnClickListener(v -> SlideUpBottomSheet());
        if (isBottomSheetOpen) SlideUpBottomSheet();
        else getAllFiltersFromDatabase();

    }

    public void init(View v, Bundle savedInstanceState) {
        // Restore the saved variables
        if (savedInstanceState != null) {
            isTypeFilterOnBottomSheetActive = savedInstanceState.getBoolean("isTypeFilterOnBottomSheetActive", false);
            isLocationFilterOnBottomSheetActive = savedInstanceState.getBoolean("isLocationFilterOnBottomSheetActive", false);
            SortAllPlantsBy = savedInstanceState.getInt("SortAllPlantsBy", R.id.sort_by_name);
            locationFilterCurrentlyAppliedOnHost = savedInstanceState.getInt("locationFilterCurrentlyAppliedOnHost", -1);
            typeFilterCurrentlyAppliedOnHost = savedInstanceState.getInt("typeFilterCurrentlyAppliedOnHost", -1);
            isBottomSheetOpen = savedInstanceState.getBoolean("isBottomSheetOpen", false);
        } else {
            isTypeFilterOnBottomSheetActive = isLocationFilterOnBottomSheetActive = false;
            SortAllPlantsBy = R.id.sort_by_name;
            locationFilterCurrentlyAppliedOnHost = typeFilterCurrentlyAppliedOnHost = -1;
            isBottomSheetOpen = false;
        }
        filtersAppliedLabelOnHost = v.findViewById(R.id.filter_text_label);
        filterImageBtnOnHost = v.findViewById(R.id.filter_btn);
        gridLayout = v.findViewById(R.id.gridLayout);
        default_item_layout = v.findViewById(R.id.default_all_plants_item);
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        locations_radio_group = v.findViewById(R.id.location_radio_group);
        type_radio_group = v.findViewById(R.id.type_radio_group);
        locations_radio_group.setOnCheckedChangeListener(this);
        type_radio_group.setOnCheckedChangeListener(this);
    }

    private void SlideUpBottomSheet() {
        // Let the fragment know bottom sheet is open
        isBottomSheetOpen = true;
        // Create a bottom sheet & set content
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.com_bottom_sheet_layout);
        // Initialise view inside content view
        RadioGroup grp = bottomSheetDialog.findViewById(R.id.sort_radio_group);
        CheckBox location_check_box = bottomSheetDialog.findViewById(R.id.group_by_location),
                type_check_box = bottomSheetDialog.findViewById(R.id.group_by_type);
        // Set default selections of views
        assert grp != null;
        grp.check(SortAllPlantsBy);
        assert location_check_box != null;
        location_check_box.setChecked(isLocationFilterOnBottomSheetActive);
        assert type_check_box != null;
        // Add Listeners to the view
        type_check_box.setChecked(isTypeFilterOnBottomSheetActive);
        grp.setOnCheckedChangeListener((radioGroup, i) -> SortAllPlantsBy = i);
        location_check_box.setOnClickListener(v -> isLocationFilterOnBottomSheetActive = !isLocationFilterOnBottomSheetActive);
        type_check_box.setOnClickListener(v -> isTypeFilterOnBottomSheetActive = !isTypeFilterOnBottomSheetActive);
        // Add listener to when user closes the bottom sheet
        bottomSheetDialog.setOnDismissListener(dialog -> {
            isBottomSheetOpen = false;
            // Prevent multiple checkedchange
            preventMultipleCheckedChange = true;
            // Call appropriate validator
            getAllFiltersFromDatabase();
        });
        bottomSheetDialog.show();
    }

    public void addPlantViewsToGrid(List<PlantsWithEverything> items) {
        gridLayout.removeAllViews();
        if (items.size() != 0) {
            default_item_layout.setVisibility(View.GONE);
            for (PlantsWithEverything all_plants : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_all_plants_grid_item_layout, gridLayout, false);
                TextView plantTag = item.findViewById(R.id.plant_tag);
                ShapeableImageView imageView = item.findViewById(R.id.image);
                imageView.setImageBitmap(AttributeConverters.StringToBitMap(all_plants.plant.profile_image));
                plantTag.setText(all_plants.plant.plantName);
                gridLayout.addView(item);
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext().getApplicationContext(), MyPlant.class);
                    intent.putExtra("plantName", all_plants.plant.plantName);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.fragment_enter_anim, R.anim.fragment_exit_anim);
                });
            }
        } else default_item_layout.setVisibility(View.VISIBLE);
    }

    public void getAllFiltersFromDatabase() {
        // If none are active filters then remove existing filter views by passing false in generate
        if (!isTypeFilterOnBottomSheetActive && !isLocationFilterOnBottomSheetActive)
            validateFilterActivation(null, null);
        else {
            getFilterThread = new Thread(() -> {
                if (isTypeFilterOnBottomSheetActive)
                    all_plant_types = DAO.getAllPlantTypes();
                if (isLocationFilterOnBottomSheetActive)
                    all_plant_locations = DAO.getAllPlantLocations();
                requireActivity().runOnUiThread(() -> validateFilterActivation(all_plant_types, all_plant_locations));
            });
            getFilterThread.start();
        }
    }

    public void validateFilterActivation(@Nullable List<?> typeList, @Nullable List<?> locList) {
        setFiltersAppliedLabelOnHost();
        if (typeList == null && locList == null)
            putAllPlantDataInGrid(true);
        generateFilter(typeList, type_radio_group, isTypeFilterOnBottomSheetActive);
        generateFilter(locList, locations_radio_group, isLocationFilterOnBottomSheetActive);
        if (preventMultipleCheckedChange) {
            putAllPlantDataInGrid(false);
            preventMultipleCheckedChange = false;
        }
    }

    public void generateFilter(List<?> list, RadioGroup group, boolean generate) {
        group.removeAllViews();
        if (!generate) {
            group.setVisibility(View.GONE);
            return;
        }
        if (list == null || list.size() == 0)
            return;
        group.check(-1);
        addRadioButtons(list, group);
        group.setVisibility(View.VISIBLE);
        if (group.getId() == R.id.location_radio_group)
            ((AppCompatRadioButton) group.getChildAt(locationFilterCurrentlyAppliedOnHost == -1 ? 0 : locationFilterCurrentlyAppliedOnHost)).setChecked(true);
        if (group.getId() == R.id.type_radio_group)
            ((AppCompatRadioButton) group.getChildAt(typeFilterCurrentlyAppliedOnHost == -1 ? 0 : typeFilterCurrentlyAppliedOnHost)).setChecked(true);
    }

    public void setFiltersAppliedLabelOnHost() {
        filtersAppliedLabelOnHost.setText(R.string.filter_label);
        if (isLocationFilterOnBottomSheetActive) {
            filtersAppliedLabelOnHost.append(" Location");
        }
        if (isTypeFilterOnBottomSheetActive) {
            if (isLocationFilterOnBottomSheetActive)
                filtersAppliedLabelOnHost.append(" & ");
            filtersAppliedLabelOnHost.append(" Type");
        }
        if (!isTypeFilterOnBottomSheetActive && !isLocationFilterOnBottomSheetActive)
            filtersAppliedLabelOnHost.append(" Inactive");
    }

    public void addRadioButtons(List<?> list, RadioGroup group) {
        List<String> ListOfRadioNames = new ArrayList<>();
        for (Object pl : list) ListOfRadioNames.add(pl.toString());
        ListOfRadioNames.add(0, "All");
        for (int i = 0; i < ListOfRadioNames.size(); i++) {
            View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_radio_button_layout, group, false);
            item.setId(i);
            ((AppCompatRadioButton) item).setText(ListOfRadioNames.get(i));
            group.addView(item);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == -1)
            return;
        switch (radioGroup.getId()) {
            case R.id.location_radio_group:
                locationFilterCurrentlyAppliedOnHost = i;
                break;
            case R.id.type_radio_group:
                typeFilterCurrentlyAppliedOnHost = i;
                break;
        }
        Log.d("CalledMe", "Location : " + locationFilterCurrentlyAppliedOnHost + "|| Type : " + typeFilterCurrentlyAppliedOnHost + " Sort : " + getSortNameFromID());
        if (!preventMultipleCheckedChange)
            putAllPlantDataInGrid(false);

    }

    public void putAllPlantDataInGrid(boolean generateAll) {
        new Thread(() -> {
            List<PlantsWithEverything> newlist;
            if (!generateAll) {
                if (getFilterThread != null && getFilterThread.isAlive())
                    try {
                        getFilterThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                if (isLocationFilterOnBottomSheetActive && isTypeFilterOnBottomSheetActive && typeFilterCurrentlyAppliedOnHost > 0 && locationFilterCurrentlyAppliedOnHost > 0)
                    filterQuery += " WHERE plantType='" + all_plant_types.get(typeFilterCurrentlyAppliedOnHost - 1).type + "' AND plantLocation='" + all_plant_locations.get(locationFilterCurrentlyAppliedOnHost - 1).location + "'";
                else if (isTypeFilterOnBottomSheetActive && typeFilterCurrentlyAppliedOnHost > 0)
                    filterQuery += " WHERE plantType='" + all_plant_types.get(typeFilterCurrentlyAppliedOnHost - 1).type + "'";
                else if (isLocationFilterOnBottomSheetActive && locationFilterCurrentlyAppliedOnHost > 0)
                    filterQuery += " WHERE plantLocation='" + all_plant_locations.get(locationFilterCurrentlyAppliedOnHost - 1).location + "'";
            }
            try {
                filterQuery += " ORDER BY " + getSortNameFromID();
                newlist = DAO.customFilterPlantsRawQuery(new SimpleSQLiteQuery(filterQuery));
            } catch (SQLiteException ex) {
                newlist = new ArrayList<>();
                ex.printStackTrace();
            }
            Log.d("Query", filterQuery);
            filterQuery = query;
            List<PlantsWithEverything> finalNewlist = newlist;
            requireActivity().runOnUiThread(() -> addPlantViewsToGrid(finalNewlist));
        }).start();
    }


    public String getSortNameFromID() {
        switch (SortAllPlantsBy) {
            case R.id.sort_by_name:
                return "plantName";
            case R.id.sort_by_type:
                return "plantType";
            case R.id.sort_by_location:
                return "plantLocation";
            case R.id.sort_by_date:
                return "dateOfCreation";
            default:
                return null;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isTypeFilterOnBottomSheetActive", isTypeFilterOnBottomSheetActive);
        outState.putBoolean("isLocationFilterOnBottomSheetActive", isLocationFilterOnBottomSheetActive);
        outState.putInt("SortAllPlantsBy", SortAllPlantsBy);
        outState.putInt("locationFilterCurrentlyAppliedOnHost", locationFilterCurrentlyAppliedOnHost);
        outState.putInt("typeFilterCurrentlyAppliedOnHost", typeFilterCurrentlyAppliedOnHost);
        outState.putBoolean("isBottomSheetOpen", isBottomSheetOpen);
    }

}