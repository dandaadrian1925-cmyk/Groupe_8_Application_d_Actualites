package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.news.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etDOB, etEmail, etPassword;
    private MaterialButton btnRegister;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Lier les vues
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDOB = findViewById(R.id.etDOB);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Clique sur Créer compte
        btnRegister.setOnClickListener(v -> attemptRegister());

        // Lien vers Login
        tvLoginLink.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void attemptRegister() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dob = etDOB.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // ----------------------
        // Validation des champs
        // ----------------------
        if (!isValidName(firstName)) {
            etFirstName.setError("Prénom invalide (≥3 lettres, ne commence pas par chiffre)");
            etFirstName.requestFocus();
            return;
        }

        if (!isValidName(lastName)) {
            etLastName.setError("Nom invalide (≥3 lettres, ne commence pas par chiffre)");
            etLastName.requestFocus();
            return;
        }

        if (!isValidDOB(dob)) {
            etDOB.setError("Date invalide (avant 01-01-2010)");
            etDOB.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        if (!isValidPassword(password)) {
            etPassword.setError("Mot de passe invalide (8+ chars, 1 chiffre, 1 spécial)");
            etPassword.requestFocus();
            return;
        }

        // ----------------------
        // TODO: Ajouter inscription Firebase ou backend ici
        // ----------------------
        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

        // Redirection vers LoginActivity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    // ----------------------
    // Validation
    // ----------------------
    private boolean isValidName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 3) return false;
        return !Character.isDigit(name.charAt(0));
    }

    private boolean isValidDOB(String dob) {
        if (TextUtils.isEmpty(dob)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dob);
            Date limit = sdf.parse("2010-01-01");
            return date.before(limit);
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        String pattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(pattern);
    }
}