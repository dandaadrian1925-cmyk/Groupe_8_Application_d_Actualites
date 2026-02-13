package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvConnexion, tvInscription;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnVisitor;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        // 🔹 Récupération des vues
        tvConnexion = findViewById(R.id.tvConnexion);
        tvInscription = findViewById(R.id.tvInscription);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnVisitor = findViewById(R.id.btnVisitor);

        // 🔹 Onglet "Inscription" sélectionné par défaut
        updateSwitchColors(true);

        // 🔹 Clic sur Connexion -> bascule vers Login
        tvConnexion.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // 🔹 Clic sur Inscription -> reste sur page
        tvInscription.setOnClickListener(v -> updateSwitchColors(true));

        // 🔹 Inscription
        btnRegister.setOnClickListener(v -> goToInformationPage());

        // 🔹 Visiteur
        btnVisitor.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    // 🔹 Met à jour les couleurs du switch
    private void updateSwitchColors(boolean inscriptionSelected) {
        if (inscriptionSelected) {
            tvInscription.setBackgroundColor(0xFF8B0000);
            tvInscription.setTextColor(0xFFFFFFFF);
            tvConnexion.setBackgroundColor(0xFFFFEEEE);
            tvConnexion.setTextColor(0xFF8B0000);
        } else {
            tvConnexion.setBackgroundColor(0xFF8B0000);
            tvConnexion.setTextColor(0xFFFFFFFF);
            tvInscription.setBackgroundColor(0xFFFFEEEE);
            tvInscription.setTextColor(0xFF8B0000);
        }
    }

    private void goToInformationPage() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Adresse email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Cette adresse email est déjà utilisée", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(RegisterActivity.this, InformationActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Erreur lors de la vérification de l'email", Toast.LENGTH_LONG).show();
                    }
                });
    }
}