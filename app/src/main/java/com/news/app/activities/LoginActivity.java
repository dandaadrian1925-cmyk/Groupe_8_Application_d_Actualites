package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.news.app.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView tvForgotPassword;

    private FirebaseAuth mAuth;

    // 🔹 Pour simuler le chargement (Flutter _loading)
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();

        // 🔹 Récupération des vues
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // créer dans XML

        // 🔹 Clic sur bouton Login
        btnLogin.setOnClickListener(v -> loginUser());

        // 🔹 Clic sur bouton Inscription
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 🔹 Clic sur Mot de passe oublié
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        if (isLoading) return; // empêche double clic

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 🔹 Validation des champs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Veuillez entrer votre email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Veuillez entrer votre mot de passe");
            etPassword.requestFocus();
            return;
        }

        // 🔹 Simuler le chargement
        isLoading = true;
        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion...");

        // 🔹 Connexion Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // 🔹 Stop chargement
                    isLoading = false;
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Se connecter");

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Connexion réussie 🎉 Bienvenue !",
                                Toast.LENGTH_SHORT).show();

                        // 🔹 Redirection vers l'écran principal
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 🔹 Gestion des erreurs
                        String errorMessage = "Erreur inattendue";
                        if (task.getException() != null) {
                            String message = task.getException().getMessage();
                            if (message.contains("no user record")) {
                                errorMessage = "Aucun utilisateur trouvé avec cet email";
                            } else if (message.contains("password is invalid")) {
                                errorMessage = "Mot de passe incorrect";
                            } else if (message.contains("email address is badly formatted")) {
                                errorMessage = "Adresse email invalide";
                            } else if (message.contains("disabled")) {
                                errorMessage = "Ce compte a été désactivé";
                            } else {
                                errorMessage = message;
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}