package com.news.app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 🌍 URL LocalTunnel (backend exposé sur Internet)
    private static final String BASE_URL =
    "https://cekiqtkdgjgawxxerjdf.supabase.co/functions/v1/news-api/";



    private static Retrofit retrofit = null;

    public static NewsApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NewsApiService.class);
    }
}
