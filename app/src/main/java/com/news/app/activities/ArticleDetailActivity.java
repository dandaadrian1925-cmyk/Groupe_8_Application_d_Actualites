package com.news.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // ton layout avec l'animation

        new Handler(g6).postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // utilisateur déjà connecté
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            } else {
                // utilisateur non connecté
                startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
