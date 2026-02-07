package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.news.app.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private TextView tvName, tvEmail, tvFavoritesCount;
    private Button btnEditProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupActions();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvFavoritesCount = findViewById(R.id.tvFavoritesCount);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        // MOCK DATA (à remplacer plus tard par SharedPreferences ou API)
        tvName.setText("Dan Di");
        tvEmail.setText("dandi@email.com");
        tvFavoritesCount.setText("Articles favoris : 5");

        // Image profil avec Glide
        Glide.with(this)
                .load("https://i.pravatar.cc/300") // Exemple avatar aléatoire
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProfile);
    }

    private void setupActions() {
        btnEditProfile.setOnClickListener(v -> {
            // TODO : ajouter modification profil
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}