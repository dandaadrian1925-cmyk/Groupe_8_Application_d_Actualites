package com.news.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;
import com.news.app.adapters.ArticleAdapter;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private EditText etSearch;
    private BottomNavigationView bottomNavigationView;

    private ArticleAdapter adapter;
    private final List<Article> favoritesList = new ArrayList<>();
    private final List<Article> fullList = new ArrayList<>();

    private FirebaseFirestore db;

    private TextView tvProfile;
    private TextView tvNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupRecycler();
        setupListeners();

        db = FirebaseFirestore.getInstance();

        loadFavorites();
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        etSearch = findViewById(R.id.etSearchFavorites);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        View header = findViewById(R.id.header);
        tvProfile = header.findViewById(R.id.tvProfile);
        tvNotifications = header.findViewById(R.id.tvNotifications);
    }

    private void setupRecycler() {
        adapter = new ArticleAdapter(this, favoritesList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }

    private void setupListeners() {

        // 🔥 Bouton favoris sélectionné
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
            return true;
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterFavorites(s.toString());
            }
        });

        tvNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        );

        tvProfile.setOnClickListener(this::showProfileMenu);
    }

    private void loadFavorites() {
        db.collection("favorites")
                .get()
                .addOnSuccessListener(query -> {

                    favoritesList.clear();
                    fullList.clear();

                    for (var doc : query) {
                        Article article = doc.toObject(Article.class);
                        favoritesList.add(article);
                        fullList.add(article);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void filterFavorites(String query) {

        favoritesList.clear();

        if (query.isEmpty()) {
            favoritesList.addAll(fullList);
        } else {
            for (Article article : fullList) {
                if (article.getTitle() != null &&
                        article.getTitle().toLowerCase()
                                .contains(query.toLowerCase())) {
                    favoritesList.add(article);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.show();
    }
}