package com.mawumbo.mystoryapp.ui.detailstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mawumbo.mystoryapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is DetailStoryUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is DetailStoryUiState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.tvItemName.text = uiState.story.name
                    binding.tvItemDescription.text = uiState.story.description
                    Glide.with(requireContext())
                        .load(uiState.story.photoUrl)
                        .into(binding.ivItemPhoto)
                }
                is DetailStoryUiState.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Snackbar.make(
                        requireView(),
                        uiState.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}