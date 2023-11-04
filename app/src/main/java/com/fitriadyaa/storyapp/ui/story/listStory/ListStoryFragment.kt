package com.fitriadyaa.storyapp.ui.story.listStory

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.databinding.FragmentListStoryBinding
import com.fitriadyaa.storyapp.utils.ViewModelFactory

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StoryAdapter
    private val listStoryViewModel: ListStoryViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding.progressBar.visibility = View.VISIBLE

        listStoryViewModel.stories.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                adapter.submitData(viewLifecycleOwner.lifecycle , data)
            }
        }

        setupAdapter()
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.createStoryFragment)
        }
        onBackPressed()

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvStory.layoutManager = layoutManager

        adapter = StoryAdapter { story, _, _, _, _ ->
            val action = ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(
                id = story.id,
                name = story.name,
                description = story.description,
                photoUrl = story.photoUrl,
                createdAt = story.createdAt
            )
            Log.d("ListStoryFragment", "CreatedAt: ${story.createdAt}")
            findNavController().navigate(action)
        }

        binding.rvStory.adapter = adapter
        binding.rvStory.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
