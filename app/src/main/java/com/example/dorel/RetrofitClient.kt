package com.example.dorel

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Data class for the API response
data class CurrencyResponse(
    val conversion_rates: Map<String, Double>
)

interface CurrencyApi {
    @GET("latest/{base_currency}")
    fun getExchangeRates(@Path("base_currency") baseCurrency: String): retrofit2.Call<CurrencyResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/64ae0e306408e1d29e7c81d9/"

    val instance: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}
