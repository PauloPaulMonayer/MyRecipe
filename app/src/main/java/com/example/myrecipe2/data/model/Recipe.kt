package com.example.myrecipe2.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "api_meal_id")
    val apiMealId: String? = null,

    @ColumnInfo(name = "recipe_title")
    val title: String,

    @ColumnInfo(name = "recipe_description")
    val description: String,

    @ColumnInfo(name = "recipe_image")
    val imageUri: String? = null,

    @ColumnInfo(name = "recipe_ingredients")
    val ingredients: String = "",

    @ColumnInfo(name = "recipe_directions")
    val directions: String = "",

    @ColumnInfo(name = "is_vegetarian")
    val isVegetarian: Boolean = false,

    @ColumnInfo(name = "is_vegan")
    val isVegan: Boolean = false,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)
