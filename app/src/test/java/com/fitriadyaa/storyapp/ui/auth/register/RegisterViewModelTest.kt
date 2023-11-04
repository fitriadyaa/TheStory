package com.fitriadyaa.storyapp.ui.auth.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.utils.DataDummy
import com.fitriadyaa.storyapp.data.Result
import com.fitriadyaa.storyapp.utils.getOrAwaitValue
import org.junit.jupiter.api.Assertions.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private lateinit var registerViewModel: RegisterViewModel
    private val dummyRegisterResponse = DataDummy.generateDummyRegister()

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(repositoryStory)
    }

    @Test
    fun `when register Should Not Null and return success`() {
        val expectedRegisterResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedRegisterResponse.value = Result.Success(dummyRegisterResponse)

        val name = "name"
        val email = "name@email.com"
        val password = "secretpassword"

        Mockito.`when`(repositoryStory.postSignUp(name, email, password)).thenReturn(expectedRegisterResponse)

        val actualResponse = registerViewModel.register(name, email, password).getOrAwaitValue()

        Mockito.verify(repositoryStory).postSignUp(name, email, password)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success<*>)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedRegisterResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedRegisterResponse.value = Result.Error("network error")

        val name = "name"
        val email = "name@email.com"
        val password = "secretpassword"

        Mockito.`when`(repositoryStory.postSignUp(name, email, password)).thenReturn(expectedRegisterResponse)

        val actualResponse = registerViewModel.register(name, email, password).getOrAwaitValue()

        Mockito.verify(repositoryStory).postSignUp(name, email, password)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }
}