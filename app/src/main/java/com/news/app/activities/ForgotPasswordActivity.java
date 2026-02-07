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

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Lier les vues
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Clique sur Réinitialiser
        btnResetPassword.setOnClickListener(v -> attemptReset());

        // Lien vers Login
        tvLoginLink.setOnClickListener(v -> startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class)));
    }

    private void attemptReset() {
        String email = etEmail.getText().toString().trim();

        if (!isValidEmail(email)) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        // ----------------------
        // TODO: Ajouter logique Firebase / backend pour reset mot de passe
        // ----------------------
        Toast.makeText(this, "Email de réinitialisation envoyé !", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}