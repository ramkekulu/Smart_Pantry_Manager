package com.kekulu.smart_pantry_manager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private EditText editConfirmPassword;

    private Button btnSavePassword;
    private Button btnCancelPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnCancelPassword = findViewById(R.id.btnCancelPassword);

        btnSavePassword.setOnClickListener(v -> changePassword());

        btnCancelPassword.setOnClickListener(v -> finish());
    }

    private void changePassword() {

        String currentPassword =
                editCurrentPassword.getText().toString().trim();

        String newPassword =
                editNewPassword.getText().toString().trim();

        String confirmPassword =
                editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            editCurrentPassword.setError("Enter your current password");
            editCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editNewPassword.setError("Enter a new password");
            editNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editNewPassword.setError(
                    "Password must be at least 6 characters"
            );
            editNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editConfirmPassword.setError(
                    "Confirm your new password"
            );
            editConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError(
                    "Passwords do not match"
            );
            editConfirmPassword.requestFocus();
            return;
        }

        SharedPreferences userPrefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        String savedPassword =
                userPrefs.getString("user_password", "");

        if (savedPassword.isEmpty()) {
            Toast.makeText(
                    this,
                    "No saved password was found.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (!currentPassword.equals(savedPassword)) {
            editCurrentPassword.setError(
                    "Current password is incorrect"
            );
            editCurrentPassword.requestFocus();
            return;
        }

        userPrefs.edit()
                .putString("user_password", newPassword)
                .apply();

        Toast.makeText(
                this,
                "Password changed successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}

