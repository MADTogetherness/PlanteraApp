package com.example.planteraapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
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
            navController.navigate(v, null, options.build());
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
        setFabBackgroundTint(destination.getId() == R.id.newPlant_fragment);
    }

    @Override
    public void recreate() {
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        Intent intent = new Intent(this, getClass());
        intent.putExtra("saved_state", bundle);
        intent.putExtra("destination", R.id.settings);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration newConfig = new Configuration(newBase.getResources().getConfiguration());
        newConfig.fontScale = newBase.getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).getFloat("font", 1f);
        applyOverrideConfiguration(newConfig);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onBackPressed() {
        View v = findViewById(R.id.coordinator_layout);
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
            Fragment frag = getSupportFragmentManager().findFragmentByTag("SubFrag");
            if (frag != null)
                getSupportFragmentManager().beginTransaction().remove(frag).commitNowAllowingStateLoss();
        } else
            super.onBackPressed();
    }
}