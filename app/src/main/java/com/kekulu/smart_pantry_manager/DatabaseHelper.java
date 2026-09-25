package com.kekulu.smart_pantry_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ============================================================
    // DATABASE
    // ============================================================

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 4;

    // ============================================================
    // PANTRY TABLE
    // ============================================================

    public static final String TABLE_PANTRY = "pantry";

    public static final String PANTRY_ID = "id";
    public static final String PANTRY_NAME = "ingredient_name";
    public static final String PANTRY_QUANTITY = "quantity";
    public static final String PANTRY_UNIT = "unit";
    public static final String PANTRY_EXPIRY = "expiry_date";

    // ============================================================
    // RECIPES TABLE
    // ============================================================

    public static final String TABLE_RECIPES = "recipes";

    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_METHOD = "method";

    // ============================================================
    // RECIPE INGREDIENTS TABLE
    // ============================================================

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RECIPE_INGREDIENT_ID = "id";
    public static final String RECIPE_INGREDIENT_RECIPE_ID = "recipe_id";
    public static final String RECIPE_INGREDIENT_NAME = "ingredient_name";
    public static final String RECIPE_REQUIRED_QUANTITY =
            "required_quantity";
    public static final String RECIPE_INGREDIENT_UNIT = "unit";

    // ============================================================
    // USERS TABLE
    // ============================================================

    public static final String TABLE_USERS = "users";

    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ============================================================
    // CREATE DATABASE
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        createPantryTable(db);
        createRecipesTable(db);
        createRecipeIngredientsTable(db);
        createUsersTable(db);

        seedRecipes(db);
    }

    // ============================================================
    // DATABASE UPGRADE
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        /*
         * During development the database is recreated when
         * the database structure changes.
         *
         * This keeps the application database consistent.
         */

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPE_INGREDIENTS
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPES
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_PANTRY
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_USERS
        );

        onCreate(db);
    }

    // ============================================================
    // CREATE PANTRY TABLE
    // ============================================================

    private void createPantryTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        PANTRY_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        PANTRY_NAME +
                        " TEXT NOT NULL, " +

                        PANTRY_QUANTITY +
                        " REAL NOT NULL, " +

                        PANTRY_UNIT +
                        " TEXT NOT NULL, " +

                        PANTRY_EXPIRY +
                        " TEXT" +

                        ")";

        db.execSQL(sql);
    }

    // ============================================================
    // CREATE RECIPES TABLE
    // ============================================================

    private void createRecipesTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        RECIPE_METHOD +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(sql);
    }

    // ============================================================
    // CREATE RECIPE INGREDIENTS TABLE
    // ============================================================

    private void createRecipeIngredientsTable(
            SQLiteDatabase db
    ) {

        String sql =
                "CREATE TABLE " +
                        TABLE_RECIPE_INGREDIENTS + " (" +

                        RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_INGREDIENT_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        RECIPE_INGREDIENT_NAME +
                        " TEXT NOT NULL, " +

                        RECIPE_REQUIRED_QUANTITY +
                        " REAL NOT NULL, " +

                        RECIPE_INGREDIENT_UNIT +
                        " TEXT NOT NULL, " +

                        "FOREIGN KEY (" +
                        RECIPE_INGREDIENT_RECIPE_ID +
                        ") REFERENCES " +
                        TABLE_RECIPES +
                        "(" +
                        RECIPE_ID +
                        ")" +

                        ")";

        db.execSQL(sql);
    }

    // ============================================================
    // CREATE USERS TABLE
    // ============================================================

    private void createUsersTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE " + TABLE_USERS + " (" +

                        USER_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        USER_NAME +
                        " TEXT NOT NULL, " +

                        USER_EMAIL +
                        " TEXT NOT NULL UNIQUE, " +

                        USER_PASSWORD +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(sql);
    }

    // ============================================================
    // USER - REGISTER
    // ============================================================

    public boolean registerUser(
            String name,
            String email,
            String password
    ) {

        if (name == null ||
                email == null ||
                password == null) {

            return false;
        }

        name = name.trim();

        email =
                email.trim()
                        .toLowerCase(Locale.ROOT);

        if (name.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()) {

            return false;
        }

        if (emailExists(email)) {
            return false;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);

        long result;

        try {

            result =
                    db.insert(
                            TABLE_USERS,
                            null,
                            values
                    );

        } finally {

            db.close();
        }

        return result != -1;
    }

    // ============================================================
    // USER - EMAIL EXISTS
    // ============================================================

    public boolean emailExists(String email) {

        if (email == null ||
                email.trim().isEmpty()) {

            return false;
        }

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.query(
                            TABLE_USERS,
                            new String[]{
                                    USER_ID
                            },
                            USER_EMAIL + "=?",
                            new String[]{
                                    email.trim()
                                            .toLowerCase(
                                            Locale.ROOT
                                    )
                            },
                            null,
                            null,
                            null
                    );

            return cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }
    }

    // ============================================================
    // USER - LOGIN
    // ============================================================

    public boolean checkUserLogin(
            String email,
            String password
    ) {

        if (email == null ||
                password == null) {

            return false;
        }

        if (email.trim().isEmpty() ||
                password.isEmpty()) {

            return false;
        }

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.query(
                            TABLE_USERS,

                            new String[]{
                                    USER_ID
                            },

                            USER_EMAIL +
                                    "=? AND " +
                                    USER_PASSWORD +
                                    "=?",

                            new String[]{
                                    email.trim()
                                            .toLowerCase(
                                            Locale.ROOT
                                    ),
                                    password
                            },

                            null,
                            null,
                            null
                    );

            return cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }
    }

    // ============================================================
    // USER - GET USER BY EMAIL
    // ============================================================

    public Cursor getUserByEmail(String email) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_USERS,

                new String[]{
                        USER_ID,
                        USER_NAME,
                        USER_EMAIL,
                        USER_PASSWORD
                },

                USER_EMAIL + "=?",

                new String[]{
                        email.trim()
                                .toLowerCase(Locale.ROOT)
                },

                null,
                null,
                null
        );
    }

    // ============================================================
    // USER - UPDATE PASSWORD
    // ============================================================

    public boolean updateUserPassword(
            String email,
            String newPassword
    ) {

        if (email == null ||
                newPassword == null) {

            return false;
        }

        email =
                email.trim()
                        .toLowerCase(Locale.ROOT);

        if (email.isEmpty() ||
                newPassword.isEmpty()) {

            return false;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                USER_PASSWORD,
                newPassword
        );

        int result;

        try {

            result =
                    db.update(
                            TABLE_USERS,
                            values,
                            USER_EMAIL + "=?",
                            new String[]{
                                    email
                            }
                    );

        } finally {

            db.close();
        }

        return result > 0;
    }

    // ============================================================
    // USER - CHECK CURRENT PASSWORD
    // ============================================================

    public boolean checkCurrentPassword(
            String email,
            String currentPassword
    ) {

        return checkUserLogin(
                email,
                currentPassword
        );
    }

    // ============================================================
    // PANTRY - ADD
    // ============================================================

    public long addPantryItem(
            String ingredientName,
            double quantity,
            String unit,
            String expiryDate
    ) {

        if (ingredientName == null ||
                ingredientName.trim().isEmpty()) {

            return -1;
        }

        if (quantity <= 0) {
            return -1;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                PANTRY_NAME,
                normalizeIngredient(ingredientName)
        );

        values.put(
                PANTRY_QUANTITY,
                quantity
        );

        values.put(
                PANTRY_UNIT,
                normalizeUnit(unit)
        );

        values.put(
                PANTRY_EXPIRY,
                expiryDate
        );

        long id;

        try {

            id =
                    db.insert(
                            TABLE_PANTRY,
                            null,
                            values
                    );

        } finally {

            db.close();
        }

        return id;
    }

    // ============================================================
    // PANTRY - GET ALL
    // ============================================================

    public Cursor getAllPantryItems() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                PANTRY_NAME + " ASC"
        );
    }

    // ============================================================
    // PANTRY - GET ONE
    // ============================================================

    public Cursor getPantryItem(int id) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );
    }

    // ============================================================
    // PANTRY - UPDATE
    // ============================================================

    public int updatePantryItem(
            int id,
            String ingredientName,
            double quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                PANTRY_NAME,
                normalizeIngredient(ingredientName)
        );

        values.put(
                PANTRY_QUANTITY,
                quantity
        );

        values.put(
                PANTRY_UNIT,
                normalizeUnit(unit)
        );

        values.put(
                PANTRY_EXPIRY,
                expiryDate
        );

        int result;

        try {

            result =
                    db.update(
                            TABLE_PANTRY,
                            values,
                            PANTRY_ID + "=?",
                            new String[]{
                                    String.valueOf(id)
                            }
                    );

        } finally {

            db.close();
        }

        return result;
    }

    // ============================================================
    // PANTRY - DELETE ONE
    // ============================================================

    public int deletePantryItem(int id) {

        SQLiteDatabase db =
                getWritableDatabase();

        int result;

        try {

            result =
                    db.delete(
                            TABLE_PANTRY,
                            PANTRY_ID + "=?",
                            new String[]{
                                    String.valueOf(id)
                            }
                    );

        } finally {

            db.close();
        }

        return result;
    }

    // ============================================================
    // PANTRY - CLEAR ALL
    // ============================================================

    public int clearPantry() {

        SQLiteDatabase db =
                getWritableDatabase();

        int result;

        try {

            result =
                    db.delete(
                            TABLE_PANTRY,
                            null,
                            null
                    );

        } finally {

            db.close();
        }

        return result;
    }

    // ============================================================
    // RECIPES - GET ALL
    // ============================================================

    public Cursor getAllRecipes() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " ASC"
        );
    }

    // ============================================================
    // RECIPE - GET ONE
    // ============================================================

    public Cursor getRecipe(int recipeId) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                RECIPE_ID + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }

    // ============================================================
    // RECIPE INGREDIENTS - GET
    // ============================================================

    public Cursor getRecipeIngredients(
            int recipeId
    ) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RECIPE_INGREDIENT_RECIPE_ID + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                RECIPE_INGREDIENT_ID + " ASC"
        );
    }

    // ============================================================
    // RECIPE - CHECK IF CAN MAKE
    // ============================================================

    public boolean canMakeRecipe(int recipeId) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor recipeCursor = null;

        try {

            recipeCursor =
                    db.query(
                            TABLE_RECIPE_INGREDIENTS,
                            null,
                            RECIPE_INGREDIENT_RECIPE_ID +
                                    "=?",
                            new String[]{
                                    String.valueOf(recipeId)
                            },
                            null,
                            null,
                            null
                    );

            if (!recipeCursor.moveToFirst()) {
                return false;
            }

            do {

                String requiredName =
                        recipeCursor.getString(
                                recipeCursor
                                        .getColumnIndexOrThrow(
                                                RECIPE_INGREDIENT_NAME
                                        )
                        );

                double requiredQuantity =
                        recipeCursor.getDouble(
                                recipeCursor
                                        .getColumnIndexOrThrow(
                                                RECIPE_REQUIRED_QUANTITY
                                        )
                        );

                String requiredUnit =
                        recipeCursor.getString(
                                recipeCursor
                                        .getColumnIndexOrThrow(
                                                RECIPE_INGREDIENT_UNIT
                                        )
                        );

                if (!hasEnoughIngredient(
                        db,
                        requiredName,
                        requiredQuantity,
                        requiredUnit
                )) {

                    return false;
                }

            } while (recipeCursor.moveToNext());

            return true;

        } finally {

            if (recipeCursor != null) {
                recipeCursor.close();
            }

            db.close();
        }
    }

    // ============================================================
    // CHECK PANTRY QUANTITY
    // ============================================================

    private boolean hasEnoughIngredient(
            SQLiteDatabase db,
            String requiredName,
            double requiredQuantity,
            String requiredUnit
    ) {

        Cursor cursor = null;

        double availableQuantity = 0;

        try {

            cursor =
                    db.query(
                            TABLE_PANTRY,
                            new String[]{
                                    PANTRY_NAME,
                                    PANTRY_QUANTITY,
                                    PANTRY_UNIT
                            },
                            null,
                            null,
                            null,
                            null,
                            null
                    );

            while (cursor.moveToNext()) {

                String pantryName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        PANTRY_NAME
                                )
                        );

                String pantryUnit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        PANTRY_UNIT
                                )
                        );

                double pantryQuantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        PANTRY_QUANTITY
                                )
                        );

                if (ingredientsMatch(
                        pantryName,
                        requiredName
                ) &&
                        unitsMatch(
                                pantryUnit,
                                requiredUnit
                        )) {

                    availableQuantity +=
                            pantryQuantity;
                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return availableQuantity >=
                requiredQuantity;
    }

    // ============================================================
    // GET SUGGESTED RECIPES
    // ============================================================

    public List<Integer> getSuggestedRecipeIds() {

        List<Integer> recipeIds =
                new ArrayList<>();

        Cursor cursor =
                getAllRecipes();

        try {

            while (cursor.moveToNext()) {

                int recipeId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_ID
                                )
                        );

                if (canMakeRecipe(recipeId)) {

                    recipeIds.add(recipeId);
                }
            }

        } finally {

            cursor.close();
        }

        return recipeIds;
    }

    // ============================================================
    // INGREDIENT NORMALIZATION
    // ============================================================

    public String normalizeIngredient(
            String ingredient
    ) {

        if (ingredient == null) {
            return "";
        }

        String value =
                ingredient.trim()
                        .toLowerCase(Locale.ROOT);

        value =
                value.replaceAll(
                        "\\s+",
                        " "
                );

        if (value.endsWith("ies")) {

            value =
                    value.substring(
                            0,
                            value.length() - 3
                    ) + "y";

        } else if (value.endsWith("oes")) {

            value =
                    value.substring(
                            0,
                            value.length() - 2
                    );

        } else if (
                value.endsWith("s") &&
                        !value.endsWith("ss")
        ) {

            value =
                    value.substring(
                            0,
                            value.length() - 1
                    );
        }

        switch (value) {

            case "eggs":
            case "egg":
                return "egg";

            case "tomatoes":
            case "tomato":
                return "tomato";

            case "potatoes":
            case "potato":
                return "potato";

            case "onions":
            case "onion":
                return "onion";

            case "carrots":
            case "carrot":
                return "carrot";

            case "apples":
            case "apple":
                return "apple";

            case "bananas":
            case "banana":
                return "banana";

            case "oranges":
            case "orange":
                return "orange";

            case "strawberries":
            case "strawberry":
                return "strawberry";

            case "grapes":
            case "grape":
                return "grape";

            case "pepper":
            case "bell pepper":
            case "capsicum":
                return "pepper";

            case "chicken":
            case "chicken breast":
            case "cooked chicken":
                return "chicken";

            case "beef":
            case "ground beef":
            case "minced beef":
                return "beef";

            case "cheese":
            case "cheddar cheese":
            case "mozzarella cheese":
                return "cheese";

            case "milk":
                return "milk";

            case "bread":
                return "bread";

            case "rice":
            case "cooked rice":
                return "rice";

            case "pasta":
                return "pasta";

            case "macaroni":
                return "macaroni";

            case "flour":
                return "flour";

            case "sugar":
                return "sugar";

            case "butter":
                return "butter";

            case "garlic":
                return "garlic";

            case "peas":
            case "pea":
                return "pea";

            case "lettuce":
                return "lettuce";

            case "cucumber":
            case "cucumbers":
                return "cucumber";

            case "mayonnaise":
                return "mayonnaise";

            case "soy sauce":
                return "soy sauce";

            case "olive oil":
                return "olive oil";

            case "cooking oil":
            case "vegetable oil":
                return "cooking oil";

            case "salt":
                return "salt";

            case "black pepper":
                return "black pepper";

            case "cinnamon":
                return "cinnamon";

            case "baking powder":
                return "baking powder";

            case "parsley":
                return "parsley";

            case "basil":
                return "basil";

            case "honey":
                return "honey";

            case "pizza dough":
                return "pizza dough";

            case "taco shell":
            case "taco shells":
                return "taco shell";

            case "burger bun":
            case "burger buns":
                return "burger bun";

            case "tortilla":
            case "tortilla wrap":
            case "tortilla wraps":
                return "tortilla";

            case "tomato sauce":
                return "tomato sauce";

            default:
                return value;
        }
    }

    // ============================================================
    // INGREDIENT COMPARISON
    // ============================================================

    public boolean ingredientsMatch(
            String pantryIngredient,
            String recipeIngredient
    ) {

        String pantry =
                normalizeIngredient(
                        pantryIngredient
                );

        String recipe =
                normalizeIngredient(
                        recipeIngredient
                );

        return pantry.equals(recipe);
    }

    // ============================================================
    // UNIT NORMALIZATION
    // ============================================================

    public String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String value =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (value) {

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";

            case "cup":
            case "cups":
                return "cup";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tbsp";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";

            default:
                return value;
        }
    }

    // ============================================================
    // UNIT COMPARISON
    // ============================================================

    public boolean unitsMatch(
            String pantryUnit,
            String recipeUnit
    ) {

        String pantry =
                normalizeUnit(pantryUnit);

        String recipe =
                normalizeUnit(recipeUnit);

        return pantry.equals(recipe);
    }

    // ============================================================
    // ADD QUANTIFIED RECIPE
    // ============================================================

    private long addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String method,
            String[][] ingredients
    ) {

        ContentValues recipeValues =
                new ContentValues();

        recipeValues.put(
                RECIPE_NAME,
                recipeName
        );

        recipeValues.put(
                RECIPE_METHOD,
                method
        );

        long recipeId =
                db.insert(
                        TABLE_RECIPES,
                        null,
                        recipeValues
                );

        if (recipeId == -1) {
            return -1;
        }

        if (ingredients == null) {
            return recipeId;
        }

        for (String[] ingredient : ingredients) {

            if (ingredient == null ||
                    ingredient.length < 3) {

                continue;
            }

            try {

                String ingredientName =
                        normalizeIngredient(
                                ingredient[0]
                        );

                double quantity =
                        Double.parseDouble(
                                ingredient[1]
                        );

                String unit =
                        normalizeUnit(
                                ingredient[2]
                        );

                ContentValues values =
                        new ContentValues();

                values.put(
                        RECIPE_INGREDIENT_RECIPE_ID,
                        recipeId
                );

                values.put(
                        RECIPE_INGREDIENT_NAME,
                        ingredientName
                );

                values.put(
                        RECIPE_REQUIRED_QUANTITY,
                        quantity
                );

                values.put(
                        RECIPE_INGREDIENT_UNIT,
                        unit
                );

                db.insert(
                        TABLE_RECIPE_INGREDIENTS,
                        null,
                        values
                );

            } catch (NumberFormatException e) {
                // Ignore invalid quantity.
            }
        }

        return recipeId;
    }

    // ============================================================
    // SEED RECIPES
    // ============================================================

    private void seedRecipes(SQLiteDatabase db) {

        addRecipe(
                db,
                "French Toast",
                "Beat the eggs with milk, sugar and cinnamon. " +
                        "Dip the bread into the mixture. " +
                        "Fry in butter until golden on both sides.",
                new String[][]{
                        {"bread", "2", "piece"},
                        {"egg", "2", "piece"},
                        {"milk", "100", "ml"},
                        {"sugar", "10", "g"},
                        {"cinnamon", "2", "g"},
                        {"butter", "10", "g"}
                }
        );

        addRecipe(
                db,
                "Pancakes",
                "Mix flour, sugar and baking powder. " +
                        "Add milk and eggs. " +
                        "Cook portions in a buttered pan.",
                new String[][]{
                        {"flour", "200", "g"},
                        {"milk", "250", "ml"},
                        {"egg", "2", "piece"},
                        {"sugar", "30", "g"},
                        {"baking powder", "5", "g"},
                        {"butter", "20", "g"}
                }
        );

        addRecipe(
                db,
                "Omelette",
                "Beat eggs with salt and pepper. " +
                        "Cook in butter. Add onion, tomato " +
                        "and cheese. Fold and serve.",
                new String[][]{
                        {"egg", "3", "piece"},
                        {"onion", "50", "g"},
                        {"tomato", "1", "piece"},
                        {"cheese", "30", "g"},
                        {"butter", "10", "g"},
                        {"salt", "2", "g"},
                        {"black pepper", "1", "g"}
                }
        );

        addRecipe(
                db,
                "Grilled Cheese Sandwich",
                "Butter the bread. Add cheese between the slices. " +
                        "Cook until golden and the cheese melts.",
                new String[][]{
                        {"bread", "2", "piece"},
                        {"cheese", "50", "g"},
                        {"butter", "10", "g"}
                }
        );

        addRecipe(
                db,
                "Chicken Sandwich",
                "Spread mayonnaise on the bread. " +
                        "Add chicken, lettuce and tomato.",
                new String[][]{
                        {"bread", "2", "piece"},
                        {"chicken", "100", "g"},
                        {"lettuce", "30", "g"},
                        {"tomato", "1", "piece"},
                        {"mayonnaise", "20", "g"}
                }
        );

        addRecipe(
                db,
                "Chicken Wrap",
                "Place chicken, lettuce, tomato and cheese " +
                        "on the tortilla. Add mayonnaise and roll.",
                new String[][]{
                        {"tortilla", "2", "piece"},
                        {"chicken", "150", "g"},
                        {"lettuce", "30", "g"},
                        {"tomato", "1", "piece"},
                        {"cheese", "30", "g"},
                        {"mayonnaise", "20", "g"}
                }
        );

        addRecipe(
                db,
                "Chicken Curry",
                "Cook onion and garlic in oil. Add chicken " +
                        "and tomatoes. Simmer until cooked.",
                new String[][]{
                        {"chicken", "250", "g"},
                        {"onion", "1", "piece"},
                        {"garlic", "10", "g"},
                        {"tomato", "2", "piece"},
                        {"cooking oil", "20", "ml"},
                        {"salt", "3", "g"}
                }
        );

        addRecipe(
                db,
                "Chicken Stir-Fry",
                "Stir-fry chicken in oil. Add vegetables " +
                        "and garlic. Add soy sauce and cook.",
                new String[][]{
                        {"chicken", "250", "g"},
                        {"carrot", "1", "piece"},
                        {"pepper", "1", "piece"},
                        {"onion", "1", "piece"},
                        {"garlic", "10", "g"},
                        {"soy sauce", "30", "ml"},
                        {"cooking oil", "20", "ml"}
                }
        );

        addRecipe(
                db,
                "Fried Rice",
                "Cook onion and vegetables. Add eggs and scramble. " +
                        "Add rice and soy sauce and stir-fry.",
                new String[][]{
                        {"rice", "300", "g"},
                        {"egg", "2", "piece"},
                        {"carrot", "1", "piece"},
                        {"pea", "50", "g"},
                        {"onion", "1", "piece"},
                        {"soy sauce", "30", "ml"},
                        {"cooking oil", "20", "ml"}
                }
        );

        addRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Fry onion and garlic. " +
                        "Add tomato sauce and combine with pasta.",
                new String[][]{
                        {"pasta", "200", "g"},
                        {"tomato sauce", "200", "ml"},
                        {"onion", "1", "piece"},
                        {"garlic", "10", "g"},
                        {"olive oil", "20", "ml"},
                        {"salt", "2", "g"}
                }
        );

        addRecipe(
                db,
                "Macaroni and Cheese",
                "Cook macaroni. Make a sauce with butter, flour " +
                        "and milk. Add cheese and mix with macaroni.",
                new String[][]{
                        {"macaroni", "200", "g"},
                        {"cheese", "100", "g"},
                        {"milk", "250", "ml"},
                        {"butter", "30", "g"},
                        {"flour", "20", "g"},
                        {"salt", "2", "g"}
                }
        );

        addRecipe(
                db,
                "Spaghetti Bolognese",
                "Cook spaghetti. Brown beef with onion and garlic. " +
                        "Add tomato sauce and simmer.",
                new String[][]{
                        {"pasta", "200", "g"},
                        {"beef", "250", "g"},
                        {"onion", "1", "piece"},
                        {"garlic", "10", "g"},
                        {"tomato sauce", "200", "ml"},
                        {"salt", "2", "g"}
                }
        );

        addRecipe(
                db,
                "Chicken Salad",
                "Chop chicken and vegetables. Mix together " +
                        "with mayonnaise and seasoning.",
                new String[][]{
                        {"chicken", "150", "g"},
                        {"lettuce", "50", "g"},
                        {"tomato", "1", "piece"},
                        {"cucumber", "1", "piece"},
                        {"mayonnaise", "20", "g"},
                        {"salt", "2", "g"}
                }
        );

        addRecipe(
                db,
                "Vegetable Soup",
                "Cook onion. Add vegetables and water or stock. " +
                        "Simmer until vegetables are tender.",
                new String[][]{
                        {"carrot", "2", "piece"},
                        {"potato", "2", "piece"},
                        {"onion", "1", "piece"},
                        {"tomato", "2", "piece"},
                        {"pea", "100", "g"},
                        {"salt", "3", "g"}
                }
        );

        addRecipe(
                db,
                "Mashed Potatoes",
                "Boil potatoes until soft. Mash with butter and milk. " +
                        "Season with salt and pepper.",
                new String[][]{
                        {"potato", "500", "g"},
                        {"butter", "30", "g"},
                        {"milk", "100", "ml"},
                        {"salt", "3", "g"},
                        {"black pepper", "1", "g"}
                }
        );

        addRecipe(
                db,
                "Garlic Bread",
                "Mix butter with garlic and parsley. " +
                        "Spread on bread and bake until golden.",
                new String[][]{
                        {"bread", "4", "piece"},
                        {"butter", "40", "g"},
                        {"garlic", "10", "g"},
                        {"parsley", "5", "g"},
                        {"salt", "1", "g"}
                }
        );

        addRecipe(
                db,
                "Beef Burger",
                "Season beef and form a patty. Cook until done. " +
                        "Serve in a bun with vegetables and cheese.",
                new String[][]{
                        {"beef", "150", "g"},
                        {"burger bun", "1", "piece"},
                        {"lettuce", "20", "g"},
                        {"tomato", "1", "piece"},
                        {"onion", "30", "g"},
                        {"cheese", "30", "g"}
                }
        );

        addRecipe(
                db,
                "Fruit Salad",
                "Wash and chop the fruit. Mix together " +
                        "and drizzle with honey.",
                new String[][]{
                        {"apple", "1", "piece"},
                        {"banana", "1", "piece"},
                        {"orange", "1", "piece"},
                        {"grape", "100", "g"},
                        {"strawberry", "100", "g"},
                        {"honey", "15", "ml"}
                }
        );

        addRecipe(
                db,
                "Pizza Margherita",
                "Spread tomato sauce over pizza dough. " +
                        "Add mozzarella and bake. Finish with basil.",
                new String[][]{
                        {"pizza dough", "1", "piece"},
                        {"tomato sauce", "100", "ml"},
                        {"cheese", "125", "g"},
                        {"basil", "10", "g"},
                        {"olive oil", "10", "ml"}
                }
        );

        addRecipe(
                db,
                "Beef Tacos",
                "Cook seasoned beef. Fill taco shells with beef, " +
                        "lettuce, tomato, onion and cheese.",
                new String[][]{
                        {"taco shell", "3", "piece"},
                        {"beef", "200", "g"},
                        {"lettuce", "30", "g"},
                        {"tomato", "1", "piece"},
                        {"onion", "50", "g"},
                        {"cheese", "40", "g"}
                }
        );
    }
}