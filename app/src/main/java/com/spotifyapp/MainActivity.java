package com.spotifyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifyapp.ui.login.LoginFragment;
import com.spotifyapp.ui.login.SignUpFragment;

public class MainActivity extends AppCompatActivity implements SignUpFragment.OnButtonClickListener {
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "spotifyapp://auth";
    private static final String CLIENT_ID = "0acfab54a1484caea991efa0dda5b0e9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton = findViewById(R.id.login_button);
        Button signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_authentication, new LoginFragment())
                        .commit();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_authentication, new SignUpFragment())
                        .commit();
            }
        });
    }

    @Override
    public void onButtonClicked() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthorizationResponse response = AuthorizationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    String authToken = response.getAccessToken();
                    storeAuthTokenInFirebase(authToken);
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show();
                    startNewActivity();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    String errorMessage = response.getError();
                    Toast.makeText(this, "Authentication error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Toast.makeText(this, "Authentication canceled", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void storeAuthTokenInFirebase(String authToken) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        Log.d("LOGIN", "here!");

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .update("authToken", authToken)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Auth token stored successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to store auth token", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(MainActivity.this, WrappedActivity.class);
        startActivity(intent);
        finish();
    }
}