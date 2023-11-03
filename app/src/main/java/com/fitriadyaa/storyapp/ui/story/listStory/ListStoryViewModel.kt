package com.fitriadyaa.storyapp.ui.story.listStory

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.data.Result

class ListStoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>>
        get() = _stories

    @SuppressLint("StringFormatInvalid")
    fun fetchStories(page: Int, size: Int, context: Context) {
        repositoryStory.getStories(page, size).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _stories.postValue(result.data.listStory)
                    showToast(context, context.getString(R.string.data_success))
                }
                is Result.Error -> {
                    showToast(context, context.getString(R.string.data_error, result.error))
                }
                is Result.Loading -> {
                    showToast(context, context.getString(R.string.data_loading))
                }
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
