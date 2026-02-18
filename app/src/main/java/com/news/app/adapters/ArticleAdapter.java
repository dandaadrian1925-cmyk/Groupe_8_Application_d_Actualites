package com.news.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.app.R;
import com.news.app.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private final Context context;
    private final List<Article> articles;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvCategory, tvDate;
        ImageView ivThumbnail, ivFavorite;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitles);
            tvDescription = itemView.findViewById(R.id.tvDescriptions);
            tvCategory = itemView.findViewById(R.id.tvCategorys);
            tvDate = itemView.findViewById(R.id.tvDates);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnails);
            ivFavorite = itemView.findViewById(R.id.ivFavorites);
        }

        void bind(Article article) {

            if (article == null) return;

            tvTitle.setText(article.getTitle());
            tvDescription.setText(article.getDescription());
            tvCategory.setText(article.getCategory());
            tvDate.setText(article.getFriendlyDate());

            // Image avec Glide
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(article.getImageUrl())
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setBackgroundColor(Color.LTGRAY);
            }

            // ⭐ Etat initial
            ivFavorite.setImageResource(
                    article.isFavorite()
                            ? android.R.drawable.btn_star_big_on
                            : android.R.drawable.btn_star_big_off
            );

            // ⭐ Gestion clic favori
            ivFavorite.setOnClickListener(v -> {

                FirebaseUser user = auth.getCurrentUser();

                if (user == null) {
                    Toast.makeText(context,
                            "Veuillez vous connecter pour ajouter aux favoris",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();
                String articleId = article.getUrl();

                if (articleId == null || articleId.isEmpty()) {
                    Toast.makeText(context,
                            "Article invalide",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sécurisation ID Firestore
                String safeId = articleId
                        .replace("/", "_")
                        .replace(".", "_")
                        .replace("#", "_")
                        .replace("$", "_")
                        .replace("[", "_")
                        .replace("]", "_");

                if (article.isFavorite()) {

                    // 🔥 SUPPRESSION
                    db.collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(safeId)
                            .delete()
                            .addOnSuccessListener(unused -> {

                                article.setFavorite(false);
                                ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);

                                Toast.makeText(context,
                                        "Retiré des favoris",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context,
                                            "Erreur suppression : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );

                } else {

                    // 🔥 AJOUT
                    article.setFavorite(true); // Important

                    db.collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(safeId)
                            .set(article)
                            .addOnSuccessListener(unused -> {

                                ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);

                                Toast.makeText(context,
                                        "Ajouté aux favoris",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {

                                article.setFavorite(false);

                                Toast.makeText(context,
                                        "Erreur ajout : " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            });

            // 🌍 Ouvrir article
            itemView.setOnClickListener(v -> {
                if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(article.getUrl()));
                    context.startActivity(browserIntent);
                }
            });
        }
    }
}
