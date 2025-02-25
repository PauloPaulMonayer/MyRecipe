package com.example.myrecipe2.data.remote

import com.example.myrecipe2.data.model.Recipe
import com.google.gson.annotations.SerializedName

data class ApiRecipe(
    @SerializedName("idMeal") val idMeal: String?,
    @SerializedName("strMeal") val strMeal: String?,
    @SerializedName("strCategory") val strCategory: String?,
    @SerializedName("strArea") val strArea: String?,
    @SerializedName("strInstructions") val strInstructions: String?,
    @SerializedName("strMealThumb") val strMealThumb: String?,
    @SerializedName("strTags") val strTags: String?,

    @SerializedName("strIngredient1") val strIngredient1: String?,
    @SerializedName("strIngredient2") val strIngredient2: String?,
    @SerializedName("strIngredient3") val strIngredient3: String?,
    @SerializedName("strIngredient4") val strIngredient4: String?,
    @SerializedName("strIngredient5") val strIngredient5: String?,
    @SerializedName("strIngredient6") val strIngredient6: String?,
    @SerializedName("strIngredient7") val strIngredient7: String?,
    @SerializedName("strIngredient8") val strIngredient8: String?,
    @SerializedName("strIngredient9") val strIngredient9: String?,
    @SerializedName("strIngredient10") val strIngredient10: String?,
    @SerializedName("strIngredient11") val strIngredient11: String?,
    @SerializedName("strIngredient12") val strIngredient12: String?,
    @SerializedName("strIngredient13") val strIngredient13: String?,
    @SerializedName("strIngredient14") val strIngredient14: String?,
    @SerializedName("strIngredient15") val strIngredient15: String?,
    @SerializedName("strIngredient16") val strIngredient16: String?,
    @SerializedName("strIngredient17") val strIngredient17: String?,
    @SerializedName("strIngredient18") val strIngredient18: String?,
    @SerializedName("strIngredient19") val strIngredient19: String?,
    @SerializedName("strIngredient20") val strIngredient20: String?,

    @SerializedName("strMeasure1") val strMeasure1: String?,
    @SerializedName("strMeasure2") val strMeasure2: String?,
    @SerializedName("strMeasure3") val strMeasure3: String?,
    @SerializedName("strMeasure4") val strMeasure4: String?,
    @SerializedName("strMeasure5") val strMeasure5: String?,
    @SerializedName("strMeasure6") val strMeasure6: String?,
    @SerializedName("strMeasure7") val strMeasure7: String?,
    @SerializedName("strMeasure8") val strMeasure8: String?,
    @SerializedName("strMeasure9") val strMeasure9: String?,
    @SerializedName("strMeasure10") val strMeasure10: String?,
    @SerializedName("strMeasure11") val strMeasure11: String?,
    @SerializedName("strMeasure12") val strMeasure12: String?,
    @SerializedName("strMeasure13") val strMeasure13: String?,
    @SerializedName("strMeasure14") val strMeasure14: String?,
    @SerializedName("strMeasure15") val strMeasure15: String?,
    @SerializedName("strMeasure16") val strMeasure16: String?,
    @SerializedName("strMeasure17") val strMeasure17: String?,
    @SerializedName("strMeasure18") val strMeasure18: String?,
    @SerializedName("strMeasure19") val strMeasure19: String?,
    @SerializedName("strMeasure20") val strMeasure20: String?
)


fun ApiRecipe.toRecipe(): Recipe {

    val ingredientsList = mutableListOf<String>()

    fun addIngredient(ingredient: String?, measure: String?) {
        if (!ingredient.isNullOrBlank()) {
            if (!measure.isNullOrBlank()) {
                ingredientsList.add("• $ingredient - $measure")
            } else {
                ingredientsList.add("• $ingredient")
            }
        }
    }


    addIngredient(strIngredient1, strMeasure1)
    addIngredient(strIngredient2, strMeasure2)
    addIngredient(strIngredient3, strMeasure3)
    addIngredient(strIngredient4, strMeasure4)
    addIngredient(strIngredient5, strMeasure5)
    addIngredient(strIngredient6, strMeasure6)
    addIngredient(strIngredient7, strMeasure7)
    addIngredient(strIngredient8, strMeasure8)
    addIngredient(strIngredient9, strMeasure9)
    addIngredient(strIngredient10, strMeasure10)
    addIngredient(strIngredient11, strMeasure11)
    addIngredient(strIngredient12, strMeasure12)
    addIngredient(strIngredient13, strMeasure13)
    addIngredient(strIngredient14, strMeasure14)
    addIngredient(strIngredient15, strMeasure15)
    addIngredient(strIngredient16, strMeasure16)
    addIngredient(strIngredient17, strMeasure17)
    addIngredient(strIngredient18, strMeasure18)
    addIngredient(strIngredient19, strMeasure19)
    addIngredient(strIngredient20, strMeasure20)

    val ingredientsText = ingredientsList.joinToString("\n")


    val directionsText = strInstructions ?: ""

    val categoryLower = strCategory?.lowercase() ?: ""
    val tagsLower = strTags?.lowercase() ?: ""
    val isVegetarian = categoryLower.contains("vegetarian") || tagsLower.contains("vegetarian")
    val isVegan = categoryLower.contains("vegan") || tagsLower.contains("vegan")

    val shortDescription = directionsText.take(80) + if (directionsText.length > 80) "..." else ""

    return Recipe(
        id = 0,
        apiMealId = idMeal,
        title = strMeal ?: "No Title",
        description = shortDescription,
        imageUri = strMealThumb ?: "",
        ingredients = ingredientsText,
        directions = directionsText,
        isVegetarian = isVegetarian,
        isVegan = isVegan
    )
}
