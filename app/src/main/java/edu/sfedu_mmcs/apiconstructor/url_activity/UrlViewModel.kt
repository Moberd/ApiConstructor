package edu.sfedu_mmcs.apiconstructor.url_activity
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UrlViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _url = MutableLiveData<String>()
    val url: LiveData<String> get() = _url

    private val _spec = MutableLiveData<String>()
    val spec: LiveData<String> get() = _spec

    init {
        _url.value = sharedPreferences.getString("saved_url", "") ?: ""
        _spec.value = sharedPreferences.getString("saved_spec", "") ?: ""
    }

    fun saveUrl(url: String, spec: String) {
        with(sharedPreferences.edit()) {
            putString("saved_url", url)
            putString("saved_spec", spec)
            apply()
        }
        _url.value = url
        _spec.value = spec
    }
}