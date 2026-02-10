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

        // 🔹 Firestore
        db = FirebaseFirestore.getInstance();

        // 🔹 Récupération des vues
        tvConnexion = findViewById(R.id.tvConnexion);
        tvInscription = findViewById(R.id.tvInscription);

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnVisitor = findViewById(R.id.btnVisitor);

        // 🔹 Config switch Connexion / Inscription
        setupSwitchColors();

        tvConnexion.setOnClickListener(v -> {
            // Aller à LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        tvInscription.setOnClickListener(v -> {
            // Reste sur cette page, juste pour afficher le switch correctement
            setupSwitchColors();
        });

        // 🔹 Bouton Inscription
        btnRegister.setOnClickListener(v -> goToInformationPage());

        // 🔹 Bouton Visiteur
        btnVisitor.setOnClickListener(v -> {
            // Exemple : aller sur la page principale en mode visiteur
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupSwitchColors() {
        // Connexion non sélectionnée
        tvConnexion.setBackgroundColor(0xFFFFEEEE); // léger rouge
        tvConnexion.setTextColor(0xFF8B0000);       // texte rouge foncé

        // Inscription sélectionnée
        tvInscription.setBackgroundColor(0xFF8B0000); // rouge foncé
        tvInscription.setTextColor(0xFFFFFFFF);       // texte blanc
    }

    private void goToInformationPage() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 🔹 Validation champs
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

        // 🔹 Vérifier si email existe déjà dans Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Cette adresse email est déjà utilisée", Toast.LENGTH_LONG).show();
                        } else {
                            // 🔹 Email disponible, aller sur page Informations
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