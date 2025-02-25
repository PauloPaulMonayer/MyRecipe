package com.example.myrecipe2.data.remote

import com.google.gson.annotations.SerializedName

data class ApiRecipeResponse(
    @SerializedName("meals")
    val meals: List<ApiRecipe>?
)
