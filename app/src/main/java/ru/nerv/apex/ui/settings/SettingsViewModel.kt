package ru.nerv.apex.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.nerv.apex.data.settings.SettingsRepository
import javax.inject.Inject
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<String> = settingsRepository.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val language: StateFlow<String> = settingsRepository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val defaultYear: StateFlow<Int> = settingsRepository.defaultYearFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2026)

    fun setTheme(theme: String) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)

            val localeList = when (language) {
                "en" -> LocaleListCompat.forLanguageTags("en")
                "ru" -> LocaleListCompat.forLanguageTags("ru")
                else -> LocaleListCompat.getEmptyLocaleList()
            }
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    fun setDefaultYear(year: Int) {
        viewModelScope.launch { settingsRepository.setDefaultYear(year) }
    }
}