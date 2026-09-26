package com.kampplus.hava.feature.weather.presentation.preference

/** Sıcaklık birim tercihleri. */
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

/** Görünüm / düzen tercihleri. */
enum class ForecastDisplayMode {
    DETAILED,
    COMPACT
}

/**
 * Kullanıcının seçim ve görünüm tercihlerini temsil eden UiState.
 *
 * @property temperatureUnit Seçili sıcaklık birimi (°C veya °F)
 * @property displayMode Seçili tahmin görünüm modu (Detaylı veya Özet)
 */
data class ViewPreferenceUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val displayMode: ForecastDisplayMode = ForecastDisplayMode.DETAILED
) {
    /**
     * Türetilen Bilgi 1: Mevcut birim sembolü (°C veya °F).
     * State'ten anlık olarak türetilir.
     */
    val unitSymbol: String
        get() = when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }

    /**
     * Türetilen Bilgi 2: Fahrenheit biriminin seçili olup olmadığı.
     */
    val isFahrenheit: Boolean
        get() = temperatureUnit == TemperatureUnit.FAHRENHEIT

    /**
     * Türetilen Bilgi 3: Tercihlerin özet metni.
     */
    val summaryText: String
        get() = "Birim: $unitSymbol | Görünüm: ${if (displayMode == ForecastDisplayMode.DETAILED) "Detaylı" else "Özet"}"

    /**
     * Türetilen Bilgi 4: Santigrat dereceyi seçili birime dönüştürür.
     */
    fun convertTemperature(celsius: Double): Double {
        return when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
    }

    /**
     * Türetilen Bilgi 5: Sıcaklık değerini birim sembolüyle biçimlendirir.
     */
    fun formatTemperature(celsius: Double): String {
        val converted = convertTemperature(celsius)
        return "${converted.toInt()}$unitSymbol"
    }
}
