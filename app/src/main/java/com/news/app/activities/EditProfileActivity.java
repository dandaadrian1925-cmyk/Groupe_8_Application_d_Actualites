package com.news.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.news.app.R;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private Button btnChangePhoto, btnSave, btnCancel;
    private EditText etName, etEmail;

    private Uri selectedImageUri;

    // Launcher pour choisir une image depuis la galerie
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivProfile);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        loadUserData();
        setupActions();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivEditProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
    }

    private void loadUserData() {
        // MOCK DATA
        etName.setText("Dan Di");
        etEmail.setText("dandi@email.com");

        Glide.with(this)
                .load("https://i.pravatar.cc/300")
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProfile);
    }

    private void setupActions() {

        btnChangePhoto.setOnClickListener(v -> {
            // Ouvre la galerie pour choisir une image
            pickImageLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            // TODO: sauvegarder les modifications (SharedPreferences ou API)
            finish(); // retourne à ProfileActivity
        });

        btnCancel.setOnClickListener(v -> {
            finish(); // annule et retourne à ProfileActivity
        });
    }
}