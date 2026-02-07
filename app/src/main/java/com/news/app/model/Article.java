package com.news.app.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Article {

    // ----------------------
    // Champs principaux
    // ----------------------
    private String id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String category;
    private String author;
    private String publishedAt; // format "yyyy-MM-dd"
    private String url;
    private boolean isFavorite;

    // ----------------------
    // Champs supplémentaires
    // ----------------------
    private String sourceName;
    private String sourceId;
    private List<String> tags;
    private int readTime;
    private String language;
    private String videoUrl;
    private int views;

    // ----------------------
    // Constructeur vide
    // ----------------------
    public Article() {
        this.tags = new ArrayList<>();
    }

    // ----------------------
    // Constructeur complet
    // ----------------------
    public Article(String id, String title, String description, String content,
                   String imageUrl, String category, String author,
                   String publishedAt, String url, boolean isFavorite,
                   String sourceName, String sourceId, List<String> tags,
                   int readTime, String language, String videoUrl, int views) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        this.author = author;
        this.publishedAt = publishedAt;
        this.url = url;
        this.isFavorite = isFavorite;
        this.sourceName = sourceName;
        this.sourceId = sourceId;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.readTime = readTime;
        this.language = language;
        this.videoUrl = videoUrl;
        this.views = views;
    }

    // ----------------------
    // Getters & Setters
    // ----------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public int getReadTime() { return readTime; }
    public void setReadTime(int readTime) { this.readTime = readTime; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    // ----------------------
    // Méthodes utilitaires
    // ----------------------

    public boolean isValidArticle() {
        return title != null && !title.isEmpty()
                && description != null && !description.isEmpty()
                && category != null && !category.isEmpty()
                && publishedAt != null && !publishedAt.isEmpty();
    }

    public Date getPublishedDate() {
        if (publishedAt == null) return null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(publishedAt);
        } catch (ParseException e) {
            return null;
        }
    }

    public String getFriendlyDate() {
        Date date = getPublishedDate();
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }

    // ----------------------
    // equals & hashCode (IMPORTANT)
    // ----------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ----------------------
    // toString (debug)
    // ----------------------
    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}