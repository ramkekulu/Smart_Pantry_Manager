package com.kekulu.smart_pantry_manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerViewer extends RecyclerView.Adapter<RecyclerViewer.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;

    public RecyclerViewer(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        if (holder.titleText != null) {
            holder.titleText.setText(recipe.getTitle());
        }

        // --- Intent code MUST be inside an OnClickListener method ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);

            // Pass all recipe details to RecipeDetailActivity
            intent.putExtra("RECIPE_TITLE", recipe.getTitle());
            intent.putExtra("RECIPE_INGREDIENTS", recipe.getIngredients());
            intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());
            intent.putExtra("RECIPE_PREP_TIME", recipe.getPrepTime());
            intent.putExtra("RECIPE_SERVINGS", recipe.getServings());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Match this ID to your item_recipe.xml title TextView ID
            titleText = itemView.findViewById(R.id.recipeName);
        }
    }
}
