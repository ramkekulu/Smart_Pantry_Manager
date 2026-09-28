package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView recipeNameText;
    private TextView ingredientsText;
    private TextView cookingInstructionsText;
    private TextView prepTimeText;
    private TextView servingsText;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        // Bind Toolbar and set up back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Bind Views matching activity_recipe_detail.xml IDs
        recipeNameText = findViewById(R.id.text_recipe_title);
        ingredientsText = findViewById(R.id.text_ingredients);
        cookingInstructionsText = findViewById(R.id.text_instructions);
        prepTimeText = findViewById(R.id.text_prep_time);
        servingsText = findViewById(R.id.text_servings);

        // Extract Intent Data
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("RECIPE_TITLE");
            String ingredients = intent.getStringExtra("RECIPE_INGREDIENTS");
            String instructions = intent.getStringExtra("RECIPE_INSTRUCTIONS");
            String prepTime = intent.getStringExtra("RECIPE_PREP_TIME");
            String servings = intent.getStringExtra("RECIPE_SERVINGS");

            int recipeId = intent.getIntExtra("recipe_id", -1);
            if (recipeId != -1) {
                // Fetch recipe details from Database if recipe_id is provided
                Cursor recipeCursor = databaseHelper.getRecipe(recipeId);
                if (recipeCursor != null) {
                    try {
                        if (recipeCursor.moveToFirst()) {
                            if (title == null || title.isEmpty()) {
                                title = recipeCursor.getString(
                                        recipeCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_NAME)
                                );
                            }
                            if (instructions == null || instructions.isEmpty()) {
                                instructions = recipeCursor.getString(
                                        recipeCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_METHOD)
                                );
                            }
                        }
                    } finally {
                        recipeCursor.close();
                    }
                }

                if (ingredients == null || ingredients.isEmpty()) {
                    Cursor ingCursor = databaseHelper.getRecipeIngredients(recipeId);
                    if (ingCursor != null) {
                        try {
                            StringBuilder ingBuilder = new StringBuilder();
                            while (ingCursor.moveToNext()) {
                                String ingName = ingCursor.getString(
                                        ingCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENT_NAME)
                                );
                                double qty = ingCursor.getDouble(
                                        ingCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_REQUIRED_QUANTITY)
                                );
                                String unit = ingCursor.getString(
                                        ingCursor.getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENT_UNIT)
                                );

                                String qtyStr = (qty == (long) qty) ? String.format("%d", (long) qty) : String.valueOf(qty);
                                ingBuilder.append("• ").append(qtyStr).append(" ").append(unit).append(" ").append(ingName).append("\n");
                            }
                            if (ingBuilder.length() > 0) {
                                ingredients = ingBuilder.toString().trim();
                            }
                        } finally {
                            ingCursor.close();
                        }
                    }
                }
            }

            // Populate Recipe Title
            if (recipeNameText != null) {
                recipeNameText.setText(title != null && !title.isEmpty() ? title : "Suggested Recipe");
            }

            // Populate Ingredients with bullet formatting fallback
            if (ingredientsText != null) {
                if (ingredients != null && !ingredients.isEmpty()) {
                    ingredientsText.setText(formatIngredients(ingredients));
                } else {
                    ingredientsText.setText("No ingredients available.");
                }
            }

            // Populate Cooking Instructions
            if (cookingInstructionsText != null) {
                if (instructions != null && !instructions.isEmpty()) {
                    cookingInstructionsText.setText(instructions);
                } else {
                    cookingInstructionsText.setText("No cooking instructions available.");
                }
            }

            // Populate Prep Time metadata
            if (prepTimeText != null) {
                if (prepTime != null && !prepTime.isEmpty()) {
                    prepTimeText.setText("⏱ Prep: " + prepTime);
                } else {
                    prepTimeText.setText("⏱ Prep: 15 mins");
                }
            }

            // Populate Servings metadata
            if (servingsText != null) {
                if (servings != null && !servings.isEmpty()) {
                    servingsText.setText("🍽 Servings: " + servings);
                } else {
                    servingsText.setText("🍽 Servings: 2 servings");
                }
            }
        }
    }

    /**
     * Formats raw ingredient strings or comma-separated lists into bullet points.
     */
    private String formatIngredients(String rawIngredients) {
        if (rawIngredients.contains("•")) {
            return rawIngredients.trim(); // Already formatted with bullet points
        }
        String[] items = rawIngredients.split(",");
        StringBuilder formatted = new StringBuilder();
        for (String item : items) {
            if (!item.trim().isEmpty()) {
                formatted.append("• ").append(item.trim()).append("\n");
            }
        }
        return formatted.toString().trim();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
