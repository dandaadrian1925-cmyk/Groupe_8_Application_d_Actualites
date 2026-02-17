package com.news.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvArticles;
    private EditText etSearch;
    private ArticleAdapter adapter;
    private final List<Article> articlesList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    private TextView tvProfile;
    private TextView tvNotifications;

    private NewsApiService newsApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupApi();
        setupListeners();

        fetchTopHeadlines();
    }

    private void initViews() {
        rvArticles = findViewById(R.id.rvArticles);
        etSearch = findViewById(R.id.etSearch);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        View header = findViewById(R.id.header);
        tvProfile = header.findViewById(R.id.tvProfile);
        tvNotifications = header.findViewById(R.id.tvNotifications);
    }

    private void setupRecyclerView() {
        adapter = new ArticleAdapter(this, articlesList);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);
    }

    private void setupApi() {
        newsApiService = RetrofitClient.getApiService();
    }

    private void setupListeners() {

        tvProfile.setOnClickListener(this::showProfileMenu);

        tvNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        );

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();

                if (query.length() > 2) {
                    searchArticles(query);
                } else if (query.isEmpty()) {
                    fetchTopHeadlines();
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                fetchTopHeadlines();
                return true;
            } else {
                articlesList.clear();
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        popupMenu.show();
    }

    private void fetchTopHeadlines() {
        Call<NewsResponse> call = newsApiService.getTopHeadlines();
        executeCall(call);
    }

    private void searchArticles(String query) {
        Call<NewsResponse> call = newsApiService.searchArticles(query);
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
                    showError("HTTP " + response.code(), response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {

                Toast.makeText(MainActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(String baseMessage, Response<?> response) {
        String message = baseMessage;

        try {
            if (response.errorBody() != null) {
                message += "\n" + response.errorBody().string();
            }
        } catch (IOException ignored) {}

        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
