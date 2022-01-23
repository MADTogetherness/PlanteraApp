package com.example.planteraapp.Activites;
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

import com.example.planteraapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

// The Home.java inflates activity_home.xml & sets up the fragment container to inflate different fragments
// Using the NavController Component
public class Home extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, NavController.OnDestinationChangedListener {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton add_plants_fab;
    NavController navController;
    NavOptions.Builder options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Initialise the layout views & viewGroups
        init();
    }

    public void init() {
        // Bottom Navigation View which holds menu items of @menu/bottom_navigation_options.xml
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        // Middle Floating Action Button to compensate for placeholder in
        // @menu/bottom_navigation_options.xml
        // Placeholder click redirects to FAB
        add_plants_fab = findViewById(R.id.add_plants_fab);
        //  fragment Container View which holds navigation graph of fragments
        //  @navigation/bottom_navigation_fragments.xml
        // Default start fragment is Calendar.java or fragment_calendar.xml
        navController = Navigation.findNavController(this, R.id.nav_controller);
        // Setting up nav controller with bottomNavigation View
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        // Applying animation & different settings to inflate/Remove fragments in
        // fragment container or commonly called nav controller
        options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.fragment_enter_anim)
                .setExitAnim(R.anim.fragment_exit_anim)
                .setPopEnterAnim(R.anim.fragment_popenter_anim)
                .setPopExitAnim(R.anim.fragment_popexit_anim);

        // Manually redirect the click
        // Check the destination is not selected if its already inflated inside container
        // AKA Prevent re-inflation of already inflated fragment
        add_plants_fab.setOnClickListener(view -> {
            if (isValidDestination(R.id.newPlant_fragment)) fabSelect();
        });
        bottomNavigationView.setOnItemSelectedListener(this);
        navController.addOnDestinationChangedListener(this);

        // GOING TO DIFFERENT FRAGMENTS FROM ANOTHER ACTIVITY
        // Directly navigate to the fragment using the intent of fragment ID rather than default calendar fragment
        int v = getIntent().getIntExtra(LauncherActivity.navigateToKey, -1);
        // Get Saved Bundle  and pass as arguments, if any
        Bundle b = getIntent().getBundleExtra(LauncherActivity.BundleKey);
        if (v != -1) {
            navController.navigate(v, b, options.build());
            getIntent().putExtra(LauncherActivity.navigateToKey, -1);
        }
    }


    // Manually navigate & replace fragment in Nav controller
    public void fabSelect() {
        bottomNavigationView.getMenu().findItem(R.id.placeholder).setChecked(true);
        navController.navigate(R.id.newPlant_fragment, null, options.build());
    }

    // A stub function to check for the click on the already inflated layout
    // This prevents re-inflation of fragment in Nav controller
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

    // A stub function to control on destination changed listener to manually handle FAB
    // Check if destination is FAB corresponding fragment
    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        add_plants_fab.setSupportBackgroundTintList(ContextCompat.getColorStateList(this, (destination.getId() == R.id.newPlant_fragment) ? R.color.Button_Primary : R.color.Section_Container));
    }

    // Recreating the activity for fade-in-fade-out animation upon changing the
    // app theme and change in font size
    @Override
    public void recreate() {
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        Intent intent = new Intent(this, getClass());
        intent.putExtra("saved_state", bundle);
        intent.putExtra(LauncherActivity.navigateToKey, R.id.action_calendar_to_settings);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Attach new context and change configuration of app to scale the font size based on user preferences
    // User can change the font size in settings.java of application, this function is corresponding to that
    // Called onCreate after activity gets recreated.
    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration newConfig = new Configuration(newBase.getResources().getConfiguration());
        newConfig.fontScale = newBase.getSharedPreferences(LauncherActivity.SharedFile, Context.MODE_PRIVATE).getFloat("font", 1f);
        applyOverrideConfiguration(newConfig);
        super.attachBaseContext(newBase);
    }


    // Custom backstack popping. Additional fragments inflating on top of the fragments
    // These additional fragments are inflated on top of the existing fragments & have to be removed
    // on the back pressed or after saving data
    // .SubFragments/SetReminder.java & .SubFragments/ColorTheme.java fragments
    @Override
    public void onBackPressed() {
        View v = findViewById(R.id.coordinator_layout);
        // UnHide bottom navigation when additional fragments are removed
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
            Fragment frag = getSupportFragmentManager().findFragmentByTag("SubFrag");
            if (frag != null)
                getSupportFragmentManager().beginTransaction().remove(frag).commitNowAllowingStateLoss();
        } else
            super.onBackPressed();
    }
}