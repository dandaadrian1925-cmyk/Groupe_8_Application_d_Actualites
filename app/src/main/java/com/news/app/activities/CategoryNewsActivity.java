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

public class CategoryNewsActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvCategoryNews;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_news);

        // Récupérer la catégorie envoyée
        categoryName = getIntent().getStringExtra("categoryName");

        if (categoryName == null) {
            categoryName = "Actualités";
        }

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvCategoryNews = findViewById(R.id.rvCategoryNews);

        tvCategoryTitle.setText(categoryName);

        articleList = new ArrayList<>();
        generateMockArticles(categoryName);

        articleAdapter = new ArticleAdapter(this, articleList);
        rvCategoryNews.setLayoutManager(new LinearLayoutManager(this));
        rvCategoryNews.setAdapter(articleAdapter);
    }

    private void generateMockArticles(String category) {
        for (int i = 1; i <= 10; i++) {
            Article article = new Article();
            article.setId(category + "_" + i);
            article.setTitle(category + " - Article " + i);
            article.setDescription("Description de l'article " + i + " dans " + category);
            article.setContent("Contenu complet de l'article " + i);
            article.setCategory(category);
            article.setAuthor("Auteur " + i);
            article.setPublishedAt("2026-02-07");

            articleList.add(article);
        }
    }
}