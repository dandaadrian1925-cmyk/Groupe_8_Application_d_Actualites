package com.news.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth mAuth;

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

        this.setFinishOnTouchOutside(false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        btnSubmit = findViewById(R.id.btnSubmit);
        categoriesLayout = findViewById(R.id.categoriesLayout);

        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        LinearLayout rootLayout = findViewById(R.id.categoriesLayout);
        rootLayout.addView(progressBar);

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

        // ✅ Limiter la date maximum à 31/12/2010
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2010, Calendar.DECEMBER, 31);
        dpd.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

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

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Validation prénom
        if (firstName.length() < 3 || Character.isDigit(firstName.charAt(0))) {
            Toast.makeText(this,
                    "Le prénom doit contenir au moins 3 caractères et ne pas commencer par un chiffre",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Validation nom
        if (lastName.length() < 3 || Character.isDigit(lastName.charAt(0))) {
            Toast.makeText(this,
                    "Le nom doit contenir au moins 3 caractères et ne pas commencer par un chiffre",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner au moins une catégorie", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setDateOfBirth(dateOfBirth);
                    user.setPreferences(new ArrayList<>(selectedCategories));
                    user.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                    user.setRole("user");
                    user.setProfileImageUrl("");

                    db.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, SplashActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                btnSubmit.setEnabled(true);
                                Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Erreur Auth : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onBackPressed() {
        // Désactivé
    }
}
