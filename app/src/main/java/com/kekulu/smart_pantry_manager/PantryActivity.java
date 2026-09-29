package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private RecyclerView pantryRecyclerView;
    private PantryAdapter pantryAdapter;
    private DatabaseHelper databaseHelper;

    private TextView emptyPantryText;
    private FloatingActionButton addPantryButton;
    private EditText pantrySearchInput;

    private View pantryExpiryAlertBanner;
    private TextView pantryExpiryAlertText;

    private List<PantryItem> pantryItems;
    private List<PantryItem> allPantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantry);

        // ---------------------------------------------------------
        // TOOLBAR
        // ---------------------------------------------------------

        Toolbar toolbar = findViewById(R.id.pantryToolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // ---------------------------------------------------------
        // DATABASE
        // ---------------------------------------------------------

        databaseHelper = new DatabaseHelper(this);

        // ---------------------------------------------------------
        // VIEWS
        // ---------------------------------------------------------

        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        emptyPantryText = findViewById(R.id.emptyPantryText);
        addPantryButton = findViewById(R.id.addPantryButton);
        pantrySearchInput = findViewById(R.id.pantrySearchInput);

        pantryExpiryAlertBanner = findViewById(R.id.pantryExpiryAlertBanner);
        pantryExpiryAlertText = findViewById(R.id.pantryExpiryAlertText);

        // ---------------------------------------------------------
        // SEARCH LISTENER
        // ---------------------------------------------------------

        if (pantrySearchInput != null) {
            pantrySearchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterPantry(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // ---------------------------------------------------------
        // RECYCLER VIEW
        // ---------------------------------------------------------

        pantryRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryItems = new ArrayList<>();
        allPantryItems = new ArrayList<>();

        pantryAdapter = new PantryAdapter(
                this,
                pantryItems,
                new PantryAdapter.OnPantryItemClickListener() {

                    @Override
                    public void onEditClick(PantryItem item) {

                        Intent intent = new Intent(
                                PantryActivity.this,
                                AddEditIngredientActivity.class
                        );

                        intent.putExtra(
                                "mode",
                                "edit"
                        );

                        intent.putExtra(
                                "id",
                                item.getId()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(PantryItem item) {

                        deletePantryItem(item);
                    }
                }
        );

        pantryRecyclerView.setAdapter(pantryAdapter);

        // ---------------------------------------------------------
        // ADD BUTTON
        // ---------------------------------------------------------

        addPantryButton.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    "mode",
                    "add"
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // NAVIGATION
        // ---------------------------------------------------------

        TextView pantryNavPantry = findViewById(R.id.pantryNavPantry);
        TextView pantryNavRecipes = findViewById(R.id.pantryNavRecipes);
        TextView pantryNavSettings = findViewById(R.id.pantryNavSettings);

        if (pantryNavPantry != null) {
            pantryNavPantry.setOnClickListener(view -> {
                if (pantrySearchInput != null) {
                    pantrySearchInput.setText("");
                }
                loadPantryItems();
            });
        }

        if (pantryNavRecipes != null) {
            pantryNavRecipes.setOnClickListener(view -> {
                Intent intent = new Intent(
                        PantryActivity.this,
                        SuggestedRecipesActivity.class
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (pantryNavSettings != null) {
            pantryNavSettings.setOnClickListener(view -> {
                Intent intent = new Intent(
                        PantryActivity.this,
                        SettingsActivity.class
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // ---------------------------------------------------------
        // LOAD DATA
        // ---------------------------------------------------------

        loadPantryItems();
    }

    // =============================================================
    // LOAD PANTRY ITEMS
    // =============================================================

    private void loadPantryItems() {

        allPantryItems.clear();

        Cursor cursor =
                databaseHelper.getAllPantryItems();

        try {

            while (cursor.moveToNext()) {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_NAME
                                )
                        );

                if (name == null || name.trim().isEmpty()) {
                    name = "Pantry Item";
                }

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_UNIT
                                )
                        );

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_EXPIRY
                                )
                        );

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                allPantryItems.add(item);
            }

        } finally {

            cursor.close();
        }

        String currentQuery = pantrySearchInput != null ? pantrySearchInput.getText().toString() : "";
        filterPantry(currentQuery);

        // ---------------------------------------------------------
        // EXPIRING SOON ALERT CHECK
        // ---------------------------------------------------------

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);

        int expiringCount = 0;
        if (notificationsEnabled) {
            for (PantryItem item : allPantryItems) {
                long days = DateUtils.getDaysUntilExpiry(item.getExpiryDate());
                if (days <= 3) {
                    expiringCount++;
                }
            }
        }

        if (notificationsEnabled && expiringCount > 0 && pantryExpiryAlertBanner != null) {
            pantryExpiryAlertBanner.setVisibility(View.VISIBLE);
            if (pantryExpiryAlertText != null) {
                pantryExpiryAlertText.setText("⚠️ Alert: " + expiringCount + " ingredient(s) in your pantry are expiring soon or expired!");
            }
        } else if (pantryExpiryAlertBanner != null) {
            pantryExpiryAlertBanner.setVisibility(View.GONE);
        }
    }

    // =============================================================
    // FILTER PANTRY (SEARCH)
    // =============================================================

    private void filterPantry(String query) {
        pantryItems.clear();
        String lowerQuery = query.toLowerCase(Locale.ROOT).trim();
        if (lowerQuery.isEmpty()) {
            pantryItems.addAll(allPantryItems);
        } else {
            for (PantryItem item : allPantryItems) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
                    pantryItems.add(item);
                }
            }
        }
        if (pantryAdapter != null) {
            pantryAdapter.notifyDataSetChanged();
        }

        // ---------------------------------------------------------
        // EMPTY STATE
        // ---------------------------------------------------------

        if (pantryItems.isEmpty()) {

            emptyPantryText.setVisibility(
                    TextView.VISIBLE
            );
            emptyPantryText.setText(allPantryItems.isEmpty() ? "Your pantry is empty.\nAdd ingredients to get started." : "No matching ingredients found.");

            pantryRecyclerView.setVisibility(
                    RecyclerView.GONE
            );

        } else {

            emptyPantryText.setVisibility(
                    TextView.GONE
            );

            pantryRecyclerView.setVisibility(
                    RecyclerView.VISIBLE
            );
        }
    }

    // =============================================================
    // DELETE PANTRY ITEM
    // =============================================================

    private void deletePantryItem(PantryItem item) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName()
                                + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper.deletePantryItem(
                                    item.getId()
                            );

                            loadPantryItems();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =============================================================
    // REFRESH WHEN RETURNING TO SCREEN
    // =============================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadPantryItems();
        }
    }
}
