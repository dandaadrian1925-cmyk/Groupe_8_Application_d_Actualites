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

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        if (header != null) {
            tvProfile = header.findViewById(R.id.tvProfile);
            tvNotifications = header.findViewById(R.id.tvNotifications);
        }
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

                int id = item.getItemId();

                if (id == R.id.nav_home) {

                    fetchTopHeadlines();
                    return true;

                } else if (id == R.id.nav_favorites) {

                    Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;

                } else if (id == R.id.nav_category) {

                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;

                } else if (id == R.id.nav_profile) {

                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            });
        }
    }

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

        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_view_profile) {

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.menu_settings) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.menu_logout) {

                auth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            return false;
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

                    syncFavoritesWithFirestore();
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
}
