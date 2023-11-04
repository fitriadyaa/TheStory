package com.fitriadyaa.storyapp.ui.story.listStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.utils.DataDummy
import com.fitriadyaa.storyapp.utils.MainDispatcherRule
import com.fitriadyaa.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private val dummyStoriesResponse = DataDummy.generateDummyStory(10)

    @Test
    fun `when getStories Should Not Null and Return Success`() = runTest {
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(repositoryStory.getStories()).thenReturn(expectedStories)

        val listStoryViewModel = ListStoryViewModel(repositoryStory)
        val actualStories: PagingData<Story> = listStoryViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStoriesResponse.listStory, differ.snapshot().items)
        Assert.assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().items.size)
        Assert.assertEquals(dummyStoriesResponse.listStory[0].id, differ.snapshot().items[0]?.id)
    }

}

class StoryPagingSource : PagingSource<Int, List<Story>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, List<Story>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, List<Story>> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}