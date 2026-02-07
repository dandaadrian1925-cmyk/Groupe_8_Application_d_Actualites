package com.news.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvCategoryArticles;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Récupérer la catégorie envoyée par Intent
        categoryName = getIntent().getStringExtra("categoryName");

        if (categoryName == null) {
            categoryName = "Catégorie";
        }

        // Initialiser les vues
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvCategoryArticles = findViewById(R.id.rvCategoryArticles);

        // Afficher le nom de la catégorie
        tvCategoryTitle.setText(categoryName);

        // Initialiser la liste
        articleList = new ArrayList<>();

        // Générer des articles mockés pour cette catégorie
        mockArticles(categoryName);

        // Configurer RecyclerView
        articleAdapter = new ArticleAdapter(this, articleList);
        rvCategoryArticles.setLayoutManager(new LinearLayoutManager(this));
        rvCategoryArticles.setAdapter(articleAdapter);
    }

    // Génération articles fictifs
    private void mockArticles(String category) {
        for (int i = 1; i <= 10; i++) {
            Article a = new Article();
            a.setId(category + "_" + i);
            a.setTitle(category + " - Article " + i);
            a.setDescription("Description de l'article " + i + " dans " + category);
            a.setContent("Contenu complet de l'article " + i + " dans la catégorie " + category);
            a.setCategory(category);
            a.setAuthor("Auteur " + i);
            a.setPublishedAt("2026-02-07");
            articleList.add(a);
        }
    }
}