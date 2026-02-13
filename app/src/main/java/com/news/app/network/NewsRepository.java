package com.news.app.network;

import com.news.app.network.NewsResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsRepository {

    private static final String BASE_URL = "https://newsapi.org/v2/";
    private final NewsApiService apiService;

    // Constructeur
    public NewsRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(NewsApiService.class);
    }

    // Récupérer les top headlines
    public Call<NewsResponse> getTopHeadlines(String country, String category, String apiKey) {
        return apiService.getTopHeadlines(country, category, apiKey);
    }

    // Rechercher des articles par mot-clé
    public Call<NewsResponse> searchArticles(String query, String language, String apiKey) {
        return apiService.searchArticles(query, language, apiKey);
    }

    // 🔹 Interface Retrofit
    public interface NewsApiService {

        @GET("top-headlines")
        Call<NewsResponse> getTopHeadlines(
                @Query("country") String country,
                @Query("category") String category,
                @Query("apiKey") String apiKey
        );

        @GET("everything")
        Call<NewsResponse> searchArticles(
                @Query("q") String query,
                @Query("language") String language,
                @Query("apiKey") String apiKey
        );
    }
}