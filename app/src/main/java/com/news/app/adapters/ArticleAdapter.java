package com.news.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.news.app.R;
import com.news.app.activities.ArticleDetailActivity;
import com.news.app.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private final Context context;
    private final List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription;
        ImageView ivImage, ivFavorite;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvDescription = itemView.findViewById(R.id.tvArticleDescription);
            ivImage = itemView.findViewById(R.id.ivArticleImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite); // ajouter un icône favori dans item_article.xml
        }

        void bind(Article article) {
            tvTitle.setText(article.getTitle());
            tvDescription.setText(article.getDescription());

            // Charger l'image avec Glide
            Glide.with(context)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivImage);

            // Etat favori
            ivFavorite.setImageResource(article.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

            // Toggle favoris au clic sur l'icône
            ivFavorite.setOnClickListener(v -> {
                article.toggleFavorite();
                ivFavorite.setImageResource(article.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            });

            // Ouvrir ArticleDetailActivity au clic sur l'article
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("articleId", article.getId());
                context.startActivity(intent);
            });
        }
    }
}