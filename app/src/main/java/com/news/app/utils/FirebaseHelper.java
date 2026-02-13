package com.news.app.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.news.app.model.Article;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final CollectionReference articlesRef;

    public FirebaseHelper() {
        FirebaseApp.initializeApp(FirebaseAuth.getInstance().getApp().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Collection "articles" dans Firestore
        articlesRef = db.collection("articles");
    }

    // Récupérer l'utilisateur actuel
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Vérifier si utilisateur connecté
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    // Ajouter un article aux favoris
    public void addFavorite(Article article) {
        if (!isUserLoggedIn() || article == null) return;

        String uid = getCurrentUser().getUid();
        db.collection("users")
                .document(uid)
                .collection("favorites")
                .document(article.getUrl()) // On utilise URL comme ID unique
                .set(article);
    }

    // Supprimer un article des favoris
    public void removeFavorite(Article article) {
        if (!isUserLoggedIn() || article == null) return;

        String uid = getCurrentUser().getUid();
        db.collection("users")
                .document(uid)
                .collection("favorites")
                .document(article.getUrl())
                .delete();
    }

    // Récupérer la liste des favoris
    public void getFavorites(OnFavoritesLoadedListener listener) {
        if (!isUserLoggedIn()) {
            if (listener != null) listener.onLoaded(new ArrayList<>());
            return;
        }

        String uid = getCurrentUser().getUid();
        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    List<Article> favorites = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Article article = doc.toObject(Article.class);
                            article.setFavorite(true);
                            favorites.add(article);
                        }
                    }
                    if (listener != null) listener.onLoaded(favorites);
                });
    }

    // Listener callback pour la récupération des favoris
    public interface OnFavoritesLoadedListener {
        void onLoaded(List<Article> favorites);
    }
}