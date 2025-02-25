package com.example.myrecipe2.ui.recipe_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipe2.R
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.databinding.AllRecipesBinding
import com.example.myrecipe2.view_model.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesListFragment : Fragment() {

    private var _binding: AllRecipesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AllRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addRecipeButton.setOnClickListener {
            viewModel.clearRecipeForm()
            findNavController().navigate(R.id.action_recipesListFragment_to_addRecipeFragment)
        }

        binding.infoBtn.setOnClickListener {
            showInfoDialog()
        }

        adapter = RecipeAdapter(listOf(), object : RecipeAdapter.RecipeListener {
            override fun onRecipeClicked(index: Int) {
                val selectedRecipe = adapter.getRecipeAt(index)
                val bundle = Bundle().apply {
                    putInt("recipe_id", selectedRecipe.id)
                }
                findNavController().navigate(
                    R.id.action_recipesListFragment_to_recipeDetailsFragment,
                    bundle
                )
            }

            override fun onRecipeLongClicked(index: Int) {
                val recipe = adapter.getRecipeAt(index)
                showDeleteConfirmationDialog(recipe, index)
            }

            override fun onFavoriteClicked(index: Int) {
                val recipe = adapter.getRecipeAt(index)
                viewModel.toggleFavorite(recipe)
                if(recipe.isFavorite){
                    Toast.makeText(requireContext(), getString(R.string.recipe_removed_from_favorites),Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.toggled_favorite_for),Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.recipesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecycler.adapter = adapter

        viewModel.allRecipes.observe(viewLifecycleOwner) {
            filterRecipes()
        }

        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRecipes()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.vegetarianCheckbox.setOnCheckedChangeListener { _, _ ->
            filterRecipes()
        }
        binding.veganCheckbox.setOnCheckedChangeListener { _, _ ->
            filterRecipes()
        }
        binding.favoritesCheckbox.setOnCheckedChangeListener { _, _ ->
            filterRecipes()
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = adapter.getRecipeAt(position)
                showDeleteConfirmationDialog(recipe, position)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recipesRecycler)
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.my_recipes_help))
            .setMessage(getString(R.string.info_app))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun filterRecipes() {
        val query = binding.searchBox.text.toString().trim()
        val isVegetarian = binding.vegetarianCheckbox.isChecked
        val isVegan = binding.veganCheckbox.isChecked
        val isFavoriteFilter = binding.favoritesCheckbox.isChecked

        val localRecipes = viewModel.allRecipes.value ?: emptyList()

        val filtered = localRecipes.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true) &&
                    (!isVegetarian || recipe.isVegetarian) &&
                    (!isVegan || recipe.isVegan) &&
                    (!isFavoriteFilter || recipe.isFavorite)
        }

        adapter.updateRecipes(filtered)
    }

    private fun showDeleteConfirmationDialog(recipe: Recipe, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_recipe))
            .setMessage(getString(R.string.are_you_sure_you_want_to_remove_the_recipe))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteRecipe(recipe)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.recipe_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
                adapter.notifyItemChanged(position)
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
