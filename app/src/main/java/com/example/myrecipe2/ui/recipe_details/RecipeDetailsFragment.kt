package com.example.myrecipe2.ui.recipe_details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myrecipe2.R
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.databinding.RecipeDetailsBinding
import com.example.myrecipe2.view_model.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment() {

    private var _binding: RecipeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RecipeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipe_id", -1) ?: -1
        if (recipeId != -1) {
            viewModel.getRecipeById(recipeId)
        }

        observeLiveData()

        binding.btnReturn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.editBtn.setOnClickListener {
            viewModel.selectRecipe(viewModel.selectedRecipe.value)
            findNavController().navigate(R.id.action_recipeDetailsFragment_to_addRecipeFragment)
        }

        binding.favoriteDetailsBtn.setOnClickListener {
            val current = viewModel.selectedRecipe.value ?: return@setOnClickListener
            if (current.isFavorite) {
                binding.favoriteDetailsBtn.setImageResource(R.drawable.ic_baseline_star_border_24)
                viewModel.toggleFavorite(current)
                Toast.makeText(requireContext(), R.string.recipe_removed_from_favorites, Toast.LENGTH_SHORT).show()
            } else {
                binding.favoriteDetailsBtn.setImageResource(R.drawable.ic_baseline_star)
                viewModel.toggleFavorite(current)
                Toast.makeText(requireContext(), R.string.recipe_added_to_favorites, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLiveData() {
        viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let { bindRecipeData(it) }
        }
    }

    private fun bindRecipeData(recipe: Recipe) {
        binding.recipeTitle.text = recipe.title
        binding.recipeDescription.text = recipe.description
        binding.ingredientsList.text = recipe.ingredients
        binding.directionsList.text = recipe.directions
        binding.vegetarianCheckboxDetail.isChecked = recipe.isVegetarian
        binding.veganCheckboxDetail.isChecked = recipe.isVegan

        val imageUri = recipe.imageUri?.let { Uri.parse(it) }
        Glide.with(this)
            .load(imageUri ?: R.drawable.uploadd_image)
            .centerCrop()
            .into(binding.recipeImage)

        val iconRes = if (recipe.isFavorite) {
            R.drawable.ic_baseline_star
        } else {
            R.drawable.ic_baseline_star_border_24
        }
        binding.favoriteDetailsBtn.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
