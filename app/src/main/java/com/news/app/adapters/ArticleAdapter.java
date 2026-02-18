package com.news.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return articles.size();
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

            // Etat initial favori
            ivFavorite.setImageResource(article.isFavorite()
                    ? android.R.drawable.btn_star_big_on
                    : android.R.drawable.btn_star_big_off);

            // Gestion clic favori
            ivFavorite.setOnClickListener(v -> {

                FirebaseUser user = auth.getCurrentUser();

                // 🔒 Si utilisateur non connecté
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

                if (article.isFavorite()) {

                    // 🔥 Suppression Firestore
                    db.collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(articleId)
                            .delete();

                    article.toggleFavorite();

                    ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);

                    Toast.makeText(context,
                            "Retiré des favoris",
                            Toast.LENGTH_SHORT).show();

                } else {

                    // 🔥 Ajout Firestore
                    db.collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(articleId)
                            .set(article);

                    article.toggleFavorite();

                    ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);

                    Toast.makeText(context,
                            "Ajouté aux favoris",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Ouvrir article dans navigateur
            itemView.setOnClickListener(v -> {
                if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            android.net.Uri.parse(article.getUrl()));
                    context.startActivity(browserIntent);
                }
            });
        }
    }
}
