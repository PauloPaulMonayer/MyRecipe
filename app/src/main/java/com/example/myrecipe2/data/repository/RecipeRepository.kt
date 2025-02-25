package com.example.myrecipe2.data.repository

import androidx.lifecycle.LiveData
import com.example.myrecipe2.data.local_db.RecipDao
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.data.remote.RetrofitInstance
import com.example.myrecipe2.data.remote.toRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipDao
) {

    fun getAllRecipes(): LiveData<List<Recipe>> = recipeDao.getAllRecipes()
    fun getRecipeById(id: Int): LiveData<Recipe> = recipeDao.getRecipeById(id)
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes()

    suspend fun insertRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.insertRecipe(recipe)
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.updateRecipe(recipe)
        }
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.deleteRecipe(recipe)
        }
    }

    suspend fun searchRecipesByCategory(category: String): List<Recipe>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.filterByCategory(category)
                val partials = response.meals?.map { it.toRecipe() } ?: emptyList()

                val fullRecipes = partials.mapNotNull { partial ->
                    val mealId = partial.apiMealId
                    if (!mealId.isNullOrBlank()) {
                        val lookupResponse = RetrofitInstance.api.lookupMeal(mealId)
                        val fullApiRecipe = lookupResponse.meals?.firstOrNull()
                        fullApiRecipe?.toRecipe() ?: partial
                    } else {
                        partial
                    }
                }
                fullRecipes
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun filterRecipesByArea(area: String): List<Recipe>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.filterByArea(area)
                val partialRecipes = response.meals?.map { it.toRecipe() } ?: emptyList()

                val fullRecipes = partialRecipes.mapNotNull { partial ->
                    val mealId = partial.apiMealId
                    if (!mealId.isNullOrBlank()) {
                        val lookupResponse = RetrofitInstance.api.lookupMeal(mealId)
                        val fullApiRecipe = lookupResponse.meals?.firstOrNull()
                        fullApiRecipe?.toRecipe() ?: partial
                    } else {
                        partial
                    }
                }

                fullRecipes
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
