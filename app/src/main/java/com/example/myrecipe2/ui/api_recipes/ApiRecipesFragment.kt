package com.example.myrecipe2.ui.api_recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipe2.R
import com.example.myrecipe2.databinding.FragmentApiRecipesBinding
import com.example.myrecipe2.ui.recipe_list.RecipeAdapter
import com.example.myrecipe2.view_model.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApiRecipesFragment : Fragment() {

    private var _binding: FragmentApiRecipesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()

    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecipeAdapter(listOf(), object : RecipeAdapter.RecipeListener {
            override fun onRecipeClicked(index: Int) {
                val recipe = adapter.getRecipeAt(index)
                viewModel.selectRecipe(recipe)
                findNavController().navigate(R.id.action_apiRecipesFragment_to_recipeDetailsFragment)
            }

            override fun onRecipeLongClicked(index: Int) {
            }

            override fun onFavoriteClicked(index: Int) {
                val recipe = adapter.getRecipeAt(index)
                val newFavoriteState = !recipe.isFavorite

                val updatedRecipe = recipe.copy(isFavorite = newFavoriteState)
                adapter.updateSingleRecipe(index, updatedRecipe)


                viewModel.toggleFavorite(recipe)

                if (newFavoriteState) {
                    Toast.makeText(requireContext(),
                        getString(R.string.recipe_added_to_favorites), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.recipe_removed_from_favorites), Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.apiRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.apiRecyclerView.adapter = adapter


        viewModel.apiRecipes.observe(viewLifecycleOwner) { list ->
            binding.loadingCard.visibility = View.GONE
            adapter.updateRecipes(list)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.loadingCard.visibility = View.GONE
                Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_apiRecipesFragment_to_mainMenuFragment)
                viewModel.clearErrorMessage()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
