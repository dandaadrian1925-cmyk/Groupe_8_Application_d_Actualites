package com.news.app.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    // Top headlines
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    // Recherche par mot-clé
    @GET("everything")
    Call<NewsResponse> searchArticles(
            @Query("q") String query,
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );
}