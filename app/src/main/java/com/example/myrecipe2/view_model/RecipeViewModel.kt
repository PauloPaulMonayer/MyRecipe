package com.example.myrecipe2.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.data.repository.RecipeRepository
import com.example.myrecipe2.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    val allRecipes: LiveData<List<Recipe>> = repository.getAllRecipes()

    private val _selectedRecipe = MutableLiveData<Recipe?>()
    val selectedRecipe: LiveData<Recipe?> get() = _selectedRecipe

    val recipeLiveData = MutableLiveData<Recipe?>()

    private val _apiRecipes = MutableLiveData<List<Recipe>>()
    val apiRecipes: LiveData<List<Recipe>> get() = _apiRecipes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun selectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
        recipeLiveData.value = recipe
    }

    fun getRecipeById(id: Int) {
        repository.getRecipeById(id).observeForever { recipe ->
            _selectedRecipe.value = recipe
            recipeLiveData.value = recipe
        }
    }

    fun clearRecipeForm() {
        recipeLiveData.value = Recipe(
            id = 0,
            title = "",
            description = "",
            ingredients = "",
            directions = "",
            imageUri = "",
            isVegetarian = false,
            isVegan = false,
            isFavorite = false
        )
    }

    fun saveRecipe(): Boolean {
        val recipe = recipeLiveData.value ?: return false
        if (recipe.title.isBlank() ||
            recipe.description.isBlank() ||
            recipe.ingredients.isBlank() ||
            recipe.directions.isBlank()
        ) {
            return false
        }

        viewModelScope.launch {
            if (recipe.id == 0) {
                repository.insertRecipe(recipe)
            } else {
                repository.updateRecipe(recipe)
            }
        }
        return true
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun fetchRecipesByCategory(category: String) {
        viewModelScope.launch {
            if (!networkHelper.isNetworkAvailable()) {
                _errorMessage.value = "No internet connection"
                return@launch
            }
            _errorMessage.value = null
            _isLoading.postValue(true)
            try {
                val recipes = repository.searchRecipesByCategory(category)
                if (!recipes.isNullOrEmpty()) {
                    _apiRecipes.value = recipes!!
                } else {
                    _apiRecipes.value = emptyList()
                }
            } catch (e: Exception) {
                _apiRecipes.value = emptyList()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            if (recipe.isFavorite) {
                repository.updateRecipe(recipe.copy(isFavorite = false))
                viewModelScope.launch(Dispatchers.Main) {
                    _apiRecipes.value = _apiRecipes.value?.map {
                        if (it.title == recipe.title) it.copy(isFavorite = false) else it
                    }
                }
            } else {
                repository.insertRecipe(recipe.copy(isFavorite = true))
                viewModelScope.launch(Dispatchers.Main) {
                    _apiRecipes.value = _apiRecipes.value?.map {
                        if (it.title == recipe.title) it.copy(isFavorite = true) else it
                    }
                }
            }
        }
    }

    fun fetchRecipesByArea(area: String) {
        viewModelScope.launch {
            if (!networkHelper.isNetworkAvailable()) {
                _errorMessage.value = "No internet connection"
                return@launch
            }
            _errorMessage.value = null
            _isLoading.postValue(true)
            try {
                val response = repository.filterRecipesByArea(area)
                if (!response.isNullOrEmpty()) {
                    _apiRecipes.postValue(response!!)
                } else {
                    _apiRecipes.postValue(emptyList())
                }
            } catch (e: Exception) {
                _apiRecipes.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
