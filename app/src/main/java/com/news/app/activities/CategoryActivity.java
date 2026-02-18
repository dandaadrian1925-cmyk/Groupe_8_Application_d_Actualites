package com.news.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;
import com.news.app.model.NewsResponse;
import com.news.app.network.NewsApiService;
import com.news.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private LinearLayout categoriesContainer;
    private RecyclerView rvArticles;
    private EditText etSearch;
    private BottomNavigationView bottomNavigationView;

    private ArticleAdapter adapter;
    private final List<Article> articlesList = new ArrayList<>();

    private NewsApiService newsApiService;

    // Catégories officielles NewsAPI
    private final String[] NEWS_CATEGORIES = {
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"
    };

    private String selectedCategory = null;
    private TextView activeCategoryView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initViews();
        setupRecycler();
        setupApi();
        setupBottomNavigation();
        setupSearch();
        addCategories();

        fetchArticles(null);
    }

    private void initViews() {
        categoriesContainer = findViewById(R.id.categoriesContainer);
        rvArticles = findViewById(R.id.rvCategories);
        etSearch = findViewById(R.id.etSearchCategory);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecycler() {
        adapter = new ArticleAdapter(this, articlesList);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);
    }

    private void setupApi() {
        newsApiService = RetrofitClient.getApiService();
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_category);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (item.getItemId() == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    private void setupSearch() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                String query = s.toString().trim();

                if (query.length() > 2) {
                    fetchArticles(query);
                } else if (query.isEmpty()) {
                    fetchArticles(null);
                }
            }
        });
    }

    private void addCategories() {

        for (String cat : NEWS_CATEGORIES) {

            TextView tv = new TextView(this);
            tv.setText(cat);
            tv.setPadding(50, 20, 50, 20);
            tv.setTextSize(14f);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.parseColor("#E0E0E0")); // gris clair

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 30, 0);
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> {

                selectedCategory = cat;

                // Réinitialiser ancienne sélection
                if (activeCategoryView != null) {
                    activeCategoryView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    activeCategoryView.setTextColor(Color.BLACK);
                }

                // Appliquer nouvelle sélection
                tv.setBackgroundColor(Color.parseColor("#6200EE")); // violet
                tv.setTextColor(Color.WHITE);

                activeCategoryView = tv;

                fetchArticles(etSearch.getText().toString().trim());
            });

            categoriesContainer.addView(tv);
        }
    }

    private void fetchArticles(String query) {

        if (newsApiService == null) return;

        Call<NewsResponse> call;

        if (query != null && query.length() > 2) {

            if (selectedCategory == null) {
                call = newsApiService.searchArticles(query);
            } else {
                call = newsApiService.searchByCategory(query, selectedCategory);
            }

        } else {

            if (selectedCategory == null) {
                call = newsApiService.getTopHeadlines();
            } else {
                call = newsApiService.getByCategory(selectedCategory);
            }
        }

        executeCall(call);
    }

    private void executeCall(Call<NewsResponse> call) {

        call.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getArticles() != null) {

                    articlesList.clear();
                    articlesList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(CategoryActivity.this,
                            "Erreur HTTP " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {

                Toast.makeText(CategoryActivity.this,
                        "Erreur : " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
