package com.news.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.news.app.R;
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

            // Favori
            ivFavorite.setImageResource(article.isFavorite() ? android.R.drawable.btn_star_big_on
                    : android.R.drawable.btn_star_big_off);

            ivFavorite.setOnClickListener(v -> {
                article.toggleFavorite();
                ivFavorite.setImageResource(article.isFavorite() ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off);
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