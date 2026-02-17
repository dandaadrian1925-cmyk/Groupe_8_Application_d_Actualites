package com.news.app.network;

import com.news.app.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines();

    @GET("search")
    Call<NewsResponse> searchArticles(@Query("q") String query);
}
