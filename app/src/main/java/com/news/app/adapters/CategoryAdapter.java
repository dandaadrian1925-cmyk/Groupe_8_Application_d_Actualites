package com.news.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.news.app.R;
import com.news.app.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Article> categoryList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public CategoryAdapter(Context context, List<Article> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = categoryList.get(position);

        holder.tvTitle.setText(article.getTitle());
        holder.tvCategory.setText(article.getCategory() != null ? article.getCategory() : "");

        // Image avec Picasso ou fond gris si absente
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Picasso.get().load(article.getImageUrl()).into(holder.ivImage);
            holder.ivImage.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.ivImage.setImageDrawable(null);
            holder.ivImage.setBackgroundColor(Color.LTGRAY);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(article));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivThumbnails);
            tvTitle = itemView.findViewById(R.id.tvTitles);
            tvCategory = itemView.findViewById(R.id.tvCategorys);
        }
    }
}