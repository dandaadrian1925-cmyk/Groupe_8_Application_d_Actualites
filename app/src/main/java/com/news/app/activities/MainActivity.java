package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    // 🔥 FIREBASE
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // 🔥 Double back to exit
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            initViews();
            setupRecyclerView();
            setupApi();
            setupListeners();

            fetchTopHeadlines();

        } catch (Exception e) {
            Toast.makeText(this,
                    "Erreur initialisation : " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {

        rvArticles = findViewById(R.id.rvArticles);
        etSearch = findViewById(R.id.etSearch);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        View header = findViewById(R.id.header);

        if (header != null) {
            tvProfile = header.findViewById(R.id.tvProfile);
            tvNotifications = header.findViewById(R.id.tvNotifications);
        }
    }

    private void setupRecyclerView() {

        if (rvArticles == null) return;

        adapter = new ArticleAdapter(this, articlesList);
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(adapter);
    }

    private void setupApi() {
        newsApiService = RetrofitClient.getApiService();
    }

    private void setupListeners() {

        if (tvProfile != null) {
            tvProfile.setOnClickListener(this::showProfileMenu);
        }

        if (tvNotifications != null) {
            tvNotifications.setOnClickListener(v ->
                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
            );
        }

        if (etSearch != null) {
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
        }

        if (bottomNavigationView != null) {

            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            bottomNavigationView.setOnItemSelectedListener(item -> {

                if (item.getItemId() == R.id.nav_home) {

                    fetchTopHeadlines();
                    return true;

                } else if (item.getItemId() == R.id.nav_favorites) {

                    Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;

                } else if (item.getItemId() == R.id.nav_category) {

                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            });
        }
    }

    // 🔥 Double back pour quitter
    @Override
    public void onBackPressed() {
        if (SystemClock.elapsedRealtime() - backPressedTime < 2000) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Appuyez encore pour quitter", Toast.LENGTH_SHORT).show();
            backPressedTime = SystemClock.elapsedRealtime();
        }
    }

    private void showProfileMenu(View anchor) {

        if (anchor == null) return;

        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        popupMenu.show();
    }

    private void fetchTopHeadlines() {

        if (newsApiService == null) return;

        Call<NewsResponse> call = newsApiService.getTopHeadlines();
        executeCall(call);
    }

    private void searchArticles(String query) {

        if (newsApiService == null) return;

        Call<NewsResponse> call = newsApiService.searchArticles(query);
        executeCall(call);
    }

    private void executeCall(Call<NewsResponse> call) {

        if (call == null) return;

        call.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {

                if (!isFinishing() && response.isSuccessful()
                        && response.body() != null
                        && response.body().getArticles() != null) {

                    articlesList.clear();
                    articlesList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();

                    syncFavoritesWithFirestore();

                } else if (!isFinishing()) {
                    showError("HTTP " + response.code(), response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {

                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this,
                            "Erreur réseau : " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void syncFavoritesWithFirestore() {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (Article article : articlesList) {
                        article.setFavorite(false);
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Article favoriteArticle = document.toObject(Article.class);
                        if (favoriteArticle == null) continue;

                        for (Article article : articlesList) {

                            if (article.getUrl() != null &&
                                    article.getUrl().equals(favoriteArticle.getUrl())) {

                                article.setFavorite(true);
                                break;
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void showError(String baseMessage, Response<?> response) {

        String message = baseMessage;

        try {
            if (response != null && response.errorBody() != null) {
                message += "\n" + response.errorBody().string();
            }
        } catch (IOException ignored) {}

        if (!isFinishing()) {
            Toast.makeText(MainActivity.this,
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }
}
