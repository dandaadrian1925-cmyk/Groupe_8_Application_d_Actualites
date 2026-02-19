package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ChipGroup chipGroupCategories;
    private MaterialButton btnSavePreferences, btnResetPreferences;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setupActions();
        loadExistingPreferences();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackPreferences);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        btnSavePreferences = findViewById(R.id.btnSavePreferences);
        btnResetPreferences = findViewById(R.id.btnResetPreferences);
    }

    private void setupActions() {

        btnBack.setOnClickListener(v -> finish());

        btnSavePreferences.setOnClickListener(v -> updatePreferences());

        btnResetPreferences.setOnClickListener(v -> clearSelections());
    }

    // 🔄 Charger préférences
    private void loadExistingPreferences() {

        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) return;

                    List<String> savedPreferences =
                            (List<String>) snapshot.get("preferences");

                    if (savedPreferences == null) return;

                    for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {

                        Chip chip = (Chip) chipGroupCategories.getChildAt(i);

                        String firestoreValue =
                                convertDisplayToFirestore(
                                        chip.getText().toString());

                        if (savedPreferences.contains(firestoreValue)) {
                            chip.setChecked(true);
                        }
                    }
                });
    }

    // 💾 Sauvegarde
    private void updatePreferences() {

        if (isLoading || currentUser == null) return;

        List<String> selected = new ArrayList<>();

        for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {

            Chip chip = (Chip) chipGroupCategories.getChildAt(i);

            if (chip.isChecked()) {

                selected.add(
                        convertDisplayToFirestore(
                                chip.getText().toString()
                        )
                );
            }
        }

        if (selected.isEmpty()) {
            Toast.makeText(this,
                    "Sélectionnez au moins une catégorie",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        btnSavePreferences.setEnabled(false);
        btnSavePreferences.setText("Mise à jour...");

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("preferences", selected);

        db.collection("users")
                .document(currentUser.getUid())
                .update(updateData)
                .addOnSuccessListener(unused -> {

                    isLoading = false;
                    btnSavePreferences.setEnabled(true);
                    btnSavePreferences.setText("Enregistrer");

                    Toast.makeText(this,
                            "Préférences mises à jour",
                            Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    isLoading = false;
                    btnSavePreferences.setEnabled(true);
                    btnSavePreferences.setText("Enregistrer");

                    Toast.makeText(this,
                            "Erreur : " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void clearSelections() {

        for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {

            Chip chip = (Chip) chipGroupCategories.getChildAt(i);
            chip.setChecked(false);
        }
    }

    // 🔥 Conversion UI -> Firestore (stable)
    private String convertDisplayToFirestore(String text) {

        switch (text.toLowerCase()) {

            case "sport":
                return "sports";

            case "technologie":
                return "technology";

            case "politique":
                return "general";

            case "économie":
                return "business";

            case "santé":
                return "health";

            case "culture":
                return "entertainment";

            default:
                return text.toLowerCase();
        }
    }
}
