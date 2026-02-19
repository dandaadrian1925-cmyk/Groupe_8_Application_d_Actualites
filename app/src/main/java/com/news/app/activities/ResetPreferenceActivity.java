package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

import java.util.ArrayList;

public class ResetPreferenceActivity extends AppCompatActivity {

    private ImageView btnBackReset;
    private MaterialButton btnConfirmReset, btnCancelReset;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_preference);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnBackReset = findViewById(R.id.btnBackReset);
        btnConfirmReset = findViewById(R.id.btnConfirmReset);
        btnCancelReset = findViewById(R.id.btnCancelReset);

        btnBackReset.setOnClickListener(v -> finish());
        btnCancelReset.setOnClickListener(v -> finish());
        btnConfirmReset.setOnClickListener(v -> resetPreferences());
    }

    private void resetPreferences() {

        if (isLoading || currentUser == null) return;

        isLoading = true;
        btnConfirmReset.setEnabled(false);
        btnConfirmReset.setText("Réinitialisation...");

        db.collection("users")
                .document(currentUser.getUid())
                .update("preferences", new ArrayList<String>())
                .addOnSuccessListener(unused -> {

                    isLoading = false;
                    btnConfirmReset.setEnabled(true);
                    btnConfirmReset.setText("Réinitialiser");

                    Toast.makeText(this,
                            "Préférences réinitialisées",
                            Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    isLoading = false;
                    btnConfirmReset.setEnabled(true);
                    btnConfirmReset.setText("Réinitialiser");

                    Toast.makeText(this,
                            "Erreur : " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
