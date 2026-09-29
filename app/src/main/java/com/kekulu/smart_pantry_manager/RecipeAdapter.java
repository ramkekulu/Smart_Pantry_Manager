package com.kekulu.smart_pantry_manager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<RecipeItem> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeItem recipe);
    }

    public RecipeAdapter(
            Context context,
            List<RecipeItem> recipes,
            OnRecipeClickListener listener
    ) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.item_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {

        RecipeItem recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());

        if (recipe.isComplete()) {
            holder.recipeDescription.setText("✓ You have all ingredients needed to make this recipe!");
            holder.recipeDescription.setTextColor(Color.parseColor("#2E7D32")); // Green
        } else {
            List<String> missing = recipe.getMissingIngredients();
            StringBuilder sb = new StringBuilder("⚠️ Missing ");
            sb.append(missing.size()).append(" ingredient");
            if (missing.size() > 1) {
                sb.append("s");
            }
            sb.append(": ");
            for (int i = 0; i < missing.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(missing.get(i));
            }
            holder.recipeDescription.setText(sb.toString());
            holder.recipeDescription.setTextColor(Color.parseColor("#C62828")); // Red
        }

        holder.viewRecipeButton.setOnClickListener(view ->
                listener.onRecipeClick(recipe)
        );

        holder.itemView.setOnClickListener(view ->
                listener.onRecipeClick(recipe)
        );
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView recipeName;
        TextView recipeDescription;
        Button viewRecipeButton;

        public RecipeViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            recipeName = itemView.findViewById(
                    R.id.recipeName
            );

            recipeDescription = itemView.findViewById(
                    R.id.recipeDescription
            );

            viewRecipeButton = itemView.findViewById(
                    R.id.viewRecipeButton
            );
        }
    }
}
