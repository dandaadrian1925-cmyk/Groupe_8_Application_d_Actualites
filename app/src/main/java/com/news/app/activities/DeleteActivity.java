package com.news.app.activities;

import com.news.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteActivity extends AppCompatActivity {

    private ImageView btnBackDelete;
    private MaterialButton btnConfirmDelete, btnCancelDelete;
    private TextInputEditText editDeletePassword;
    private TextInputLayout layoutDeletePassword;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBackDelete = findViewById(R.id.btnBackDelete);
        btnConfirmDelete = findViewById(R.id.btnConfirmDelete);
        btnCancelDelete = findViewById(R.id.btnCancelDelete);
        editDeletePassword = findViewById(R.id.editDeletePassword);
        layoutDeletePassword = findViewById(R.id.layoutDeletePassword);
    }

    private void setupListeners() {

        btnBackDelete.setOnClickListener(v -> finish());

        btnCancelDelete.setOnClickListener(v -> finish());

        btnConfirmDelete.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = editDeletePassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            layoutDeletePassword.setError("Mot de passe requis");
            return;
        } else {
            layoutDeletePassword.setError(null);
        }

        String email = user.getEmail();

        if (email == null) {
            Toast.makeText(this, "Erreur email utilisateur", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔐 Re-authentification obligatoire
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    String uid = user.getUid();

                    // 🔥 Supprimer Firestore
                    db.collection("users")
                            .document(uid)
                            .delete()
                            .addOnSuccessListener(unused1 -> {

                                // 🔥 Supprimer compte Firebase Auth
                                user.delete()
                                        .addOnSuccessListener(unused2 -> {

                                            auth.signOut();

                                            Toast.makeText(DeleteActivity.this,
                                                    "Compte supprimé définitivement",
                                                    Toast.LENGTH_LONG).show();

                                            // 🔁 Redirection Login
                                            Intent intent = new Intent(DeleteActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(DeleteActivity.this,
                                                        "Erreur suppression compte : " + e.getMessage(),
                                                        Toast.LENGTH_LONG).show());
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(DeleteActivity.this,
                                            "Erreur suppression données : " + e.getMessage(),
                                            Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e ->
                        layoutDeletePassword.setError("Mot de passe incorrect"));
    }
}
