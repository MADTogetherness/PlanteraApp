package com.example.planteraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import java.util.Objects;

public class Home extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton add_plants_fab;
    NavController navController;
    NavOptions.Builder options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    @SuppressLint("RestrictedApi")
    public void init(){
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        add_plants_fab = findViewById(R.id.add_plants_fab);
        navController = Navigation.findNavController(this, R.id.nav_controller);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        options = new NavOptions.Builder();
        options.setLaunchSingleTop(true)
                .setEnterAnim(R.anim.fragment_enter_anim)
                .setExitAnim(R.anim.fragment_exit_anim)
                .setPopEnterAnim(R.anim.fragment_popenter_anim)
                .setPopExitAnim(R.anim.fragment_popexit_anim);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        add_plants_fab.setOnClickListener(view -> {
            if(isValidDestination(R.id.newPlant_fragment)){
                MenuItem menuItem = bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
                ((MenuItemImpl) menuItem).setExclusiveCheckable(false);
                menuItem.setChecked(false);
                ((MenuItemImpl) menuItem).setExclusiveCheckable(true);
                Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.newPlant_fragment, null, options.build());
            }
        });
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(!isValidDestination(getCorrespondingFragmentsId(item.getItemId()))){
            return false;
        }
        switch (item.getItemId()){
            case R.id.calendar:
                Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.calendar_fragment, null, options.build());
                break;
            case R.id.search:
                Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.search_fragment, null, options.build());
                break;
            case R.id.all_plants:
                Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.allPlants_fragment, null, options.build());
                break;
            case R.id.settings:
                Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.settings_fragment, null, options.build());
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean isValidDestination(int destination){
        return destination != Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_controller).getCurrentDestination()).getId();
    }

    private int getCorrespondingFragmentsId(int id){
        switch (id){
            case R.id.calendar:
                return R.id.calendar_fragment;
            case R.id.calendar_fragment:
                return R.id.calendar;
            case R.id.search:
                return R.id.search_fragment;
            case R.id.search_fragment:
                return  R.id.search;
            case R.id.all_plants:
                return R.id.allPlants_fragment;
            case R.id.allPlants_fragment:
                return R.id.all_plants;
            case R.id.settings:
                return R.id.settings_fragment;
            case R.id.settings_fragment:
                return R.id.settings;
        }
        return -1;
    }
}