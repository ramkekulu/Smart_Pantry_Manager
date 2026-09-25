package com.kekulu.smart_pantry_manager;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddEditIngredientActivity
        extends AppCompatActivity {

    private EditText ingredientName;
    private EditText quantity;
    private EditText expiryDate;

    private Spinner unitSpinner;

    private Button saveButton;
    private Button selectDateButton;

    private DatabaseHelper databaseHelper;

    private String mode;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_ingredient
        );

        // ---------------------------------------------------------
        // TOOLBAR
        // ---------------------------------------------------------

        Toolbar toolbar =
                findViewById(R.id.addEditToolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayShowTitleEnabled(false);
        }

        // ---------------------------------------------------------
        // DATABASE
        // ---------------------------------------------------------

        databaseHelper =
                new DatabaseHelper(this);

        // ---------------------------------------------------------
        // VIEWS
        // ---------------------------------------------------------

        ingredientName =
                findViewById(R.id.ingredientName);

        quantity =
                findViewById(R.id.ingredientQuantity);

        expiryDate =
                findViewById(R.id.ingredientExpiry);

        unitSpinner =
                findViewById(R.id.ingredientUnit);

        saveButton =
                findViewById(R.id.saveIngredientButton);

        selectDateButton =
                findViewById(R.id.selectExpiryButton);

        // ---------------------------------------------------------
        // UNIT SPINNER
        // ---------------------------------------------------------

        String[] units = {
                "piece",
                "g",
                "kg",
                "ml",
                "l",
                "cup",
                "tbsp",
                "tsp"
        };

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitSpinner.setAdapter(unitAdapter);

        // ---------------------------------------------------------
        // DATE PICKER
        // ---------------------------------------------------------

        selectDateButton.setOnClickListener(
                view -> showDatePicker()
        );

        // ---------------------------------------------------------
        // MODE
        // ---------------------------------------------------------

        mode = getIntent()
                .getStringExtra("mode");

        if (mode == null) {
            mode = "add";
        }

        if (mode.equals("edit")) {

            itemId = getIntent()
                    .getIntExtra("id", -1);

            loadItemForEditing();

        } else {

            saveButton.setText(
                    "ADD INGREDIENT"
            );
        }

        // ---------------------------------------------------------
        // SAVE
        // ---------------------------------------------------------

        saveButton.setOnClickListener(
                view -> saveIngredient()
        );
    }

    // =============================================================
    // LOAD ITEM FOR EDITING
    // =============================================================

    private void loadItemForEditing() {

        TextView title =
                findViewById(R.id.addEditTitle);

        title.setText("Edit Ingredient");

        saveButton.setText(
                "UPDATE INGREDIENT"
        );

        Cursor cursor =
                databaseHelper.getPantryItem(itemId);

        try {

            if (cursor.moveToFirst()) {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_NAME
                                )
                        );

                double itemQuantity =
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

                String expiry =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.PANTRY_EXPIRY
                                )
                        );

                ingredientName.setText(name);

                quantity.setText(
                        String.valueOf(itemQuantity)
                );

                expiryDate.setText(
                        expiry == null ? "" : expiry
                );

                ArrayAdapter<String> adapter =
                        (ArrayAdapter<String>)
                                unitSpinner.getAdapter();

                int spinnerPosition =
                        adapter.getPosition(unit);

                if (spinnerPosition >= 0) {

                    unitSpinner.setSelection(
                            spinnerPosition
                    );
                }
            }

        } finally {

            cursor.close();
        }
    }

    // =============================================================
    // SAVE INGREDIENT
    // =============================================================

    private void saveIngredient() {

        String name =
                ingredientName
                        .getText()
                        .toString()
                        .trim();

        String quantityText =
                quantity
                        .getText()
                        .toString()
                        .trim();

        String unit =
                unitSpinner
                        .getSelectedItem()
                        .toString();

        String expiry =
                expiryDate
                        .getText()
                        .toString()
                        .trim();

        // ---------------------------------------------------------
        // VALIDATION
        // ---------------------------------------------------------

        if (TextUtils.isEmpty(name)) {

            ingredientName.setError(
                    "Enter an ingredient name"
            );

            ingredientName.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(quantityText)) {

            quantity.setError(
                    "Enter a quantity"
            );

            quantity.requestFocus();

            return;
        }

        double quantityValue;

        try {

            quantityValue =
                    Double.parseDouble(
                            quantityText
                    );

        } catch (NumberFormatException e) {

            quantity.setError(
                    "Enter a valid number"
            );

            quantity.requestFocus();

            return;
        }

        if (quantityValue <= 0) {

            quantity.setError(
                    "Quantity must be greater than 0"
            );

            quantity.requestFocus();

            return;
        }

        // ---------------------------------------------------------
        // ADD
        // ---------------------------------------------------------

        if (mode.equals("add")) {

            long result =
                    databaseHelper.addPantryItem(
                            name,
                            quantityValue,
                            unit,
                            expiry
                    );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Ingredient added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Unable to add ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }

        // ---------------------------------------------------------
        // UPDATE
        // ---------------------------------------------------------

        else {

            int result =
                    databaseHelper.updatePantryItem(
                            itemId,
                            name,
                            quantityValue,
                            unit,
                            expiry
                    );

            if (result > 0) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Unable to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // =============================================================
    // DATE PICKER
    // =============================================================

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(Calendar.YEAR);

        int month =
                calendar.get(Calendar.MONTH);

        int day =
                calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String date =
                                    String.format(
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    );

                            expiryDate.setText(date);
                        },
                        year,
                        month,
                        day
                );

        dialog.show();
    }
}
