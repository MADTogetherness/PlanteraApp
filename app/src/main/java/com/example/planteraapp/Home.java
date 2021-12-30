package com.example.planteraapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Reminder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, NavController.OnDestinationChangedListener {
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

    public void init(){
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        add_plants_fab = findViewById(R.id.add_plants_fab);
        navController = Navigation.findNavController(this, R.id.nav_controller);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.fragment_enter_anim)
                .setExitAnim(R.anim.fragment_exit_anim)
                .setPopEnterAnim(R.anim.fragment_popenter_anim)
                .setPopExitAnim(R.anim.fragment_popexit_anim);
        add_plants_fab.setOnClickListener(view -> {
            if (isValidDestination(R.id.newPlant_fragment)) fabSelect();
        });
        bottomNavigationView.setOnItemSelectedListener(this);
        navController.addOnDestinationChangedListener(this);
        //TODO: GOING TO DIFFERENT FRAGMENTS FROM ANOTHER ACTIVITY
        int v = getIntent().getIntExtra("destination", -1);
        if (v != -1) {
            navController.navigate(v, null);
        }
    }

    public void setFabBackgroundTint(boolean status) {
        add_plants_fab.setSupportBackgroundTintList(ContextCompat.getColorStateList(this, status ? R.color.Button_Primary : R.color.Section_Container));
    }

    public void fabSelect() {
        bottomNavigationView.getMenu().findItem(R.id.placeholder).setChecked(true);
        navController.navigate(R.id.newPlant_fragment, null, options.build());
    }

    private boolean isValidDestination(int destination) {
        //Checking if current fragment is equal to desired fragment
        return destination != Objects.requireNonNull(navController.getCurrentDestination()).getId();
    }

    /**
     * The function first checks for second click on same fragment
     * later, pop of the stack up until calender fragment
     * then navigate to the desired fragment
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!isValidDestination(item.getItemId())) return false;
        if ((item.getOrder() & Menu.CATEGORY_SECONDARY) == 0)
            options.setPopUpTo(R.id.calendar, false);
        navController.navigate(item.getItemId(), null, options.build());
        return true;
    }

    //TODO: Control on destination changed listener
    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        Log.d("TESTTY", getResources().getResourceName(destination.getId()));
        setFabBackgroundTint(destination.getId() == R.id.newPlant_fragment);
    }

    //TODO: UNNECESSARY FOR NOW, UNLESS SETTINGS IS IMPLEMENTED
    //AWEB NEEDS TO CALL recreate();
    @Override
    public void recreate() {
        finish();
        startActivity(new Intent(Home.this, Home.class));
        overridePendingTransition(R.anim.fragment_enter_anim, R.anim.fragment_exit_anim);
    }
}