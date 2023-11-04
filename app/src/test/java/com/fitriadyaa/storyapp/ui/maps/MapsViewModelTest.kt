package com.fitriadyaa.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryResponse
import com.fitriadyaa.storyapp.utils.DataDummy
import com.fitriadyaa.storyapp.utils.getOrAwaitValue
import com.fitriadyaa.storyapp.data.Result
import org.junit.jupiter.api.Assertions.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private lateinit var mapsViewModel: MapsViewModel
    private val dummyStoriesResponse = DataDummy.generateDummyStory(10)

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(repositoryStory)
    }

    @Test
    fun `when getStoriesWithLocation Should Not Null and return success`() {
        val expectedStoryResponse = MutableLiveData<Result<StoryResponse>>()
        expectedStoryResponse.value = Result.Success(dummyStoriesResponse)

        `when`(repositoryStory.getStoriesWithLocation()).thenReturn(expectedStoryResponse)

        val actualStories = mapsViewModel.getStoriesWithLocation().getOrAwaitValue()
        Mockito.verify(repositoryStory).getStoriesWithLocation()
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Success)
        Assert.assertEquals(dummyStoriesResponse.listStory.size, (actualStories as Result.Success).data.listStory.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedStoryResponse = MutableLiveData<Result<StoryResponse>>()
        expectedStoryResponse.value = Result.Error("network error")

        `when`(repositoryStory.getStoriesWithLocation()).thenReturn(expectedStoryResponse)

        val actualStories = mapsViewModel.getStoriesWithLocation().getOrAwaitValue()
        Mockito.verify(repositoryStory).getStoriesWithLocation()
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Error)
    }
}