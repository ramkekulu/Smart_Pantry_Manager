package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private TextView noRecipesText;
    private View noRecipesContainer;
    private Button btnAddMoreIngredients;

    private DatabaseHelper databaseHelper;

    private RecipeAdapter recipeAdapter;
    private List<RecipeItem> recipeItems;

    private TextView navPantry;
    private TextView navRecipes;
    private TextView navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Toolbar toolbar = findViewById(R.id.recipesToolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        databaseHelper = new DatabaseHelper(this);

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        noRecipesText = findViewById(R.id.noRecipesText);
        noRecipesContainer = findViewById(R.id.noRecipesContainer);
        btnAddMoreIngredients = findViewById(R.id.btnAddMoreIngredients);

        if (btnAddMoreIngredients != null) {
            btnAddMoreIngredients.setOnClickListener(v -> {
                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        PantryActivity.class
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        navPantry = findViewById(R.id.recipesNavPantry);
        navRecipes = findViewById(R.id.recipesNavRecipes);
        navSettings = findViewById(R.id.recipesNavSettings);

        recipesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeItems = new ArrayList<>();

        recipeAdapter = new RecipeAdapter(
                this,
                recipeItems,
                recipe -> {

                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra("recipe_id", recipe.getId());
                    intent.putExtra("RECIPE_TITLE", recipe.getName());
                    intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getMethod());

                    startActivity(intent);
                }
        );

        recipesRecyclerView.setAdapter(recipeAdapter);

        navPantry.setOnClickListener(view -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    PantryActivity.class
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        navRecipes.setOnClickListener(view -> {
            loadSuggestedRecipes();
        });

        navSettings.setOnClickListener(view -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    SettingsActivity.class
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        recipeItems.clear();

        List<Integer> suggestedRecipeIds =
                databaseHelper.getSuggestedRecipeIds();

        for (int recipeId : suggestedRecipeIds) {

            Cursor cursor = databaseHelper.getRecipe(recipeId);

            try {

                if (cursor.moveToFirst()) {

                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_ID
                            )
                    );

                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_NAME
                            )
                    );

                    String method = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_METHOD
                            )
                    );

                    List<String> missing = databaseHelper.getMissingIngredients(id);

                    RecipeItem recipe =
                            new RecipeItem(id, name, method, missing);

                    recipeItems.add(recipe);
                }

            } finally {
                cursor.close();
            }
        }

        recipeAdapter.notifyDataSetChanged();

        if (recipeItems.isEmpty()) {

            recipesRecyclerView.setVisibility(
                    RecyclerView.GONE
            );

            if (noRecipesContainer != null) {
                noRecipesContainer.setVisibility(View.VISIBLE);
            } else if (noRecipesText != null) {
                noRecipesText.setText("No recipes match your pantry yet - add more ingredients");
                noRecipesText.setVisibility(View.VISIBLE);
            }

        } else {

            recipesRecyclerView.setVisibility(
                    RecyclerView.VISIBLE
            );

            if (noRecipesContainer != null) {
                noRecipesContainer.setVisibility(View.GONE);
            }
            if (noRecipesText != null) {
                noRecipesText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadSuggestedRecipes();
        }
    }
}
