package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvFullName, tvSubInfo;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 🔹 Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔹 Liaison des vues
        imgProfile = findViewById(R.id.imgProfile);
        tvFullName = findViewById(R.id.tvFullName);
        tvSubInfo = findViewById(R.id.tvSubInfo);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setupBottomNavigation();

        // 🔹 Charger les infos utilisateur
        loadUserData();
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

                return true; // déjà ici
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

                        String sexe = documentSnapshot.getString("sexe");
                        String prenom = documentSnapshot.getString("prenom");
                        String nom = documentSnapshot.getString("nom");

                        String email = mAuth.getCurrentUser().getEmail();

                        if (prenom == null) prenom = "";
                        if (nom == null) nom = "";
                        if (sexe == null) sexe = "";

                        String prefix = sexe.equals("Masculin") ? "Mr. " : "Mrs. ";

                        tvFullName.setText(prefix + prenom + " " + nom);
                        tvSubInfo.setText(email != null ? email : "");
                    }
                });
    }
}
