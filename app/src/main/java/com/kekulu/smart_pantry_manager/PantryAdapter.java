package com.kekulu.smart_pantry_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final Context context;
    private final List<PantryItem> pantryItems;
    private final OnPantryItemClickListener listener;

    public interface OnPantryItemClickListener {

        void onEditClick(PantryItem item);

        void onDeleteClick(PantryItem item);
    }

    public PantryAdapter(
            Context context,
            List<PantryItem> pantryItems,
            OnPantryItemClickListener listener) {

        this.context = context;
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_pantry,
                        parent,
                        false
                );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        // Ensure ingredient name is never blank
        if (item.getName() != null && !item.getName().trim().isEmpty()) {
            holder.ingredientName.setText(item.getName().trim());
            holder.ingredientName.setVisibility(View.VISIBLE);
        } else {
            holder.ingredientName.setText("Ingredient");
            holder.ingredientName.setVisibility(View.VISIBLE);
        }

        String quantityText;

        if (item.getQuantity()
                == Math.floor(item.getQuantity())) {

            quantityText = String.format(
                    Locale.getDefault(),
                    "%.0f",
                    item.getQuantity()
            );

        } else {

            quantityText = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    item.getQuantity()
            );
        }

        holder.quantityText.setText(
                quantityText + " " + item.getUnit()
        );

        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);

        long days = DateUtils.getDaysUntilExpiry(item.getExpiryDate());

        if (notificationsEnabled && days != Long.MAX_VALUE && days <= 3) {
            if (days < 0) {
                holder.expiryText.setText("🚨 EXPIRED! (" + item.getExpiryDate() + ")");
            } else if (days == 0) {
                holder.expiryText.setText("⚠️ EXPIRING TODAY! (" + item.getExpiryDate() + ")");
            } else {
                holder.expiryText.setText("⚠️ EXPIRING SOON! (" + days + " day(s) left: " + item.getExpiryDate() + ")");
            }
            holder.expiryText.setTextColor(Color.parseColor("#A94442"));
        } else {
            if (item.getExpiryDate() == null
                    || item.getExpiryDate().trim().isEmpty()) {

                holder.expiryText.setText(
                        "Expiry: Not specified"
                );

            } else {

                holder.expiryText.setText(
                        "Expiry: " + item.getExpiryDate()
                );
            }
            holder.expiryText.setTextColor(Color.parseColor("#000000"));
        }

        holder.editButton.setOnClickListener(
                view -> listener.onEditClick(item)
        );

        holder.deleteButton.setOnClickListener(
                view -> listener.onDeleteClick(item)
        );
    }

    @Override
    public int getItemCount() {

        return pantryItems.size();
    }

    // =============================================================
    // VIEW HOLDER
    // =============================================================

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView ingredientName;
        TextView quantityText;
        TextView expiryText;

        Button editButton;
        Button deleteButton;

        public PantryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            ingredientName =
                    itemView.findViewById(
                            R.id.itemIngredientName
                    );

            quantityText =
                    itemView.findViewById(
                            R.id.itemQuantity
                    );

            expiryText =
                    itemView.findViewById(
                            R.id.itemExpiry
                    );

            editButton =
                    itemView.findViewById(
                            R.id.itemEditButton
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.itemDeleteButton
                    );
        }
    }
}
