package com.example.news.API;

import com.example.news.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<News> getNews(
            @Query("country") String country ,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<News> getNewsSearch(
            @Query("q") String key ,
            @Query("language") String language,
            @Query("sortBy") String SortBy,
            @Query("apiKey") String apiKey
    );
}
