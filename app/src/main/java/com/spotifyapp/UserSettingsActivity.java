package com.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserSettingsActivity extends AppCompatActivity {

    private EditText emailEditText;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        emailEditText = findViewById(R.id.email_edittext);
        Button resetEmailButton = findViewById(R.id.reset_email_button);
        Button resetPasswordButton = findViewById(R.id.reset_password_button);
        Button deleteAccountButton = findViewById(R.id.delete_account_button);
        Button backButton = findViewById(R.id.back_button);

        resetEmailButton.setOnClickListener(v -> resetEmail());

        resetPasswordButton.setOnClickListener(v -> resetPassword());

        deleteAccountButton.setOnClickListener(v -> deleteAccount());

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void resetEmail() {
        String newEmail = emailEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(newEmail)) {
            currentUser.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserSettingsActivity.this, "Email update link sent to your new email. Please verify it.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserSettingsActivity.this, "Failed to send email update link. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a new email", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword() {
        String emailAddress = Objects.requireNonNull(currentUser).getEmail();

        if (!TextUtils.isEmpty(emailAddress)) {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserSettingsActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserSettingsActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No email associated with this account", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAccount() {
        String uid = currentUser.getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.delete()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        startActivity(new Intent(UserSettingsActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(UserSettingsActivity.this, "Failed to delete user account", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(UserSettingsActivity.this, "Failed to delete user document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
