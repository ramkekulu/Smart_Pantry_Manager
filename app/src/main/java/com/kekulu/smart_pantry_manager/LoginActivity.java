package com.kekulu.smart_pantry_manager;

import android.content.Intent;
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
                    editEmail.getText().toString().trim();

            String password =
                    editPassword.getText().toString();

            if (email.isEmpty()) {

                editEmail.setError("Enter your email");
                editEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {

                editPassword.setError("Enter your password");
                editPassword.requestFocus();
                return;
            }

            boolean loginSuccessful =
                    databaseHelper.checkUserLogin(
                            email,
                            password
                    );

            if (loginSuccessful) {

                Toast.makeText(
                        LoginActivity.this,
                        "Login successful",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
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
        // REGISTER LINK
        // ========================================================

        txtRegister.setOnClickListener(view -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}
