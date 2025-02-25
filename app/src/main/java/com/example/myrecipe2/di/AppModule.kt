package com.example.myrecipe2.di

import android.app.Application
import androidx.room.Room
import com.example.myrecipe2.data.local_db.RecipDataBase
import com.example.myrecipe2.data.local_db.RecipDao
import com.example.myrecipe2.data.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): RecipDataBase {
        val db = Room.databaseBuilder(
            app,
            RecipDataBase::class.java,
            "recipe_db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(RecipDataBase.SeedDatabaseCallback(app))
            .build()

        RecipDataBase.INSTANCE = db
        return db
    }

    @Singleton
    @Provides
    fun provideRecipeDao(db: RecipDataBase): RecipDao {
        return db.recipeDao()
    }

    @Singleton
    @Provides
    fun provideRecipeRepository(recipeDao: RecipDao): RecipeRepository {
        return RecipeRepository(recipeDao)
    }
}
