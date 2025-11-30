package com.example.moneytracker;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExchangeRateApi {
    @GET("v4/latest/USD")
    Call<ExchangeRateResponse> getRates();
}