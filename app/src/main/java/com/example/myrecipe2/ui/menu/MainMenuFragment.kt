package com.example.myrecipe2.ui.menu

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myrecipe2.R
import com.example.myrecipe2.databinding.MenuBinding
import com.example.myrecipe2.view_model.RecipeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private var _binding: MenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineGranted || coarseGranted) {
                getCurrentLocationAndFetchRecipes()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuBinding.inflate(inflater, container, false)

        binding.button1.setOnClickListener { fetchRecipesByCategory("chicken") }
        binding.button2.setOnClickListener { fetchRecipesByCategory("beef") }
        binding.button3.setOnClickListener { fetchRecipesByCategory("vegetarian") }

        binding.button4.setOnClickListener {
            if (checkLocationPermission()) {
                getCurrentLocationAndFetchRecipes()
            } else {
                requestLocationPermission()
            }
        }

        binding.myRecipeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_recipesListFragment)
        }

        return binding.root
    }

    private fun fetchRecipesByCategory(category: String) {
        Toast.makeText(requireContext(),
            getString(R.string.loading_recipes, category), Toast.LENGTH_SHORT).show()
        viewModel.fetchRecipesByCategory(category)
        findNavController().navigate(R.id.action_mainMenuFragment_to_apiRecipesFragment)
    }

    private fun getCurrentLocationAndFetchRecipes() {
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude

                    getCountryName(lat, lng) { countryName ->
                        val mappedArea = countryName?.let { convertCountryToApiArea(it) }

                        requireActivity().runOnUiThread {
                            if (mappedArea != null) {
                                Toast.makeText(requireContext(), getString(R.string.detected_your_location_is, mappedArea), Toast.LENGTH_LONG).show()
                                viewModel.fetchRecipesByArea(mappedArea)
                            } else {
                                val randomArea = getRandomArea()
                                Snackbar.make(requireView(), getString(R.string.no_recipes_found_for, countryName), Snackbar.LENGTH_LONG).show()
                                viewModel.fetchRecipesByArea(randomArea)
                            }

                            findNavController().navigate(R.id.action_mainMenuFragment_to_apiRecipesFragment)
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), getString(R.string.location_is_null), Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addOnFailureListener {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        getString(R.string.failed_to_get_location, it.message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getCountryName(lat: Double, lng: Double, callback: (String?) -> Unit) {
        val randomArea = getRandomArea()
        if (!Geocoder.isPresent()) {
            requireActivity().runOnUiThread { Toast.makeText(requireContext(), getString(R.string.Geo_unsupported), Toast.LENGTH_LONG).show()
            }
            callback(randomArea)
            return
        }

        val geocoder = Geocoder(requireContext(), Locale.ENGLISH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                val countryName = addresses.firstOrNull()?.countryName
                requireActivity().runOnUiThread {
                    if (!countryName.isNullOrEmpty()) {
                        callback(countryName)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.could_not_determine_country), Toast.LENGTH_LONG).show()
                        callback(null)
                    }
                }
            }
        } else {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), getString(R.string.Geo_unsupported), Toast.LENGTH_LONG).show()
            }
            callback(randomArea)
        }
    }


    private fun convertCountryToApiArea(country: String): String? {
        val map = mapOf(
            "United States" to "American",
            "USA" to "American",
            "U.S." to "American",
            "United Kingdom" to "British",
            "England" to "British",
            "Scotland" to "British",
            "Wales" to "British",
            "Canada" to "Canadian",
            "China" to "Chinese",
            "India" to "Indian",
            "Italy" to "Italian",
            "Mexico" to "Mexican",
            "France" to "French",
            "Spain" to "Spanish",
            "Turkey" to "Turkish",
            "Vietnam" to "Vietnamese",
            "Egypt" to "Egyptian",
            "Croatia" to "Croatian",
            "Netherlands" to "Dutch",
            "Philippines" to "Filipino",
            "Greece" to "Greek",
            "Ireland" to "Irish",
            "Jamaica" to "Jamaican",
            "Japan" to "Japanese",
            "Kenya" to "Kenyan",
            "Malaysia" to "Malaysian",
            "Morocco" to "Moroccan",
            "Norway" to "Norwegian",
            "Poland" to "Polish",
            "Portugal" to "Portuguese",
            "Russia" to "Russian",
            "Thailand" to "Thai",
            "Tunisia" to "Tunisian",
            "Ukraine" to "Ukrainian",
            "Uruguay" to "Uruguayan"
        )
        return map[country]
    }

    private fun getRandomArea(): String {
        val allAreas = listOf(
            "American", "British", "Canadian", "Chinese", "Croatian", "Dutch", "Egyptian",
            "Filipino", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese",
            "Kenyan", "Malaysian", "Mexican", "Moroccan", "Norwegian", "Polish", "Portuguese",
            "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Ukrainian", "Uruguayan", "Vietnamese"
        )
        return allAreas.random()
    }

    private fun checkLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        return (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
