package com.kekulu.smart_pantry_manager;

import java.util.ArrayList;
import java.util.List;

public class RecipeItem {

    private int id;
    private String name;
    private String method;
    private List<String> missingIngredients;

    public RecipeItem(int id, String name, String method) {
        this(id, name, method, new ArrayList<>());
    }

    public RecipeItem(int id, String name, String method, List<String> missingIngredients) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.missingIngredients = missingIngredients != null ? missingIngredients : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public List<String> getMissingIngredients() {
        return missingIngredients;
    }

    public boolean isComplete() {
        return missingIngredients == null || missingIngredients.isEmpty();
    }
}
