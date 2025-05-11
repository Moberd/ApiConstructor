package edu.sfedu_mmcs.apiconstructor.url_activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.main_activity.MainActivity

class UrlActivity : AppCompatActivity() {
    private lateinit var viewModel: UrlViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url)
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        viewModel = ViewModelProvider(
            this,
            UrlViewModelFactory(sharedPreferences)
        )[UrlViewModel::class.java]

        val urlInput = findViewById<EditText>(R.id.url_input)
        val specInput = findViewById<EditText>(R.id.spec_input)
        val saveButton = findViewById<Button>(R.id.save_button)

        viewModel.url.observe(this, Observer { url ->
            urlInput.setText(url)
        })
        viewModel.spec.observe(this, Observer { spec ->
            specInput.setText(spec)
        })

        saveButton.setOnClickListener {
            val url = urlInput.text.toString()
            val spec = specInput.text.toString()
            viewModel.saveUrl(url, spec)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}