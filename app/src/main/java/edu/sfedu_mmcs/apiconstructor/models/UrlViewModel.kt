package edu.sfedu_mmcs.apiconstructor.models
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UrlViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _url = MutableLiveData<String>()
    val url: LiveData<String> get() = _url

    init {
        _url.value = sharedPreferences.getString("saved_url", "") ?: ""
    }

    fun saveUrl(url: String) {
        with(sharedPreferences.edit()) {
            putString("saved_url", url)
            apply()
        }
        _url.value = url
    }
}