package com.example.myrecipe2.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myrecipe2.R
import com.example.myrecipe2.data.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Recipe::class], version = 3, exportSchema = false)
abstract class RecipDataBase : RoomDatabase() {

    abstract fun recipeDao(): RecipDao

    class SeedDatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.recipeDao()?.insertRecipes(
                    listOf(
                        Recipe(
                            title = context.getString(R.string.pasta_with_sweet_potato_sauce),
                            description = context.getString(R.string.pasta_description),
                            ingredients = context.getString(R.string.pasta_ingredients),
                            directions = context.getString(R.string.pasta_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/pasta_batata",
                            isVegetarian = true,
                            isVegan = false
                        ),
                        Recipe(
                            title = context.getString(R.string.beef_stroganoff),
                            description = context.getString(R.string.beef_stroganoff_description),
                            ingredients = context.getString(R.string.beef_stroganoff_ingredients),
                            directions = context.getString(R.string.beef_stroganoff_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/beef_stroganoff",
                            isVegetarian = false,
                            isVegan = false
                        ),
                        Recipe(
                            title = context.getString(R.string.chicken_curry),
                            description = context.getString(R.string.chicken_curry_description),
                            ingredients = context.getString(R.string.chicken_curry_ingredients),
                            directions = context.getString(R.string.chicken_curry_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/chicken_curry",
                            isVegetarian = false,
                            isVegan = false
                        ),
                        Recipe(
                            title = context.getString(R.string.cheese_lasagna),
                            description = context.getString(R.string.cheese_lasagna_description),
                            ingredients = context.getString(R.string.cheese_lasagna_ingredients),
                            directions = context.getString(R.string.cheese_lasagna_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/cheese_lasagna",
                            isVegetarian = true,
                            isVegan = false
                        ),
                        Recipe(
                            title = context.getString(R.string.fettuccine_alfredo),
                            description = context.getString(R.string.fettuccine_alfredo_description),
                            ingredients = context.getString(R.string.fettuccine_alfredo_ingredients),
                            directions = context.getString(R.string.fettuccine_alfredo_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/fettuccine_alfredo",
                            isVegetarian = true,
                            isVegan = false
                        ),
                        Recipe(
                            title = context.getString(R.string.home_fries),
                            description = context.getString(R.string.home_fries_description),
                            ingredients = context.getString(R.string.home_fries_ingredients),
                            directions = context.getString(R.string.home_fries_directions),
                            imageUri = "android.resource://com.example.myrecipe2/drawable/home_fries",
                            isVegetarian = true,
                            isVegan = true
                        )
                    )
                )
            }
        }
    }

    companion object {
        @Volatile
        var INSTANCE: RecipDataBase? = null
    }
}
