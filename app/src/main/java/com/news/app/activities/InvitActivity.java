package com.news.app.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;

public class InvitActivity extends AppCompatActivity {

    private ImageView btnBackInvit;
    private Button btnShareApp, btnCopyLink;
    private TextView tvAppLink;

    private String appLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invit);

        initViews();
        setupActions();
    }

    private void initViews() {
        btnBackInvit = findViewById(R.id.btnBackInvit);
        btnShareApp = findViewById(R.id.btnShareApp);
        btnCopyLink = findViewById(R.id.btnCopyLink);
        tvAppLink = findViewById(R.id.tvAppLink);

        appLink = tvAppLink.getText().toString();
    }

    private void setupActions() {

        // Bouton retour
        btnBackInvit.setOnClickListener(v -> finish());

        // Partager l'application
        btnShareApp.setOnClickListener(v -> shareApp());

        // Copier le lien
        btnCopyLink.setOnClickListener(v -> copyLinkToClipboard());
    }

    private void shareApp() {

        String shareMessage = "Télécharge cette super application News et reste informé en temps réel ! 📱\n\n"
                + appLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private void copyLinkToClipboard() {

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("App Link", appLink);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,
                    "Lien copié dans le presse-papier",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
