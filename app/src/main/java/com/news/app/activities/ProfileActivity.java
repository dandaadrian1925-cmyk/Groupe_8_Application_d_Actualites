package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvFullName, tvSubInfo;

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

        // 🔹 Charger les infos utilisateur
        loadUserData();
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

                        // 🔹 Email depuis FirebaseAuth
                        String email = mAuth.getCurrentUser().getEmail();

                        // 🔹 Gestion null pour éviter crash
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
