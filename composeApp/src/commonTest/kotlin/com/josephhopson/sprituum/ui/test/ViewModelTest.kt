package com.josephhopson.sprituum.ui.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base class for ViewModel tests
 *
 * Sets up a test coroutine dispatcher to ensure tests run in a controlled environment.
 */
@ExperimentalCoroutinesApi
abstract class ViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        setupTest()
    }

    @AfterTest
    fun tearDown() {
        tearDownTest()
        Dispatchers.resetMain()
    }

    /**
     * Setup method that should be implemented by subclasses
     */
    abstract fun setupTest()

    /**
     * Tear down method that can be overridden by subclasses
     */
    open fun tearDownTest() {
        // Default implementation does nothing
    }
}