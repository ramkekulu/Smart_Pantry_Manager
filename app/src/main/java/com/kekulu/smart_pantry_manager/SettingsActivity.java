package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private TextView txtProfileName;
    private TextView txtProfileEmail;

    private Switch switchNotifications;
    private Switch switchUnits;
    private Switch switchDarkMode;

    private Button btnChangePassword;
    private Button btnClearPantry;
    private Button btnAbout;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Profile
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);

        // Load logged-in user data from SharedPreferences
        SharedPreferences userPrefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        String userName =
                userPrefs.getString("user_name", "User");

        String userEmail =
                userPrefs.getString("user_email", "Email");

        // Profile information
        txtProfileName.setText(userName);
        txtProfileEmail.setText(userEmail);

        // Switches
        switchNotifications = findViewById(R.id.switchNotifications);
        switchUnits = findViewById(R.id.switchUnits);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // Buttons
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnClearPantry = findViewById(R.id.btnClearPantry);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);

        // Bottom Navigation Tabs
        TextView settingsNavPantry = findViewById(R.id.settingsNavPantry);
        TextView settingsNavRecipes = findViewById(R.id.settingsNavRecipes);
        TextView settingsNavSettings = findViewById(R.id.settingsNavSettings);

        if (settingsNavPantry != null) {
            settingsNavPantry.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, PantryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (settingsNavRecipes != null) {
            settingsNavRecipes.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, SuggestedRecipesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // 1. EXPIRY REMINDERS SWITCH
        SharedPreferences prefs =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        boolean notificationsEnabled =
                prefs.getBoolean("notifications_enabled", true);

        switchNotifications.setChecked(notificationsEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit()
                    .putBoolean("notifications_enabled", isChecked)
                    .apply();

            String status = isChecked
                    ? "Expiring-soon alerts enabled"
                    : "Expiring-soon alerts disabled";

            Toast.makeText(
                    SettingsActivity.this,
                    status,
                    Toast.LENGTH_SHORT
            ).show();
        });

        // 2. UNITS PREFERENCE SWITCH
        boolean useImperialUnits =
                prefs.getBoolean("use_imperial_units", false);

        if (switchUnits != null) {
            switchUnits.setChecked(useImperialUnits);

            switchUnits.setOnCheckedChangeListener((buttonView, isChecked) -> {

                prefs.edit()
                        .putBoolean("use_imperial_units", isChecked)
                        .apply();

                String status = isChecked
                        ? "Units preference set to Imperial (oz, fl oz)"
                        : "Units preference set to Metric (g, ml)";

                Toast.makeText(
                        SettingsActivity.this,
                        status,
                        Toast.LENGTH_SHORT
                ).show();
            });
        }

        // 3. DARK MODE SWITCH
        boolean darkModeEnabled =
                AppCompatDelegate.getDefaultNightMode()
                        == AppCompatDelegate.MODE_NIGHT_YES;

        switchDarkMode.setChecked(darkModeEnabled);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        });

        // 4. CHANGE PASSWORD BUTTON
        btnChangePassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    ChangePasswordActivity.class
            );

            startActivity(intent);
        });

        // 5. CLEAR PANTRY BUTTON
        btnClearPantry.setOnClickListener(v -> {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Clear Pantry")
                    .setMessage(
                            "Are you sure you want to delete all items in your pantry? " +
                                    "This action cannot be undone."
                    )
                    .setPositiveButton("Clear All", (dialog, which) -> {

                        DatabaseHelper dbHelper = new DatabaseHelper(SettingsActivity.this);
                        dbHelper.clearPantry();

                        Toast.makeText(
                                SettingsActivity.this,
                                "Pantry cleared successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 6. ABOUT BUTTON
        btnAbout.setOnClickListener(v -> {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("About Smart Pantry Manager")
                    .setMessage(
                            "Smart Pantry Manager v1.0\n\n" +
                                    "Keep track of your grocery items, manage expiry dates, " +
                                    "and reduce food waste effortlessly."
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });

        // 7. LOGOUT BUTTON
        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout", (dialog, which) -> {

                        // Clear user session
                        userPrefs.edit().clear().apply();

                        Intent intent = new Intent(
                                SettingsActivity.this,
                                LoginActivity.class
                        );

                        // Clear activity stack so user cannot press Back
                        // to return to the Settings screen.
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
