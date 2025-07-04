package com.example.onetap.api
        ;

import com.example.onetap.model.InfoGempa;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BmkgService {
    @GET("DataMKG/TEWS/gempaterkini.xml")
    Call<InfoGempa> getGempa();
}
