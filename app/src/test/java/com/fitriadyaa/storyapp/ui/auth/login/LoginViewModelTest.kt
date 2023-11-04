package com.fitriadyaa.storyapp.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.utils.DataDummy
import com.fitriadyaa.storyapp.utils.getOrAwaitValue
import com.fitriadyaa.storyapp.data.Result
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
class LoginViewModelTest {

    // Rule to run architecture components related tests on a proper thread
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLogin()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(repositoryStory)
    }

    @Test
    fun `when login Should Not Null and return success`() {
        val expectedLoginResponse = MutableLiveData<Result<LoginResponse>>()
        expectedLoginResponse.value = Result.Success(dummyLoginResponse)

        val email = "name@email.com"
        val password = "secretpassword"

        Mockito.`when`(repositoryStory.postLogin(email, password)).thenReturn(expectedLoginResponse)

        val actualResponse = loginViewModel.login(email, password).getOrAwaitValue()

        Mockito.verify(repositoryStory).postLogin(email, password)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedLoginResponse = MutableLiveData<Result<LoginResponse>>()
        expectedLoginResponse.value = Result.Error("network error")

        val email = "name@email.com"
        val password = "secretpassword"


        Mockito.`when`(repositoryStory.postLogin(email, password)).thenReturn(expectedLoginResponse)

        val actualResponse = loginViewModel.login(email, password).getOrAwaitValue()

        Mockito.verify(repositoryStory).postLogin(email, password)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }
}
