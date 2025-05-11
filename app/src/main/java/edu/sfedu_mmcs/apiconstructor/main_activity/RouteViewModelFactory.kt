package edu.sfedu_mmcs.apiconstructor.main_activity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RouteViewModelFactory (
    private val sp: SharedPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            RouteViewModel(this.sp) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}