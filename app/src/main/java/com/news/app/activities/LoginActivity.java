package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.news.app.R;

public class LoginActivity extends AppCompatActivity {

    private TextView tvConnexion, tvInscription;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView tvForgotPassword;
    private ImageView ivTogglePassword;

    private FirebaseAuth mAuth;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tvConnexion = findViewById(R.id.tvConnexion);
        tvInscription = findViewById(R.id.tvInscription);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        updateSwitchColors(true);

        ivTogglePassword.setOnClickListener(v -> {
            if (etPassword.getInputType() ==
                    (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            } else {

                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            etPassword.setSelection(etPassword.getText().length());
        });

        tvConnexion.setOnClickListener(v -> updateSwitchColors(true));

        tvInscription.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnLogin.setOnClickListener(v -> loginUser());

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    // 🔐 Redirection automatique si déjà connecté
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void updateSwitchColors(boolean connexionSelected) {

        if (connexionSelected) {

            tvConnexion.setBackgroundColor(0xFFFFEEEE);
            tvConnexion.setTextColor(0xFF8B0000);

            tvInscription.setBackgroundColor(0xFFFFFFFF);
            tvInscription.setTextColor(0xFF8B0000);

        } else {

            tvInscription.setBackgroundColor(0xFFFFEEEE);
            tvInscription.setTextColor(0xFF8B0000);

            tvConnexion.setBackgroundColor(0xFFFFFFFF);
            tvConnexion.setTextColor(0xFF8B0000);
        }
    }

    private void loginUser() {

        if (isLoading) return;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        isLoading = true;
        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    isLoading = false;
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Se connecter");

                    if (task.isSuccessful()) {

                        Toast.makeText(LoginActivity.this,
                                "Connexion réussie 🎉 Bienvenue !",
                                Toast.LENGTH_SHORT).show();

                        // 🔥 Nettoyage définitif de la back stack
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {

                        String errorMessage = "Erreur inattendue";

                        if (task.getException() != null) {

                            String message = task.getException().getMessage();

                            if (message.contains("no user record"))
                                errorMessage = "Aucun utilisateur trouvé avec cet email";
                            else if (message.contains("password is invalid"))
                                errorMessage = "Mot de passe incorrect";
                            else if (message.contains("email address is badly formatted"))
                                errorMessage = "Adresse email invalide";
                            else if (message.contains("disabled"))
                                errorMessage = "Ce compte a été désactivé";
                            else
                                errorMessage = message;
                        }

                        Toast.makeText(LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
