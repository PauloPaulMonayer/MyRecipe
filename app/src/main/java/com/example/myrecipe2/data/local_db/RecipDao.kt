package com.example.myrecipe2.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myrecipe2.data.model.Recipe

@Dao
interface RecipDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipes(recipes: List<Recipe>)

    @Delete
    fun deleteRecipe(vararg recipes: Recipe)

    @Update
    fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe ORDER BY recipe_title ASC")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id = :id LIMIT 1")
    fun getRecipeById(id: Int): LiveData<Recipe>

    @Query("SELECT * FROM recipe WHERE recipe_title = :title LIMIT 1")
    fun getRecipeByTitle(title: String): Recipe?

    @Query("SELECT * FROM recipe WHERE is_favorite = 1 ORDER BY recipe_title ASC")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE api_meal_id = :mealId LIMIT 1")
    fun getRecipeByApiMealId(mealId: String): Recipe?
}
