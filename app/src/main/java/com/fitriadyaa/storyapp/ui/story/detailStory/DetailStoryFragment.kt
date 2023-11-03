package com.fitriadyaa.storyapp.ui.story.detailStory

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.imageLoader
import coil.request.ImageRequest
import com.fitriadyaa.storyapp.databinding.FragmentDetailStoryBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        val name = arguments?.getString("name")
        val description = arguments?.getString("description")
        val createdAt = arguments?.getString("createdAt")
        val photoUrl = arguments?.getString("photo_url")

        Log.d("DetailStoryFragment", "Name: $name")
        Log.d("DetailStoryFragment", "Description: $description")
        Log.d("DetailStoryFragment", "CreatedAt: $createdAt")
        Log.d("DetailStoryFragment", "PhotoUrl: $photoUrl")

        binding.tvTitle.text = name
        binding.tvDesc.text = description

        try {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(createdAt?.toString() ?: ""))
            binding.tvDate.text = formattedDate
        } catch (e: ParseException) {
            e.printStackTrace()
            binding.tvDate.text = "Invalid Date"
        }

        if (!photoUrl.isNullOrEmpty()) {
            val request = ImageRequest.Builder(requireContext())
                .data(photoUrl)
                .target(
                    onSuccess = { result ->
                        binding.ivStory.setImageDrawable(result)
                        startPostponedEnterTransition()
                    },
                    onError = { _ ->
                        startPostponedEnterTransition()
                    }
                )
                .build()

            requireActivity().application.imageLoader.enqueue(request)
        } else {
            startPostponedEnterTransition()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            findNavController().popBackStack()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








