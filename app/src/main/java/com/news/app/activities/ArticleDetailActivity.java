package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;
import com.news.app.model.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDate, tvContent;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAuthor = findViewById(R.id.tvDetailAuthor);
        tvDate = findViewById(R.id.tvDetailDate);
        tvContent = findViewById(R.id.tvDetailContent);
        ivImage = findViewById(R.id.ivDetailImage);

        // Récupérer l'article (mocké pour l'instant)
        Article article = getArticleFromIntent();

        if (article != null) {
            displayArticle(article);
        }
    }

    // ----------------------
    // Méthode pour récupérer l'article passé via Intent
    // ----------------------
    private Article getArticleFromIntent() {
        // Pour l'instant mock si aucun passage réel
        String articleId = getIntent().getStringExtra("articleId");
        if (articleId == null) return null;

        // Mock : créer un article basé sur l'ID
        Article a = new Article();
        a.setUrl(articleId);
        a.setTitle("Titre Article " + articleId);
        a.setAuthor("Auteur " + articleId);
        a.setPublishedAt("2026-02-07");
        a.setContent("Contenu complet de l'article " + articleId + "...");
        a.setImageUrl(""); // Pour l'instant vide
        return a;
    }

    // ----------------------
    // Afficher l'article
    // ----------------------
    private void displayArticle(Article article) {
        tvTitle.setText(article.getTitle());
        tvAuthor.setText("Par " + article.getAuthor());
        tvContent.setText(article.getContent());

        // Formater la date
        tvDate.setText(formatDate(article.getPublishedAt()));

        // Image placeholder
        ivImage.setImageResource(R.drawable.ic_launcher_background);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "";
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfOutput = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date date = sdfInput.parse(dateStr);
            return sdfOutput.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }
}