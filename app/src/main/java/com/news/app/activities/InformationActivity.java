package com.news.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;
import com.news.app.model.User;

import java.text.SimpleDateFormat;
import java.util.*;

public class InformationActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName;
    private TextView tvDateOfBirth;
    private Button btnSubmit;
    private LinearLayout categoriesLayout;

    private FirebaseFirestore db;

    // Données reçues de RegisterActivity
    private String email;
    private String password;

    // Liste des catégories
    private final String[] categories = new String[]{
            "Politique", "Économie", "Sports", "Technologie",
            "Santé", "Culture", "Science", "Divertissement",
            "Education", "Autres"
    };

    private final Set<String> selectedCategories = new HashSet<>();
    private String dateOfBirth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // 🔹 Firestore
        db = FirebaseFirestore.getInstance();

        // 🔹 Récupération des vues
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        btnSubmit = findViewById(R.id.btnSubmit);
        categoriesLayout = findViewById(R.id.categoriesLayout);

        // 🔹 Récupération email/password depuis RegisterActivity
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        // 🔹 Date picker
        tvDateOfBirth.setOnClickListener(v -> showDatePicker());

        // 🔹 Ajouter les catégories à cocher
        addCategoryCheckboxes();

        // 🔹 Bouton Valider
        btnSubmit.setOnClickListener(v -> submitInformation());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = 2000;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    m += 1; // Mois commence à 0
                    dateOfBirth = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m, d);
                    tvDateOfBirth.setText(dateOfBirth);
                }, year, month, day);
        dpd.show();
    }

    private void addCategoryCheckboxes() {
        for (String cat : categories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(cat);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategories.add(cat);
                } else {
                    selectedCategories.remove(cat);
                }
            });
            categoriesLayout.addView(checkBox);
        }
    }

    private void submitInformation() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner au moins une catégorie", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Créer un nouvel utilisateur
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setDateOfBirth(dateOfBirth);
        user.setPreferences(new ArrayList<>(selectedCategories));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        user.setCreatedAt(sdf.format(new Date()));
        user.setRole("user"); // par défaut
        user.setProfileImageUrl(""); // vide pour l'instant

        // 🔹 Enregistrer dans Firestore
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                    // 🔹 Redirection vers SplashActivity
                    Intent intent = new Intent(InformationActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Erreur lors de la création du compte : " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }
}