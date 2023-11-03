import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.data.Result

class ListStoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>>
        get() = _stories

    fun fetchStories(page: Int, size: Int) {
        repositoryStory.getStories(page, size).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _stories.postValue(result.data.listStory)
                }
                is Result.Error -> {
                    // Handle the error appropriately, e.g., show an error message
                }
                is Result.Loading -> {
                    // Handle the loading state, e.g., show a loading indicator
                }
            }
        }
    }
}
