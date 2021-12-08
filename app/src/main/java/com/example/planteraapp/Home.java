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
import android.view.Menu;
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

    public void init(){
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        add_plants_fab = findViewById(R.id.add_plants_fab);
        navController = Navigation.findNavController(this, R.id.nav_controller);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        //Animating enter & exit activities --- Exit for previous, Enter for next
        options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.fragment_enter_anim)
                .setExitAnim(R.anim.fragment_exit_anim)
                .setPopEnterAnim(R.anim.fragment_popenter_anim)
                .setPopExitAnim(R.anim.fragment_popexit_anim);

        add_plants_fab.setOnClickListener(view -> { if(isValidDestination(R.id.newPlant_fragment)) fabSelection(); });
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @SuppressLint("RestrictedApi")
    public void fabSelection(){
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
        ((MenuItemImpl) menuItem).setExclusiveCheckable(false);
        menuItem.setChecked(false);
        ((MenuItemImpl) menuItem).setExclusiveCheckable(true);
        //Navigate to FAB fragment AKA add new plants fragment
        Navigation.findNavController(this, R.id.nav_controller).navigate(R.id.newPlant_fragment, null, options.build());
    }

    private boolean isValidDestination(int destination){
        //Checking if current fragment is equal to desired fragment
        return destination != Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_controller).getCurrentDestination()).getId();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Checking for second click on same fragment
        if(!isValidDestination(item.getItemId())) return false;
        //Popping of the stack up until calender fragment
        if ((item.getOrder() & Menu.CATEGORY_SECONDARY) == 0) options.setPopUpTo(R.id.calendar, false);
        //Navigating to the desired fragment
        Navigation.findNavController(this, R.id.nav_controller).navigate(item.getItemId(), null, options.build());
        return true;
    }
}