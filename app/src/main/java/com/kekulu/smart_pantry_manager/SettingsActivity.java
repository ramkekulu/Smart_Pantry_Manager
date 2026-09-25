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

public class SettingsActivity extends AppCompatActivity {

    private TextView txtProfileName;
    private TextView txtProfileEmail;

    private Switch switchNotifications;
    private Switch switchDarkMode;

    private Button btnChangePassword;
    private Button btnClearPantry;
    private Button btnAbout;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

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
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // Buttons
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnClearPantry = findViewById(R.id.btnClearPantry);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);

        // 1. EXPIRY REMINDERS SWITCH
        SharedPreferences prefs =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        boolean notificationsEnabled =
                prefs.getBoolean("notifications_enabled", false);

        switchNotifications.setChecked(notificationsEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit()
                    .putBoolean("notifications_enabled", isChecked)
                    .apply();

            String status = isChecked
                    ? "Expiry reminders enabled"
                    : "Expiry reminders disabled";

            Toast.makeText(
                    SettingsActivity.this,
                    status,
                    Toast.LENGTH_SHORT
            ).show();
        });

        // 2. DARK MODE SWITCH
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

        // 3. CHANGE PASSWORD BUTTON
        btnChangePassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    ChangePasswordActivity.class
            );

            startActivity(intent);
        });

        // 4. CLEAR PANTRY BUTTON
        btnClearPantry.setOnClickListener(v -> {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Clear Pantry")
                    .setMessage(
                            "Are you sure you want to delete all items in your pantry? " +
                                    "This action cannot be undone."
                    )
                    .setPositiveButton("Clear All", (dialog, which) -> {

                        // TODO: Add database clear function here
                        // e.g. dbHelper.clearPantry();

                        Toast.makeText(
                                SettingsActivity.this,
                                "Pantry cleared successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 5. ABOUT BUTTON
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

        // 6. LOGOUT BUTTON
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

