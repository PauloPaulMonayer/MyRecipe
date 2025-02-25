package com.example.myrecipe2.ui.recipe_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myrecipe2.R
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.databinding.RecipeItemBinding

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val callback: RecipeListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    interface RecipeListener {
        fun onRecipeClicked(index: Int)
        fun onRecipeLongClicked(index: Int)
        fun onFavoriteClicked(index: Int)
    }

    fun getRecipeAt(position: Int): Recipe = recipes[position]

    fun updateRecipes(newRecipes: List<Recipe>) {
        val diffCallback = RecipeDiffCallback(recipes, newRecipes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        recipes = newRecipes
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateSingleRecipe(position: Int, newRecipe: Recipe) {
        val mutableList = recipes.toMutableList()
        mutableList[position] = newRecipe
        recipes = mutableList
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    inner class RecipeViewHolder(
        private val binding: RecipeItemBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
            binding.favoriteBtn.setOnClickListener {
                callback.onFavoriteClicked(adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            callback.onRecipeClicked(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callback.onRecipeLongClicked(adapterPosition)
            return true
        }

        fun bind(recipe: Recipe) {
            binding.recipeNameCard.text = recipe.title
            binding.recipeDescriptionCard.text = recipe.description

            Glide.with(binding.root).load(recipe.imageUri ?: "android.resource://com.example.myrecipe2/drawable/baseline_emoji_food_beverage_24")
                .circleCrop()
                .into(binding.recipeImageCard)

            val iconRes = if (recipe.isFavorite) {
                R.drawable.ic_baseline_star
            } else {
                R.drawable.ic_baseline_star_border_24
            }
            binding.favoriteBtn.setImageResource(iconRes)
        }
    }
}
