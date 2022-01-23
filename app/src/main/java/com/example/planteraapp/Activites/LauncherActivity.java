package com.example.planteraapp.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.planteraapp.R;

public class LauncherActivity extends AppCompatActivity {
    // Default Constants used throughout the app java files
    public static String SharedFile = "LaunchFiles";
    public static String navigateToKey = "destination";
    public static String CHANNEL_ID = "2119";
    public static String BundleKey = "NavigateBundle";
    public static String plantNameKey = "plantName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * @param: m = -1 : Follow System Theme
         * @param: m = 1 : Follow Light Mode Theme
         * @param m = 2 : Follow Dark Mode Theme
         * m is the user preferred app theme --> Stored in app preferences
         * This sharedPreference is editable in settings.java file when user wants to change the app theme
         * LauncherActivity.java on start (App start) may or may not change theme depending on the user preference
         */
        // 10 is default value when user hasn't set the app theme yet --> So we use the System defined theme
        int m = getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("mode", -1);
        AppCompatDelegate.setDefaultNightMode(m);
        super.onCreate(savedInstanceState);
        // Later setContent View --> First changing the theme, then only inflating the activity layout
        setContentView(R.layout.activity_launcher);
        // Used to animate the logo image & text logo to separate themselves vertically
        // Logo image animates to translate to top & going out of the layout
        // Logo text animates to translate to bottom & going out of the layout
        // Later on animation ends --> activity fades to start new Home activity or Intro activity depending on preferences.
        findViewById(R.id.image_logo).animate().translationY(-3200).setDuration(800).setStartDelay(2500);
        findViewById(R.id.text_logo).animate().translationY(1400).setDuration(800).setStartDelay(2500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // checkNewUser() --> A stub function to check which activity to open
                // AKA --> Is it a new user or old one, Open Home.java or Intro_Activity.java?
                startActivity(new Intent(LauncherActivity.this, checkNewUser()));
                // No transition set
                overridePendingTransition(0, 0);
                finish();
            }

            // On any error, where system crashes the animation, close the app
            @Override
            public void onAnimationCancel(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    // A stub function to check which activity to open
    // AKA --> Is it a new user or old one, Open Home.java or Intro_Activity.java?
    public Class<? extends AppCompatActivity> checkNewUser() {
        Log.d("IsOld", String.valueOf(getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("IsOld", 0)));
        return getSharedPreferences(SharedFile, Context.MODE_PRIVATE).getInt("IsOld", 0) == 0 ? Intro_Activity.class : Home.class;
    }

    /* STATIC FUNCTIONS TO USE THROUGHOUT THE JAVA FILES OF THE APPLICATION */
    public static String getThemeName(int id) {
        switch (id) {
            case R.style.Theme_PlanteraApp_Chiffon_Purple:
                return "Chiffon Purple";
            case R.style.Theme_PlanteraApp_Monochromatic_Brown:
                return "Monochromatic Brown";
            case R.style.Theme_PlanteraApp_Accent_Dark:
                return "Accent Dark";
            case R.style.Theme_PlanteraApp_Dracula_Light:
                return "Dracula Light";
            default:
                return "Default App Theme";
        }
    }

    public static int getColour(String name) {
        switch (name) {
            case "Water":
            case "water":
            case "Aqua":
            case "aqua":
                return R.color.Reminder_Water;
            case "Soil":
            case "Fertile":
            case "soil":
            case "fertile":
                return R.color.Reminder_Soil;
            default:
                return R.color.Reminder_Other;
        }
    }

    // Request to open the soft keyboard & focus on the editText
    public static void openSoftKeyboard(final Context context, final EditText editText) {
        editText.requestFocus();
        editText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }
}