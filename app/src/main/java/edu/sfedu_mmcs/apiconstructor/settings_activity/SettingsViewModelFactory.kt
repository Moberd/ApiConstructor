package edu.sfedu_mmcs.apiconstructor.settings_activity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory (
    private val sp: SharedPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(this.sp) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}