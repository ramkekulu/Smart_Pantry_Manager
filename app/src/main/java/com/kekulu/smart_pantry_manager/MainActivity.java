package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btnViewPantry;
    private Button btnSuggestedRecipes;

    private TextView navPantry;
    private TextView navRecipes;
    private TextView navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ---------------------------------------------------------
        // TOOLBAR
        // ---------------------------------------------------------

        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // ---------------------------------------------------------
        // BUTTONS
        // ---------------------------------------------------------

        btnViewPantry = findViewById(R.id.btnViewPantry);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        navPantry = findViewById(R.id.navPantry);
        navRecipes = findViewById(R.id.navRecipes);
        navSettings = findViewById(R.id.navSettings);

        // ---------------------------------------------------------
        // PANTRY BUTTON
        // ---------------------------------------------------------

        btnViewPantry.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // SUGGESTED RECIPES BUTTON
        // ---------------------------------------------------------

        btnSuggestedRecipes.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // BOTTOM NAVIGATION
        // ---------------------------------------------------------

        navPantry.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        navRecipes.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        navSettings.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }
}
