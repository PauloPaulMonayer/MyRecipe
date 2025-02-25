package com.example.myrecipe2.ui.add_recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myrecipe2.R
import com.example.myrecipe2.data.model.Recipe
import com.example.myrecipe2.databinding.AddRecipeBinding
import com.example.myrecipe2.view_model.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipeFragment : Fragment() {
    private var _binding: AddRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val updatedRecipe = viewModel.recipeLiveData.value?.copy(imageUri = uri.toString())
                viewModel.recipeLiveData.value = updatedRecipe

                Glide.with(this).load(uri).centerCrop().into(binding.recipeImageInput)
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddRecipeBinding.inflate(inflater, container, false)

        binding.saveRecipeButton.setOnClickListener { saveRecipe() }
        binding.recipeImageInput.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }

        setupBulletInput(binding.ingredientsInput)
        setupBulletInput(binding.directionsInput)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, imeInsets.bottom)
            insets
        }
    }

    private fun observeLiveData() {
        viewModel.recipeLiveData.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                if (binding.recipeTitleInput.text.isEmpty()) {
                    binding.recipeTitleInput.setText(it.title)
                }
                if (binding.recipeDescriptionInput.text.isEmpty()) {
                    binding.recipeDescriptionInput.setText(it.description)
                }
                if (binding.ingredientsInput.text.isEmpty()) {
                    binding.ingredientsInput.setText(it.ingredients)
                }
                if (binding.directionsInput.text.isEmpty()) {
                    binding.directionsInput.setText(it.directions)
                }
                binding.vegetarianCheckboxInput.isChecked = it.isVegetarian
                binding.veganCheckboxInput.isChecked = it.isVegan

                val imageUri = it.imageUri?.takeIf { uri -> uri.isNotBlank() }?.let { uri -> Uri.parse(uri) }
                Glide.with(this)
                    .load(imageUri ?: R.drawable.uploadd_image)
                    .centerCrop()
                    .into(binding.recipeImageInput)
            }
        }
    }

    private fun setupBulletInput(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    val lines = text.split("\n")
                    val formattedText = lines.joinToString("\n") { line ->
                        if (line.isNotBlank() && !line.trim().startsWith("•")) {
                            "• ${line.trim()}"
                        } else {
                            line
                        }
                    }
                    if (formattedText != text) {
                        editText.removeTextChangedListener(this)
                        editText.setText(formattedText)
                        editText.setSelection(editText.text.length)
                        editText.addTextChangedListener(this)
                    }
                }
            }
        })
    }

    private fun saveRecipe() {
        val title = binding.recipeTitleInput.text.toString().trim()
        val description = binding.recipeDescriptionInput.text.toString().trim()
        val ingredients = binding.ingredientsInput.text.toString().trim()
        val directions = binding.directionsInput.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty() || directions.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val updatedRecipe = viewModel.recipeLiveData.value?.copy(
            title = title,
            description = description,
            ingredients = ingredients,
            directions = directions,
            isVegetarian = binding.vegetarianCheckboxInput.isChecked,
            isVegan = binding.veganCheckboxInput.isChecked
        )
        viewModel.recipeLiveData.value = updatedRecipe

        if (viewModel.saveRecipe()) {
            Toast.makeText(requireContext(), getString(R.string.recipe_saved_successfully), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addRecipeFragment_to_recipesListFragment)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.error_saving_recipe), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        val currentRecipe = viewModel.recipeLiveData.value ?: Recipe(
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
        val updatedRecipe = currentRecipe.copy(
            title = binding.recipeTitleInput.text.toString(),
            description = binding.recipeDescriptionInput.text.toString(),
            ingredients = binding.ingredientsInput.text.toString(),
            directions = binding.directionsInput.text.toString(),
            isVegetarian = binding.vegetarianCheckboxInput.isChecked,
            isVegan = binding.veganCheckboxInput.isChecked
        )
        viewModel.recipeLiveData.value = updatedRecipe
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
