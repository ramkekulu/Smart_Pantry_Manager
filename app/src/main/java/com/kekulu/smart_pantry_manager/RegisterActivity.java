package com.kekulu.smart_pantry_manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;

    private Button btnRegister;
    private TextView txtLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword =
                findViewById(R.id.editConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        // ========================================================
        // REGISTER BUTTON
        // ========================================================

        btnRegister.setOnClickListener(view -> {

            String name =
                    editName.getText().toString().trim();

            String email =
                    editEmail.getText().toString().trim();

            String password =
                    editPassword.getText().toString();

            String confirmPassword =
                    editConfirmPassword
                            .getText()
                            .toString();

            // ----------------------------------------------------
            // NAME
            // ----------------------------------------------------

            if (name.isEmpty()) {

                editName.setError("Enter your name");
                editName.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // EMAIL
            // ----------------------------------------------------

            if (email.isEmpty()) {

                editEmail.setError("Enter your email");
                editEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()) {

                editEmail.setError(
                        "Enter a valid email address"
                );

                editEmail.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // PASSWORD
            // ----------------------------------------------------

            if (password.isEmpty()) {

                editPassword.setError(
                        "Enter a password"
                );

                editPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {

                editPassword.setError(
                        "Password must be at least 6 characters"
                );

                editPassword.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // CONFIRM PASSWORD
            // ----------------------------------------------------

            if (!password.equals(confirmPassword)) {

                editConfirmPassword.setError(
                        "Passwords do not match"
                );

                editConfirmPassword.requestFocus();
                return;
            }

            // ----------------------------------------------------
            // CHECK EXISTING EMAIL
            // ----------------------------------------------------

            if (databaseHelper.emailExists(email)) {

                Toast.makeText(
                        RegisterActivity.this,
                        "An account with this email already exists",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ----------------------------------------------------
            // CREATE ACCOUNT
            // ----------------------------------------------------

            boolean registered =
                    databaseHelper.registerUser(
                            name,
                            email,
                            password
                    );

            if (registered) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Account created successfully",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);

                finish();

            } else {

                Toast.makeText(
                        RegisterActivity.this,
                        "Registration failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // ========================================================
        // BACK TO LOGIN
        // ========================================================

        txtLogin.setOnClickListener(view -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }
}

