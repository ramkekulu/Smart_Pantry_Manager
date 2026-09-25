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

        holder.ingredientName.setText(
                item.getName()
        );

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

