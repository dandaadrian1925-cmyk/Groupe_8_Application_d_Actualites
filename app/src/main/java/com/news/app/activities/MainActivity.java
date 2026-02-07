package com.news.app.activities;
import com.news.app.R;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

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

            // TODO: ici tu peux connecter à Firebase ou ton backend
            Toast.makeText(MainActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
        });
    }

    // Vérifie email
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Vérifie mot de passe : 8+ caractères, au moins 1 chiffre et 1 caractère spécial
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(passwordPattern);
    }
}
