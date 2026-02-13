package com.news.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private String email;
    private String password;

    private final String[] categories = new String[]{
            "Politique", "Économie", "Sports", "Technologie",
            "Santé", "Culture", "Science", "Divertissement",
            "Education", "Autres"
    };

    private final Set<String> selectedCategories = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        db = FirebaseFirestore.getInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        btnSubmit = findViewById(R.id.btnSubmit);
        categoriesLayout = findViewById(R.id.categoriesLayout);

        // 🔹 Ajouter un ProgressBar en code
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        LinearLayout parentLayout = findViewById(R.id.categoriesLayout).getRootView().findViewById(R.id.categoriesLayout).getRootView() instanceof LinearLayout ? (LinearLayout)findViewById(R.id.categoriesLayout).getRootView() : null;
        if (parentLayout != null) parentLayout.addView(progressBar);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        tvDateOfBirth.setOnClickListener(v -> showDatePicker());

        addCategoryCheckboxes();

        btnSubmit.setOnClickListener(v -> submitInformation());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = 2000;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    m += 1;
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m, d);
                    tvDateOfBirth.setText(date);
                }, year, month, day);
        dpd.show();
    }

    private void addCategoryCheckboxes() {
        for (String cat : categories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(cat);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) selectedCategories.add(cat);
                else selectedCategories.remove(cat);
            });
            categoriesLayout.addView(checkBox);
        }
    }

    private void submitInformation() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dateOfBirth = tvDateOfBirth.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner au moins une catégorie", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Désactiver le bouton et afficher le ProgressBar
        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setDateOfBirth(dateOfBirth);
        user.setPreferences(new ArrayList<>(selectedCategories));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        user.setCreatedAt(sdf.format(new Date()));
        user.setRole("user");
        user.setProfileImageUrl("");

        db.collection("users")
                .add(user)
                .addOnSuccessListener(docRef -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this,
                            "Erreur lors de la création du compte : " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
