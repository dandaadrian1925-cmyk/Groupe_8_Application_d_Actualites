package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvArticles;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvArticles = findViewById(R.id.rvArticles);

        // ----------------------
        // Préparer la liste d'articles mockée
        // ----------------------
        articleList = new ArrayList<>();
        mockArticles();

        // ----------------------
        // Configurer RecyclerView
        // ----------------------
        articleAdapter = new ArticleAdapter(articleList, this::onArticleClick);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(articleAdapter);
    }

    // ----------------------
    // Clic sur un article
    // ----------------------
    private void onArticleClick(Article article) {
        Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
        intent.putExtra("articleId", article.getId());
        startActivity(intent);
    }

    // ----------------------
    // Mock articles
    // ----------------------
    private void mockArticles() {
        for (int i = 1; i <= 10; i++) {
            Article a = new Article();
            a.setId(String.valueOf(i));
            a.setTitle("Titre Article " + i);
            a.setDescription("Description courte de l'article " + i);
            a.setContent("Contenu complet de l'article " + i + "...");
            a.setCategory("Actualité");
            a.setAuthor("Auteur " + i);
            a.setPublishedAt("2026-02-07");
            articleList.add(a);
        }
    }
}