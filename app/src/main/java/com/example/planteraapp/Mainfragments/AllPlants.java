package com.example.planteraapp.Mainfragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
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
    private List<PlantsWithEverything> all_plants;
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

    public AllPlants() {
        // Required empty public constructor
    }


    public static AllPlants newInstance(String param1, String param2) {
        AllPlants fragment = new AllPlants();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
//        locations_radio_group.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
//        type_radio_group.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        filterImageBtnOnHost.setOnClickListener(v -> OpenBottomSheet());
        //Helping with restore bundle instance
        if (isBottomSheetOpen) OpenBottomSheet();
        else {
            getAllFiltersFromDatabase();
        }
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


    private void OpenBottomSheet() {
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
        grp.setOnCheckedChangeListener(this);
        location_check_box.setOnClickListener(v -> isLocationFilterOnBottomSheetActive = !isLocationFilterOnBottomSheetActive);
        type_check_box.setOnClickListener(v -> isTypeFilterOnBottomSheetActive = !isTypeFilterOnBottomSheetActive);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            isBottomSheetOpen = false;
            getAllFiltersFromDatabase();
        });
        bottomSheetDialog.show();
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

    public void getAllFiltersFromDatabase() {
        getFilterThread = new Thread(() -> {
            if (isTypeFilterOnBottomSheetActive)
                all_plant_types = DAO.getAllPlantTypes();
            if (isLocationFilterOnBottomSheetActive)
                all_plant_locations = DAO.getAllPlantLocations();
            requireActivity().runOnUiThread(() -> {
                generateFilter(all_plant_types, type_radio_group, isTypeFilterOnBottomSheetActive);
                generateFilter(all_plant_locations, locations_radio_group, isLocationFilterOnBottomSheetActive);
            });
        });
        getFilterThread.start();
    }

    public void generateFilter(List<?> list, RadioGroup group, boolean generate) {
        if (!generate) {
            group.setVisibility(View.GONE);
            return;
        }
        if (list == null || list.size() == 0)
            return;
        setFiltersAppliedLabelOnHost();
        group.check(-1);
        group.removeAllViews();
        generateRadioButtons(list, group);
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

    public void generateRadioButtons(List<?> list, RadioGroup group) {
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

    //    TODO:ADD putAllPlantsDataInGrid() inside this function
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == -1)
            return;
        boolean changeOccured = false;
        switch (radioGroup.getId()) {
            case R.id.sort_radio_group:
                if (i != SortAllPlantsBy) {
                    SortAllPlantsBy = i;
                    changeOccured = true;
                }
                break;
            case R.id.location_radio_group:
                if (i != locationFilterCurrentlyAppliedOnHost) {
                    locationFilterCurrentlyAppliedOnHost = i;
                    changeOccured = true;
                }
                break;
            case R.id.type_radio_group:
                if (i != typeFilterCurrentlyAppliedOnHost) {
                    typeFilterCurrentlyAppliedOnHost = i;
                    changeOccured = true;
                }
                break;
        }
        if (changeOccured) {
            Log.d("CalledMe", "Location : " + locationFilterCurrentlyAppliedOnHost + "|| Type : " + typeFilterCurrentlyAppliedOnHost + " Sort : " + getSortNameFromID());
            putAllPlantDataInGrid();
        }
    }

    public void putAllPlantDataInGrid() {
        new Thread(() -> {
            if (getFilterThread != null && getFilterThread.isAlive()) {
                try {
                    getFilterThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<PlantsWithEverything> newlist;
            if (typeFilterCurrentlyAppliedOnHost > 0 && locationFilterCurrentlyAppliedOnHost > 0) {
                newlist = DAO.getAllPlantsWithTANDL(all_plant_types.get(typeFilterCurrentlyAppliedOnHost - 1).type, all_plant_locations.get(locationFilterCurrentlyAppliedOnHost - 1).location, getSortNameFromID());
            } else if (typeFilterCurrentlyAppliedOnHost > 0) {
                newlist = DAO.getAllPlantsWithType(all_plant_types.get(typeFilterCurrentlyAppliedOnHost - 1).type, getSortNameFromID());
            } else if (locationFilterCurrentlyAppliedOnHost > 0) {
                newlist = DAO.getAllPlantsWithLocation(all_plant_locations.get(locationFilterCurrentlyAppliedOnHost - 1).location, getSortNameFromID());
            } else {
                newlist = DAO.getAllPlantsWithEverything();
            }
            requireActivity().runOnUiThread(() -> addViews(newlist));
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