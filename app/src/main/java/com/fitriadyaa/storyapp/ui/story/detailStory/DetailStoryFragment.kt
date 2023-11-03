package com.fitriadyaa.storyapp.ui.story.detailStory

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        val name = arguments?.getString("name")
        val description = arguments?.getString("description")
        val createdAt = arguments?.getString("created_at")
        val photoUrl = arguments?.getString("photo_url")

        Log.d("DetailStoryFragment", "Name: $name")
        Log.d("DetailStoryFragment", "Description: $description")
        Log.d("DetailStoryFragment", "CreatedAt: $createdAt")
        Log.d("DetailStoryFragment", "PhotoUrl: $photoUrl")

        binding.tvTitle.text = name
        binding.tvDesc.text = description

        try {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(createdAt?: "")
                ?.let { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it).toString() }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
