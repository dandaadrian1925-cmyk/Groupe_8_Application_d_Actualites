package com.news.app.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;

public class ArticleDetailActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private ImageView ivBack;
    private TextView tvTitle;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Initialisation des vues
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvArticleTitle);

        // Récupération des données
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        if (title != null && !title.isEmpty()) {
            tvTitle.setText(title);
        }

        // Configuration WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        progressBar.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        }

        // Bouton retour
        ivBack.setOnClickListener(v -> finish());
    }

    // Gestion bouton retour Android
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Nettoyage mémoire
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }
}
