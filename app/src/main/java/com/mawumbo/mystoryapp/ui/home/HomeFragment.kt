package com.mawumbo.mystoryapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mawumbo.mystoryapp.R
import com.mawumbo.mystoryapp.adapter.StoryListAdapter
import com.mawumbo.mystoryapp.databinding.FragmentHomeBinding
import com.mawumbo.mystoryapp.model.Story
import com.mawumbo.mystoryapp.ui.detailstory.DetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var storyList: List<Story>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StoryListAdapter(onClick = { storyId ->
            val selectedStory = getDataById(storyId)
            if (selectedStory != null) {
                toDetailStory(selectedStory)
            }
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager

        binding.rvStories.adapter = adapter

        viewModel.refreshStories()

        viewModel.isLoading.observe(viewLifecycleOwner) {
            isLoading(it)
        }

        viewModel.allStory.observe(viewLifecycleOwner) {storyData ->
            Log.d("Home", "$storyData")
            storyList = storyData
            adapter.submitList(storyData)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sign_out -> {
                    viewModel.logout()
                    val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                    findNavController().navigate(action)
                    Toast.makeText(
                        requireContext(),
                        "Logout Succeed",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddStoryFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getDataById(storyId: String?): Story? {
        return storyList.find { it.id == storyId }
    }

    private fun toDetailStory(storyId: Story) {
        val bundle = Bundle()
        bundle.putParcelable("story", storyId)

        val detailFragment = DetailFragment()
        detailFragment.arguments = bundle

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.container, detailFragment)
        transaction.addToBackStack(null)
        transaction.commit()
//        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(storyId)
//        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}