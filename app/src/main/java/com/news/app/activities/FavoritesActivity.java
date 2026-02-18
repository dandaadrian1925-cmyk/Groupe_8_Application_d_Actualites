package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private EditText etSearchFavorites;
    private BottomNavigationView bottomNavigationView;

    private ArticleAdapter adapter;

    private final List<Article> favoritesList = new ArrayList<>();
    private final List<Article> fullFavoritesList = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecycler();
        setupBottomNavigation();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        etSearchFavorites = findViewById(R.id.etSearchFavorites);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecycler() {
        adapter = new ArticleAdapter(this, favoritesList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_favorites) {

                return true;

            } else if (id == R.id.nav_category) {

                Intent intent = new Intent(FavoritesActivity.this, CategoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_profile) {

                Intent intent = new Intent(FavoritesActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void setupSearch() {

        etSearchFavorites.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                String query = s.toString().toLowerCase().trim();

                favoritesList.clear();

                if (query.isEmpty()) {
                    favoritesList.addAll(fullFavoritesList);
                } else {
                    for (Article article : fullFavoritesList) {

                        if (article.getTitle() != null &&
                                article.getTitle().toLowerCase().contains(query)) {

                            favoritesList.add(article);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadFavorites() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(this,
                    "Veuillez vous connecter pour voir vos favoris",
                    Toast.LENGTH_LONG).show();

            favoritesList.clear();
            fullFavoritesList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (isFinishing()) return;

                    favoritesList.clear();
                    fullFavoritesList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Article article = document.toObject(Article.class);

                        if (article != null) {
                            article.setFavorite(true);
                            favoritesList.add(article);
                            fullFavoritesList.add(article);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (favoritesList.isEmpty()) {
                        Toast.makeText(this,
                                "Aucun favori pour le moment",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                    if (!isFinishing()) {
                        Toast.makeText(this,
                                "Erreur chargement favoris : " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
