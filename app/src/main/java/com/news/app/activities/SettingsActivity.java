package com.news.app.activities;

import com.news.app.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;

    private MaterialCardView cardEditProfile;
    private MaterialCardView cardChangePassword;
    private MaterialCardView cardDeleteAccount;
    private MaterialCardView cardEditPreferences;
    private MaterialCardView cardResetPreferences;
    private MaterialCardView cardPrivacy;
    private MaterialCardView cardTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupListeners();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardDeleteAccount = findViewById(R.id.cardDeleteAccount);
        cardEditPreferences = findViewById(R.id.cardEditPreferences);
        cardResetPreferences = findViewById(R.id.cardResetPreferences);
        cardPrivacy = findViewById(R.id.cardPrivacy);
        cardTerms = findViewById(R.id.cardTerms);
    }

    private void setupListeners() {

        // Retour
        btnBack.setOnClickListener(v -> finish());

        // Modifier profil
        cardEditProfile.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class))
        );

        // Changer mot de passe
        cardChangePassword.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class))
        );

        // Supprimer compte
        cardDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        // Modifier préférences
        cardEditPreferences.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, PreferenceActivity.class))
        );

        // Réinitialiser préférences

        cardResetPreferences.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ResetPreferenceActivity.class))
        );

        // Politique de confidentialité
        cardPrivacy.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ConfidentActivity.class))
        );

        // Conditions d'utilisation
        cardTerms.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, ConditionsActivity.class))
        );
    }

    // =============================
    // DIALOG SUPPRESSION COMPTE
    // =============================

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer mon compte")
                .setMessage("Êtes-vous sûr de vouloir supprimer définitivement votre compte ? Cette action est irréversible.")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    // Ici tu peux ajouter la suppression Firebase si besoin

                    // Exemple : retour vers écran login
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // =============================
    // DIALOG RESET PREFERENCES
    // =============================

    private void showResetPreferencesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Réinitialiser mes préférences")
                .setMessage("Voulez-vous vraiment réinitialiser toutes vos préférences ?")
                .setPositiveButton("Oui", (dialog, which) -> {

                    // Suppression des SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
