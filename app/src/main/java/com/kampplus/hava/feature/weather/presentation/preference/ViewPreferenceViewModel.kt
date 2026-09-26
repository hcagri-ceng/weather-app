package com.kampplus.hava.feature.weather.presentation.preference

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Seçim ve görünüm tercihlerini yöneten ViewModel.
 * NavHost üstünden tek bir ortak örnek alınarak ilgili ekranların aynı durum kaynağını
 * (StateFlow<ViewPreferenceUiState>) kullanması sağlanır.
 *
 * SavedStateHandle kullanımı sayesinde ekran geçişlerinde ve cihaz döndürüldüğünde (configuration change)
 * kullanıcının seçimi korunur.
 */
@HiltViewModel
class ViewPreferenceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ViewPreferenceUiState(
            temperatureUnit = savedStateHandle.get<String>(KEY_TEMP_UNIT)
                ?.let { runCatching { TemperatureUnit.valueOf(it) }.getOrNull() }
                ?: TemperatureUnit.CELSIUS,
            displayMode = savedStateHandle.get<String>(KEY_DISPLAY_MODE)
                ?.let { runCatching { ForecastDisplayMode.valueOf(it) }.getOrNull() }
                ?: ForecastDisplayMode.DETAILED
        )
    )

    /** Durumu StateFlow<ViewPreferenceUiState> üzerinden sunar. */
    val uiState: StateFlow<ViewPreferenceUiState> = _uiState.asStateFlow()

    /** Sıcaklık birimini günceller ve yeni state üretir. */
    fun selectTemperatureUnit(unit: TemperatureUnit) {
        _uiState.update { currentState ->
            currentState.copy(temperatureUnit = unit)
        }
        savedStateHandle[KEY_TEMP_UNIT] = unit.name
    }

    /** Sıcaklık birimini toggle eder (°C <-> °F). */
    fun toggleTemperatureUnit() {
        val nextUnit = if (_uiState.value.temperatureUnit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
        selectTemperatureUnit(nextUnit)
    }

    /** Görünüm modunu günceller (Detaylı <-> Özet). */
    fun selectDisplayMode(mode: ForecastDisplayMode) {
        _uiState.update { currentState ->
            currentState.copy(displayMode = mode)
        }
        savedStateHandle[KEY_DISPLAY_MODE] = mode.name
    }

    companion object {
        const val KEY_TEMP_UNIT = "key_temperature_unit"
        const val KEY_DISPLAY_MODE = "key_display_mode"
    }
}
