package com.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotifyapp.ui.main.SectionsPagerAdapter;
import com.spotifyapp.databinding.ActivityWrappedBinding;

public class WrappedActivity extends AppCompatActivity implements SpotifyAPI.SpotifyDataListener {

    private ActivityWrappedBinding binding;
    private FirebaseFirestore db;
    private String authToken;
    private SpotifyAPI spotifyAPI;
    private Button shortButton, mediumButton, longButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWrappedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shortButton = findViewById(R.id.short_button);
        mediumButton = findViewById(R.id.medium_button);
        longButton = findViewById(R.id.long_button);
        settingsButton = findViewById(R.id.settings_button);

        shortButton.setOnClickListener(v -> {
            spotifyAPI.setTimeRange("short_term");
            shortButton.setEnabled(false);
            mediumButton.setEnabled(true);
            longButton.setEnabled(true);
            setButtonAlpha(shortButton, false);
            setButtonAlpha(mediumButton, true);
            setButtonAlpha(longButton, true);
        });

        mediumButton.setOnClickListener(v -> {
            spotifyAPI.setTimeRange("medium_term");
            shortButton.setEnabled(true);
            mediumButton.setEnabled(false);
            longButton.setEnabled(true);
            setButtonAlpha(shortButton, true);
            setButtonAlpha(mediumButton, false);
            setButtonAlpha(longButton, true);
        });

        longButton.setOnClickListener(v -> {
            spotifyAPI.setTimeRange("long_term");
            shortButton.setEnabled(true);
            mediumButton.setEnabled(true);
            longButton.setEnabled(false);
            setButtonAlpha(shortButton, true);
            setButtonAlpha(mediumButton, true);
            setButtonAlpha(longButton, false);
        });

        longButton.setEnabled(false);

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(WrappedActivity.this, UserSettingsActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        getUserToken();
    }

    private void setButtonAlpha(Button button, boolean enabled) {
        if (enabled) {
            button.setAlpha(1.0f);
        } else {
            button.setAlpha(0.5f);
        }
    }

    private void getUserToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            authToken = documentSnapshot.getString("authToken");
                            spotifyAPI = new SpotifyAPI(authToken, this, this);
                        } else {
                            Toast.makeText(this, "Failed to get spotify credentials", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to get spotify credentials", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onDataLoaded() {
        setupViewPager();
        Toast.makeText(this, "Successfully got spotify credentials", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataLoadError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), spotifyAPI);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }
}
