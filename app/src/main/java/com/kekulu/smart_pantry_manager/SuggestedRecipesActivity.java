package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private TextView noRecipesText;
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

        Toolbar toolbar = findViewById(R.id.recipesToolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        databaseHelper = new DatabaseHelper(this);

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        noRecipesText = findViewById(R.id.noRecipesText);

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

                    startActivity(intent);
                }
        );

        recipesRecyclerView.setAdapter(recipeAdapter);

        navPantry.setOnClickListener(view -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        navRecipes.setOnClickListener(view -> {
            loadSuggestedRecipes();
        });

        navSettings.setOnClickListener(view -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        recipeItems.clear();

        /*
         * This method returns ONLY recipes that can be
         * completely prepared using the current pantry.
         *
         * It checks:
         * 1. Every required ingredient exists.
         * 2. Pantry quantity is sufficient.
         * 3. Units match.
         * 4. Ingredient names are normalized.
         */
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

                    RecipeItem recipe =
                            new RecipeItem(id, name, method);

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

            noRecipesText.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            recipesRecyclerView.setVisibility(
                    RecyclerView.VISIBLE
            );

            noRecipesText.setVisibility(
                    TextView.GONE
            );
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
