package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvNotifications, tvDarkMode;
    private Switch switchNotifications, switchDarkMode;
    private Button btnChangePassword, btnLogoutSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupActions();
        loadSettings();
    }

    private void initViews() {
        tvNotifications = findViewById(R.id.tvNotifications);
        tvDarkMode = findViewById(R.id.tvDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogoutSettings = findViewById(R.id.btnLogoutSettings);
    }

    private void loadSettings() {
        // MOCK : charger les paramètres existants
        switchNotifications.setChecked(true); // notifications activées par défaut
        switchDarkMode.setChecked(false);     // mode sombre désactivé par défaut
    }

    private void setupActions() {

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: sauvegarder préférence notification
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: appliquer le mode sombre
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: ouvrir EditPasswordActivity ou dialogue de changement mot de passe
        });

        btnLogoutSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}