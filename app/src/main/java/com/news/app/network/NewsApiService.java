package com.news.app.network;

import com.news.app.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    // 🔥 Tout afficher
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines();

    // 🔥 Par catégorie
    @GET("top-headlines")
    Call<NewsResponse> getByCategory(
            @Query("category") String category
    );

    // 🔥 Recherche globale
    @GET("search")
    Call<NewsResponse> searchArticles(
            @Query("q") String query
    );

    // 🔥 Recherche + catégorie
    @GET("search")
    Call<NewsResponse> searchByCategory(
            @Query("q") String query,
            @Query("category") String category
    );
}
