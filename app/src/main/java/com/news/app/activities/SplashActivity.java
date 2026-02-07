package com.news.app.activities;
import com.news.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen affiché au lancement de l'application.
 * Affiche le logo "News" et passe automatiquement à LoginActivity après un délai.
 */
public class SplashActivity extends AppCompatActivity {

    // Durée du splash en millisecondes (2 secondes)
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Utilise Handler avec Looper.getMainLooper() pour être sûr d'exécuter sur le thread principal
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Redirection vers LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ferme le splash pour ne pas revenir en arrière
        }, SPLASH_DELAY);
    }
}