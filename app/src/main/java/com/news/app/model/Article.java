package com.news.app.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Article {

    // ----------------------
    // Champs API
    // ----------------------

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("urlToImage")
    private String imageUrl;

    @SerializedName("url")
    private String url; // Utilisé comme ID unique

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("author")
    private String author;

    @SerializedName("source")
    private Source source;

    // ----------------------
    // Champs locaux (non API)
    // ----------------------

    private String category;      // Catégorie locale
    private boolean isFavorite;   // Gestion favoris

    // ----------------------
    // Constructeur vide (obligatoire pour Gson)
    // ----------------------

    public Article() {}

    // ----------------------
    // Getters & Setters sécurisés
    // ----------------------

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt != null ? publishedAt : "";
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getAuthor() {
        return author != null ? author : "Auteur inconnu";
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceName() {
        return source != null && source.name != null ? source.name : "";
    }

    // ----------------------
    // Gestion favoris
    // ----------------------

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void toggleFavorite() {
        isFavorite = !isFavorite;
    }

    // ----------------------
    // Gestion date
    // ----------------------

    public Date getPublishedDate() {
        if (publishedAt == null || publishedAt.isEmpty()) return null;

        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            return sdf.parse(publishedAt);
        } catch (ParseException e) {
            return null;
        }
    }

    public String getFriendlyDate() {
        Date date = getPublishedDate();
        if (date == null) return "";

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // ----------------------
    // Equals & HashCode
    // Basés sur l'URL (identifiant unique)
    // ----------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return Objects.equals(url, article.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    // ----------------------
    // Classe interne Source
    // ----------------------

    public static class Source {

        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;
    }
}