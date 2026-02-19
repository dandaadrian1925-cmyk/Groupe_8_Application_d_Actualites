package com.news.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.AuthCredential;
import com.news.app.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;
    private TextInputEditText editCurrentPassword, editNewPassword, editConfirmPassword;
    private MaterialButton btnUpdatePassword, btnCancelPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setupActions();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBackChangePassword);

        layoutCurrentPassword = findViewById(R.id.layoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnCancelPassword = findViewById(R.id.btnCancelPassword);
    }

    private void setupActions() {

        btnBack.setOnClickListener(v -> finish());
        btnCancelPassword.setOnClickListener(v -> finish());

        btnUpdatePassword.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {

        if (isLoading) return;

        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Reset erreurs
        layoutCurrentPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);

        if (TextUtils.isEmpty(currentPassword) ||
                TextUtils.isEmpty(newPassword) ||
                TextUtils.isEmpty(confirmPassword)) {

            Toast.makeText(this,
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔐 EXACTEMENT le même pattern que RegisterActivity
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";

        if (!newPassword.matches(passwordPattern)) {
            layoutNewPassword.setError(
                    "8 caractères min, 1 chiffre, 1 caractère spécial"
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError(
                    "Les mots de passe ne correspondent pas"
            );
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this,
                    "Utilisateur non connecté",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        btnUpdatePassword.setEnabled(false);
        btnUpdatePassword.setText("Mise à jour...");

        // 🔐 Réauthentification obligatoire Firebase
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {

                                    isLoading = false;
                                    btnUpdatePassword.setEnabled(true);
                                    btnUpdatePassword.setText("Mettre à jour");

                                    if (updateTask.isSuccessful()) {

                                        Toast.makeText(this,
                                                "Mot de passe modifié avec succès",
                                                Toast.LENGTH_LONG).show();

                                        finish();

                                    } else {

                                        Toast.makeText(this,
                                                "Erreur lors de la mise à jour",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {

                        isLoading = false;
                        btnUpdatePassword.setEnabled(true);
                        btnUpdatePassword.setText("Mettre à jour");

                        layoutCurrentPassword.setError(
                                "Mot de passe actuel incorrect"
                        );
                    }
                });
    }
}
