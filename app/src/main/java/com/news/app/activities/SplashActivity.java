package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.news.app.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2,5 secondes de splash
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ⚡ Initialiser Firebase
        FirebaseApp.initializeApp(this);

        // Initialiser FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Attendre SPLASH_DELAY ms puis vérifier l'utilisateur
        new Handler(Looper.getMainLooper()).postDelayed(this::checkUser, SPLASH_DELAY);
    }

    // Vérifier si l'utilisateur est connecté
    private void checkUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            // Utilisateur connecté → MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Non connecté → LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Empêche de revenir à la splash screen
    }
}