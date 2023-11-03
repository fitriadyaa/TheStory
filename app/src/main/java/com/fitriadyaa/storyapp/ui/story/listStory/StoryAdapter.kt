package com.fitriadyaa.storyapp.ui.story.listStory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.databinding.CardItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter(
    private val callback: (story: Story, imageView: View, nameView: View, descView: View, createAtView : View) -> Unit
) : ListAdapter<Story, StoriesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, callback)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id
        }
    }
}

class StoriesViewHolder(private val binding: CardItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: Story?,
        callback: (story: Story, imageView: View, nameView: View, descView: View, createAtView: View) -> Unit
    ) {
        item?.let { story ->
            binding.tvTitle.text = story.name
            binding.tvDesc.text = story.description

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            var formattedDate = "Invalid Date"

            if (story.createdAt.isNotBlank()) {
                try {
                    val date =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(story.createdAt)
                    formattedDate = date?.let { dateFormat.format(it) }.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.tvDate.text = formattedDate

            val drawable = CircularProgressDrawable(binding.root.context)
            drawable.strokeWidth = 5f
            drawable.centerRadius = 30f
            drawable.start()

            binding.ivStory.load(story.photoUrl) {
                placeholder(drawable)
                allowHardware(false)
            }

            binding.root.setOnClickListener {
                callback.invoke(
                    story,
                    binding.ivStory,
                    binding.tvTitle,
                    binding.tvDesc,
                    binding.tvDate
                )
            }
        }
    }
}