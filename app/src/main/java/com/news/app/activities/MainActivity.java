package com.news.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;
import com.news.app.network.NewsApiService;
import com.news.app.network.NewsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvArticles;
    private EditText etSearch;
    private ArticleAdapter adapter;
    private List<Article> articlesList = new ArrayList<>();

    // 🔹 Clé API NewsAPI
    private final String API_KEY = "3705e91ac46643458fc204af4087954a";

    // Retrofit
    private NewsApiService newsApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        rvArticles = findViewById(R.id.rvArticles);
        etSearch = findViewById(R.id.etSearch);

        // RecyclerView
        adapter = new ArticleAdapter(this, articlesList);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsApiService = retrofit.create(NewsApiService.class);

        // Charger les articles par défaut
        fetchTopHeadlines();

        // Recherche en temps réel
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    searchArticles(s.toString());
                } else if (s.length() == 0) {
                    fetchTopHeadlines();
                }
            }
        });
    }

    // 🔹 Top headlines
    private void fetchTopHeadlines() {
        Call<NewsResponse> call = newsApiService.getTopHeadlines("us", "general", API_KEY);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    articlesList.clear();
                    articlesList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur récupération articles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Échec réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 🔹 Recherche par mot-clé
    private void searchArticles(String query) {
        Call<NewsResponse> call = newsApiService.searchArticles(query, "en", API_KEY);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    articlesList.clear();
                    articlesList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur récupération recherche", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Échec réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}