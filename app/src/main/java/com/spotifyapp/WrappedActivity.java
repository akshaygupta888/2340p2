package com.spotifyapp;

import android.os.Bundle;
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
    private String uid;
    private SpotifyAPI spotifyAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWrappedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        getUserToken();
    }

    private void getUserToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
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
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get spotify credentials", Toast.LENGTH_SHORT).show();
                    });
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
