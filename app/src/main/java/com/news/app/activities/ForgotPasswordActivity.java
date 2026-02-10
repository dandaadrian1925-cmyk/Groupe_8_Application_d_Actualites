package com.news.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.news.app.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailForgot;
    private Button btnSendReset;
    private TextView tvBackLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Récupération des vues
        etEmailForgot = findViewById(R.id.etEmailForgot);
        btnSendReset = findViewById(R.id.btnSendReset);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        // 🔹 Bouton Envoyer
        btnSendReset.setOnClickListener(v -> resetPassword());

        // 🔹 Bouton Retour
        tvBackLogin.setOnClickListener(v -> finish()); // Retour à LoginActivity
    }

    private void resetPassword() {
        String email = etEmailForgot.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmailForgot.setError("Veuillez entrer votre email");
            etEmailForgot.requestFocus();
            return;
        }

        btnSendReset.setEnabled(false);
        btnSendReset.setText("Envoi...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSendReset.setEnabled(true);
                    btnSendReset.setText("Envoyer");

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Lien de réinitialisation envoyé ✅",
                                Toast.LENGTH_LONG).show();
                        finish(); // Retour à la page login
                    } else {
                        String errorMessage = "Erreur inattendue";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Erreur : " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}