package com.mawumbo.mystoryapp.ui.detailstory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mawumbo.mystoryapp.databinding.FragmentDetailBinding
import com.mawumbo.mystoryapp.model.Story
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val story: Story? = arguments?.getParcelable("story")
        if (story != null) {
            Log.d("DetailFragment", "$story")
            displayDetail(story)
        }
    }

    private fun displayDetail(data: Story){
        binding.tvItemName.text = data.name
        binding.tvItemDescription.text = data.description
        Glide.with(requireContext())
            .load(data.photoUrl)
            .into(binding.ivItemPhoto)
    }
}