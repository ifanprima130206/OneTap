package com.example.onetap.api;

import com.example.onetap.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // Ambil berita terbaru dari satu sumber (misalnya: cnn/terbaru, tempo/terbaru)
    @GET("{source}/terbaru")
    Call<NewsResponse> getLatestNewsBySource(
            @Path("source") String source
    );

    // Ambil berita berdasarkan kategori dari satu sumber (misal: cnn/kategori/teknologi)
    @GET("{source}/{category}")
    Call<NewsResponse> getNewsByCategory(
            @Path("source") String source,
            @Path("category") String category
    );

    // (Opsional) Ambil berita trending dari suatu sumber, jika ada endpoint-nya
    @GET("{source}/trending")
    Call<NewsResponse> getTrendingNews(
            @Path("source") String source
    );
}
