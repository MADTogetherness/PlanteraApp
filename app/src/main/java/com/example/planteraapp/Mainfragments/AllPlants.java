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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.planteraapp.Database.AppDatabase;
import com.example.planteraapp.Activites.MyPlant;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.Other.AttributeConverters;
import com.example.planteraapp.Model.DAO.PlantDAO;
import com.example.planteraapp.Model.Entities.PlantLocation;
import com.example.planteraapp.Model.Entities.PlantType;
import com.example.planteraapp.Model.Relations.PlantsWithEverything;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * * --------------------- Static Variable -------------------------
 *
 * @Variable: query - A default query string
 * --------------------- Boolean Flags -------------------------
 * @Variable: isBottomSheetOpen - To see if bottom sheet is open
 * @Variable: isTypeFilterOnBottomSheetActive - To see if the type filter is active
 * @Variable: isLocationFilterOnBottomSheetActive - To see if location filter is active
 * @Variable: preventMultipleCheckedChange - Prevent Multiple Queries when creating & inserting filters Initially
 * --------------------- INT TYPE ID ---------------------------
 * @Variable: SortAllPlantsBy - To get the current sorting by type
 * @Variable: locationFilterCurrentlyAppliedOnHost - To get the current applied location filter, example hallway
 * @Variable: typeFilterCurrentlyAppliedOnHost - To get the current applied type filter, example fern
 */
public class AllPlants extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private static final String query = "SELECT * FROM Plant";
    private boolean isTypeFilterOnBottomSheetActive, isLocationFilterOnBottomSheetActive, isBottomSheetOpen, preventMultipleCheckedChange = false;
    private int SortAllPlantsBy, locationFilterCurrentlyAppliedOnHost, typeFilterCurrentlyAppliedOnHost;
    private ImageButton filterImageBtnOnHost;
    private TextView filtersAppliedLabelOnHost;
    private GridLayout gridLayout;
    private ScrollView default_item_layout;
    private List<PlantType> all_plant_types;
    private List<PlantLocation> all_plant_locations;
    private PlantDAO DAO;
    private RadioGroup locations_radio_group, type_radio_group;
    private Thread getFilterThread;
    private String filterQuery = query;

    public AllPlants() {/*REQUIRE EMPTY CONSTRUCTOR*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_plants, container, false);
        // Initialise views & viewGroups + setup listeners
        init(v, savedInstanceState);
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

    /**
     * ----------- Let the fragment know Bottom sheet is open ----------
     * Create Bottom sheet & setContentView
     * Initialise the radioButtons & checkbox buttons & setDefault Values
     * Add Listeners to the content
     * set PreventMultipleQueries flag = True, Since after bottom sheet filters will either be created or destroyed
     * Then onBottomSheetDismiss call getAllFiltersFromDatabase()
     */
    private void SlideUpBottomSheet() {
        isBottomSheetOpen = true;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.com_bottom_sheet_layout);
        RadioGroup grp = bottomSheetDialog.findViewById(R.id.sort_radio_group);
        CheckBox location_check_box = bottomSheetDialog.findViewById(R.id.group_by_location),
                type_check_box = bottomSheetDialog.findViewById(R.id.group_by_type);
        assert grp != null;
        grp.check(SortAllPlantsBy);
        assert location_check_box != null;
        location_check_box.setChecked(isLocationFilterOnBottomSheetActive);
        assert type_check_box != null;
        type_check_box.setChecked(isTypeFilterOnBottomSheetActive);
        grp.setOnCheckedChangeListener((radioGroup, i) -> SortAllPlantsBy = i);
        location_check_box.setOnClickListener(v -> isLocationFilterOnBottomSheetActive = !isLocationFilterOnBottomSheetActive);
        type_check_box.setOnClickListener(v -> isTypeFilterOnBottomSheetActive = !isTypeFilterOnBottomSheetActive);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            isBottomSheetOpen = false;
            preventMultipleCheckedChange = true;
            getAllFiltersFromDatabase();
        });
        bottomSheetDialog.show();
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
        generateFilter(typeList, type_radio_group, isTypeFilterOnBottomSheetActive);
        generateFilter(locList, locations_radio_group, isLocationFilterOnBottomSheetActive);
        if (typeList == null && locList == null)
            putAllPlantDataInGrid(true);
        else if (preventMultipleCheckedChange) {
            putAllPlantDataInGrid(false);
            preventMultipleCheckedChange = false;
        }
    }

    public void generateFilter(List<?> list, @NonNull RadioGroup group, boolean generate) {
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

    public void addPlantViewsToGrid(@NonNull List<PlantsWithEverything> items) {
        gridLayout.removeAllViews();
        if (items.size() != 0) {
            default_item_layout.setVisibility(View.GONE);
            for (PlantsWithEverything all_plants : items) {
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_all_plants_grid_item_layout, gridLayout, false);
                TextView plantTag = item.findViewById(R.id.plant_tag);
                ImageView imageView = item.findViewById(R.id.image);
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

    public void setFiltersAppliedLabelOnHost() {
        filtersAppliedLabelOnHost.setText(R.string.filter_label);
        if (isTypeFilterOnBottomSheetActive && isLocationFilterOnBottomSheetActive)
            filtersAppliedLabelOnHost.append(" Location & Type");
        else if (isLocationFilterOnBottomSheetActive)
            filtersAppliedLabelOnHost.append(" Location");
        else if (isTypeFilterOnBottomSheetActive)
            filtersAppliedLabelOnHost.append(" Type");
        else
            filtersAppliedLabelOnHost.append(" Inactive");
    }

    public void addRadioButtons(@NonNull List<?> list, RadioGroup group) {
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
        if (radioGroup.getId() == R.id.location_radio_group)
            locationFilterCurrentlyAppliedOnHost = i;
        else if (radioGroup.getId() == R.id.type_radio_group)
            typeFilterCurrentlyAppliedOnHost = i;
        if (!preventMultipleCheckedChange)
            putAllPlantDataInGrid(false);

    }

    public void putAllPlantDataInGrid(boolean generateAll) {
        Log.d("Query", "called me : " + generateAll);
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
            Log.d("Query", filterQuery + " : " + generateAll);
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