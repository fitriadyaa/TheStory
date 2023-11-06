package com.fitriadyaa.storyapp.ui.story.createStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryPostResponse
import com.fitriadyaa.storyapp.utils.DataDummy
import com.fitriadyaa.storyapp.utils.getOrAwaitValue
import com.fitriadyaa.storyapp.data.Result
import org.junit.jupiter.api.Assertions.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Assertions


@RunWith(MockitoJUnitRunner::class)
class CreateStoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private lateinit var createStoryViewModel: CreateStoryViewModel
    private val dummyResponse = DataDummy.generateDummyCreateStory()

    @Before
    fun setUp() {
        createStoryViewModel = CreateStoryViewModel(repositoryStory)
    }

    @Test
    fun `when postStory Should Not Null and return success`() {
        val descriptionText = "Description Text"
        val description = descriptionText.toRequestBody("text/plain".toMediaType())

        val file = File("testFile.jpg")
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        val expectedPostResponse = MutableLiveData<Result<StoryPostResponse>>()
        expectedPostResponse.value = Result.Success(dummyResponse)

        Mockito.`when`(repositoryStory.postStory(imageMultipart, description, 0.0, 0.0))
            .thenReturn(expectedPostResponse)

        val actualResponse = createStoryViewModel.postStory(imageMultipart, description, 0.0, 0.0).getOrAwaitValue()

        Mockito.verify(repositoryStory).postStory(imageMultipart, description, 0.0, 0.0)
        Assertions.assertNotNull(actualResponse)
        Assertions.assertTrue(actualResponse is Result.Success)
        Assertions.assertEquals(dummyResponse.error, (actualResponse as Result.Success).data.error)

        // Check if data is null
        if (actualResponse is Result.Success) {
            Assertions.assertFalse(actualResponse.data == null)
        } else {
            Assert.assertFalse(actualResponse == null)
        }
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val descriptionText = "Description text"
        val description = descriptionText.toRequestBody("text/plain".toMediaType())

        val file = File("testFile.jpg")
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        val expectedPostResponse = MutableLiveData<Result<StoryPostResponse>>()
        expectedPostResponse.value = Result.Error("network error")

        Mockito.`when`(repositoryStory.postStory(imageMultipart, description, 0.0, 0.0)).thenReturn(expectedPostResponse)

        val actualResponse = createStoryViewModel.postStory(imageMultipart, description, 0.0, 0.0).getOrAwaitValue()

        Mockito.verify(repositoryStory).postStory(imageMultipart, description, 0.0, 0.0)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }
}