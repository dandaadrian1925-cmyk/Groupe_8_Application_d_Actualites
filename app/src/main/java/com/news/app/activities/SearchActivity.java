package com.news.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvSearchResults;

    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private List<Article> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        articleList = new ArrayList<>();
        filteredList = new ArrayList<>();

        generateMockArticles();

        articleAdapter = new ArticleAdapter(this, filteredList);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(articleAdapter);

        // Listener de recherche
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void generateMockArticles() {
        for (int i = 1; i <= 15; i++) {
            Article article = new Article();
            article.setId(String.valueOf(i));
            article.setTitle("Actualité Sport " + i);
            article.setDescription("Description de l'article " + i);
            article.setContent("Contenu complet " + i);
            article.setCategory("Sport");
            article.setAuthor("Auteur " + i);
            article.setPublishedAt("2026-02-07");

            articleList.add(article);
        }

        // afficher tout au début
        filteredList.addAll(articleList);
    }

    private void filterArticles(String query) {
        filteredList.clear();

        for (Article article : articleList) {
            if (article.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(article);
            }
        }

        articleAdapter.notifyDataSetChanged();
    }
}