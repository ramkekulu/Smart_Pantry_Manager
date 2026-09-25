package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;

    private Button btnLogin;
    private TextView txtRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // ========================================================
        // LOGIN BUTTON
        // ========================================================

        btnLogin.setOnClickListener(view -> {

            String email =
                    editEmail.getText()
                            .toString()
                            .trim()
                            .toLowerCase();

            String password =
                    editPassword.getText()
                            .toString();

            // ----------------------------------------------------
            // EMAIL VALIDATION
            // ----------------------------------------------------

            if (email.isEmpty()) {

                editEmail.setError("Enter your email");
                editEmail.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // PASSWORD VALIDATION
            // ----------------------------------------------------

            if (password.isEmpty()) {

                editPassword.setError("Enter your password");
                editPassword.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // CHECK LOGIN
            // ----------------------------------------------------

            boolean loginSuccessful =
                    databaseHelper.checkUserLogin(
                            email,
                            password
                    );

            if (loginSuccessful) {

                // =================================================
                // GET THE ACTUAL USER DETAILS FROM DATABASE
                // =================================================

                Cursor cursor =
                        databaseHelper.getUserByEmail(email);

                if (cursor != null) {

                    try {

                        if (cursor.moveToFirst()) {

                            String userName =
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    DatabaseHelper.USER_NAME
                                            )
                                    );

                            String userEmail =
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    DatabaseHelper.USER_EMAIL
                                            )
                                    );

                            // =====================================
                            // SAVE CURRENT USER SESSION
                            // =====================================

                            SharedPreferences userPrefs =
                                    getSharedPreferences(
                                            "UserSession",
                                            MODE_PRIVATE
                                    );

                            userPrefs.edit()
                                    .putString(
                                            "user_name",
                                            userName
                                    )
                                    .putString(
                                            "user_email",
                                            userEmail
                                    )
                                    .apply();
                        }

                    } finally {

                        cursor.close();
                    }
                }

                // =================================================
                // LOGIN SUCCESSFUL
                // =================================================

                Toast.makeText(
                        LoginActivity.this,
                        "Login successful",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent =
                        new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                startActivity(intent);

                finish();

            } else {

                Toast.makeText(
                        LoginActivity.this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // ========================================================
        // REGISTER
        // ========================================================

        txtRegister.setOnClickListener(view -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);
        });
    }
}