package com.news.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private ArticleAdapter articleAdapter;
    private List<Article> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = findViewById(R.id.rvFavorites);

        favoriteList = new ArrayList<>();

        loadFavoriteArticles();

        articleAdapter = new ArticleAdapter(this, favoriteList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(articleAdapter);
    }

    private void loadFavoriteArticles() {

        // MOCK TEMPORAIRE (plus tard on utilisera Room ou Firebase)

        for (int i = 1; i <= 10; i++) {
            Article article = new Article();
            article.setUrl(String.valueOf(i));
            article.setTitle("Article Favori " + i);
            article.setDescription("Description article favori " + i);
            article.setContent("Contenu complet favori " + i);
            article.setCategory("Favoris");
            article.setAuthor("Auteur " + i);
            article.setPublishedAt("2026-02-07");

            article.setFavorite(true); // IMPORTANT

            favoriteList.add(article);
        }
    }
}