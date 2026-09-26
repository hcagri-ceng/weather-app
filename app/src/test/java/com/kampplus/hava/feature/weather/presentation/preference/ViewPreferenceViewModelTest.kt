package com.kampplus.hava.feature.weather.presentation.preference

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kampplus.hava.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ViewPreferenceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state defaults to Celsius and Detailed display mode`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = ViewPreferenceViewModel(savedStateHandle)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(TemperatureUnit.CELSIUS, initial.temperatureUnit)
            assertEquals(ForecastDisplayMode.DETAILED, initial.displayMode)
            assertEquals("°C", initial.unitSymbol)
            assertFalse(initial.isFahrenheit)
        }
    }

    @Test
    fun `toggleTemperatureUnit switches between Celsius and Fahrenheit`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = ViewPreferenceViewModel(savedStateHandle)

        viewModel.uiState.test {
            assertEquals(TemperatureUnit.CELSIUS, awaitItem().temperatureUnit)

            viewModel.toggleTemperatureUnit()
            val fahrenheitState = awaitItem()
            assertEquals(TemperatureUnit.FAHRENHEIT, fahrenheitState.temperatureUnit)
            assertEquals("°F", fahrenheitState.unitSymbol)
            assertTrue(fahrenheitState.isFahrenheit)

            viewModel.toggleTemperatureUnit()
            val celsiusState = awaitItem()
            assertEquals(TemperatureUnit.CELSIUS, celsiusState.temperatureUnit)
            assertEquals("°C", celsiusState.unitSymbol)
        }
    }

    @Test
    fun `selectDisplayMode updates display mode and produces new state`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = ViewPreferenceViewModel(savedStateHandle)

        viewModel.uiState.test {
            assertEquals(ForecastDisplayMode.DETAILED, awaitItem().displayMode)

            viewModel.selectDisplayMode(ForecastDisplayMode.COMPACT)
            val compactState = awaitItem()
            assertEquals(ForecastDisplayMode.COMPACT, compactState.displayMode)
            assertTrue(compactState.summaryText.contains("Özet"))
        }
    }

    @Test
    fun `restores state from SavedStateHandle on process recreation or configuration change`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                ViewPreferenceViewModel.KEY_TEMP_UNIT to TemperatureUnit.FAHRENHEIT.name,
                ViewPreferenceViewModel.KEY_DISPLAY_MODE to ForecastDisplayMode.COMPACT.name
            )
        )
        val viewModel = ViewPreferenceViewModel(savedStateHandle)

        viewModel.uiState.test {
            val restored = awaitItem()
            assertEquals(TemperatureUnit.FAHRENHEIT, restored.temperatureUnit)
            assertEquals(ForecastDisplayMode.COMPACT, restored.displayMode)
            assertEquals("77°F", restored.formatTemperature(25.0))
        }
    }

    @Test
    fun `derived information calculations are accurate`() {
        val celsiusState = ViewPreferenceUiState(
            temperatureUnit = TemperatureUnit.CELSIUS,
            displayMode = ForecastDisplayMode.DETAILED
        )
        assertEquals("°C", celsiusState.unitSymbol)
        assertEquals(25.0, celsiusState.convertTemperature(25.0), 0.01)
        assertEquals("25°C", celsiusState.formatTemperature(25.0))
        assertTrue(celsiusState.summaryText.contains("°C"))

        val fahrenheitState = ViewPreferenceUiState(
            temperatureUnit = TemperatureUnit.FAHRENHEIT,
            displayMode = ForecastDisplayMode.COMPACT
        )
        assertEquals("°F", fahrenheitState.unitSymbol)
        assertEquals(77.0, fahrenheitState.convertTemperature(25.0), 0.01)
        assertEquals("77°F", fahrenheitState.formatTemperature(25.0))
        assertTrue(fahrenheitState.isFahrenheit)
    }
}
