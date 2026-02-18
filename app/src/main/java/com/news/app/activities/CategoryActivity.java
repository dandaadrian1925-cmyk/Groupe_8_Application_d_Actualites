package com.news.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    private final String[] NEWS_CATEGORIES = {
            "business", "entertainment", "general",
            "health", "science", "sports", "technology"
    };

    private String selectedCategory = null;
    private TextView activeCategoryView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoriesContainer = findViewById(R.id.categoriesContainer);
        rvArticles = findViewById(R.id.rvCategories);
        etSearch = findViewById(R.id.etSearchCategory);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        adapter = new ArticleAdapter(this, articlesList);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);

        newsApiService = RetrofitClient.getApiService();

        setupBottomNavigation();
        setupSearch();
        addCategories();

        fetchArticles(null);
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_category);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_favorites) {

                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_category) {

                return true; // déjà sur cette page

            } else if (id == R.id.nav_profile) {

                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
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

        int redColor = ContextCompat.getColor(this, R.color.dark_red);
        int lightGray = Color.parseColor("#F1F1F1");

        for (String cat : NEWS_CATEGORIES) {

            TextView tv = new TextView(this);
            tv.setText(cat.toUpperCase());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tv.setPadding(60, 30, 60, 30);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.DKGRAY);

            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(70);
            bg.setColor(lightGray);
            tv.setBackground(bg);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(20, 15, 20, 15);
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> {

                selectedCategory = cat;

                if (activeCategoryView != null) {
                    GradientDrawable oldBg = new GradientDrawable();
                    oldBg.setCornerRadius(70);
                    oldBg.setColor(lightGray);
                    activeCategoryView.setBackground(oldBg);
                    activeCategoryView.setTextColor(Color.DKGRAY);
                }

                GradientDrawable selectedBg = new GradientDrawable();
                selectedBg.setCornerRadius(70);
                selectedBg.setColor(redColor);

                tv.setBackground(selectedBg);
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

        if (selectedCategory != null) {

            if (query != null && query.length() > 2) {
                call = newsApiService.searchByCategory(query, selectedCategory);
            } else {
                call = newsApiService.getByCategory(selectedCategory);
            }

        } else {

            if (query != null && query.length() > 2) {
                call = newsApiService.searchArticles(query);
            } else {
                call = newsApiService.getTopHeadlines();
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
                    rvArticles.scrollToPosition(0);

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
