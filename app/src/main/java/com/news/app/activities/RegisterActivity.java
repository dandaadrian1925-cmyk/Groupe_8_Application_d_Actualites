package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvConnexion, tvInscription;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnVisitor;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvConnexion = findViewById(R.id.tvConnexion);
        tvInscription = findViewById(R.id.tvInscription);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnVisitor = findViewById(R.id.btnVisitor);

        updateSwitchColors(true);

        tvConnexion.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        tvInscription.setOnClickListener(v -> updateSwitchColors(true));

        btnRegister.setOnClickListener(v -> goToInformationPage());

        btnVisitor.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // 🔐 Si déjà connecté → redirection automatique
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void updateSwitchColors(boolean inscriptionSelected) {

        if (inscriptionSelected) {

            tvInscription.setBackgroundColor(0xFFFFEEEE);
            tvInscription.setTextColor(0xFF8B0000);

            tvConnexion.setBackgroundColor(0xFFFFFFFF);
            tvConnexion.setTextColor(0xFF8B0000);

        } else {

            tvConnexion.setBackgroundColor(0xFFFFEEEE);
            tvConnexion.setTextColor(0xFF8B0000);

            tvInscription.setBackgroundColor(0xFFFFFFFF);
            tvInscription.setTextColor(0xFF8B0000);
        }
    }

    private void goToInformationPage() {

        if (isLoading) return;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword)) {

            Toast.makeText(this,
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this,
                    "Adresse email invalide",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";

        if (!password.matches(passwordPattern)) {
            Toast.makeText(this,
                    "Mot de passe invalide (8 caractères minimum, 1 chiffre, 1 caractère spécial)",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this,
                    "Les mots de passe ne correspondent pas",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        btnRegister.setEnabled(false);
        btnRegister.setText("Inscription...");

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {

                    isLoading = false;
                    btnRegister.setEnabled(true);
                    btnRegister.setText("S'inscrire");

                    if (task.isSuccessful()) {

                        if (!task.getResult().isEmpty()) {

                            Toast.makeText(RegisterActivity.this,
                                    "Cette adresse email est déjà utilisée",
                                    Toast.LENGTH_LONG).show();

                        } else {

                            Intent intent = new Intent(RegisterActivity.this,
                                    InformationActivity.class);

                            intent.putExtra("email", email);
                            intent.putExtra("password", password);

                            startActivity(intent);
                        }

                    } else {

                        Toast.makeText(RegisterActivity.this,
                                "Erreur lors de la vérification de l'email",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
