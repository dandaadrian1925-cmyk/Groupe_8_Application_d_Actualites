package com.news.app.network;

import retrofit2.Call;

public class NewsRepository {

    private final NewsApiService apiService;

    // Constructeur
    public NewsRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // Top headlines
    public Call<NewsResponse> getTopHeadlines(String country, String category, String apiKey) {
        return apiService.getTopHeadlines(country, category, apiKey);
    }

    // Recherche
    public Call<NewsResponse> searchArticles(String query, String language, String apiKey) {
        return apiService.searchArticles(query, language, apiKey);
    }
}