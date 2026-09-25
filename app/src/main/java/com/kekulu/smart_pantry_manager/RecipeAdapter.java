package com.kekulu.smart_pantry_manager;

import android.content.Context;
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

        holder.recipeDescription.setText(
                "You have all the ingredients needed to make this recipe."
        );

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

