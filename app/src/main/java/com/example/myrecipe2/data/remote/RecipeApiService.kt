package com.example.myrecipe2.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    @GET("filter.php")
    suspend fun filterByArea(@Query("a") area: String): ApiRecipeResponse

    @GET("lookup.php")
    suspend fun lookupMeal(@Query("i") mealId: String): ApiRecipeResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): ApiRecipeResponse

}
