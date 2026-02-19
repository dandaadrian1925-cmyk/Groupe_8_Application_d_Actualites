package com.news.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvFullName, tvSubInfo, tvPreferences;
    private BottomNavigationView bottomNavigationView;
    private SwitchMaterial switchNotifications;

    private MaterialCardView cardInvite;
    private MaterialCardView cardSettings;
    private MaterialCardView cardLogout; // ✅ AJOUT

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "news_app_prefs";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        imgProfile = findViewById(R.id.imgProfile);
        tvFullName = findViewById(R.id.tvFullName);
        tvSubInfo = findViewById(R.id.tvSubInfo);
        tvPreferences = findViewById(R.id.tvPreferences);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        switchNotifications = findViewById(R.id.switchNotifications);

        cardInvite = findViewById(R.id.cardInvite);
        cardSettings = findViewById(R.id.cardSettings);
        cardLogout = findViewById(R.id.cardLogout); // ✅ AJOUT

        setupBottomNavigation();
        setupNotificationsSwitch();
        setupCardActions();

        loadUserData();
    }

    private void setupCardActions() {

        if (cardInvite != null) {
            cardInvite.setOnClickListener(v ->
                    startActivity(new Intent(this, InvitActivity.class))
            );
        }

        if (cardSettings != null) {
            cardSettings.setOnClickListener(v ->
                    startActivity(new Intent(this, SettingsActivity.class))
            );
        }

        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> showLogoutConfirmation());
        }
    }

    // ✅ Confirmation Dialog
    private void showLogoutConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> {

                    mAuth.signOut();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void setupNotificationsSwitch() {

        boolean isEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
        switchNotifications.setChecked(isEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_NOTIFICATIONS, isChecked);
            editor.apply();
        });
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_favorites) {

                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_category) {

                startActivity(new Intent(this, CategoryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_profile) {

                return true;
            }

            return false;
        });
    }

    private void loadUserData() {

        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");

                        if (firstName == null) firstName = "";
                        if (lastName == null) lastName = "";
                        if (email == null) email = "";

                        tvFullName.setText((firstName + " " + lastName).trim());
                        tvSubInfo.setText(email);

                        List<String> preferences =
                                (List<String>) documentSnapshot.get("preferences");

                        if (preferences != null && !preferences.isEmpty()) {

                            StringBuilder builder = new StringBuilder();

                            for (String pref : preferences) {
                                builder.append(capitalize(pref)).append(" • ");
                            }

                            String result = builder.toString().trim();

                            if (result.endsWith("•")) {
                                result = result.substring(0, result.length() - 1).trim();
                            }

                            tvPreferences.setText(result);

                        } else {
                            tvPreferences.setText("Aucune préférence sélectionnée");
                        }
                    }
                });
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}
