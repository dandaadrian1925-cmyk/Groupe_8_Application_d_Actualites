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

public class LoginActivity extends AppCompatActivity {

    // Champs
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Lier les vues
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Clique sur le bouton Connexion
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Clique sur "Mot de passe oublié ?"
        tvForgotPassword.setOnClickListener(v -> {
            // Redirection vers ForgotPasswordActivity
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // Clique sur "Créer un compte"
        tvRegister.setOnClickListener(v -> {
            // Redirection vers RegisterActivity
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    /**
     * Tente de connecter l'utilisateur après validation des champs
     */
    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validation email
        if (!isValidEmail(email)) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        // Validation mot de passe
        if (!isValidPassword(password)) {
            etPassword.setError("Mot de passe invalide (8+ chars, 1 chiffre, 1 spécial)");
            etPassword.requestFocus();
            return;
        }

        // TODO: connecter avec Firebase Auth ou ton backend
        // Pour l'instant, on affiche un Toast de succès
        Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

        // Redirection vers MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish(); // Ferme LoginActivity pour ne pas revenir dessus
    }

    // ----------------------
    // Validation simple
    // ----------------------
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        // 8+ caractères, au moins 1 chiffre et 1 caractère spécial
        String pattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(pattern);
    }
}