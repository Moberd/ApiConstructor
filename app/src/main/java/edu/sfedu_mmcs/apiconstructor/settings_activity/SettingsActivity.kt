package edu.sfedu_mmcs.apiconstructor.settings_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.main_activity.MainActivity
import edu.sfedu_mmcs.apiconstructor.url_activity.UrlActivity
import edu.sfedu_mmcs.apiconstructor.utils.AuthInfo

class SettingsActivity : AppCompatActivity() {
    private lateinit var mViewModel: SettingsViewModel
    private val authAdapter = AuthTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val switchExamples = findViewById<SwitchMaterial>(R.id.switch_examples)
        val switchPrev = findViewById<SwitchMaterial>(R.id.switch_prev)
        val buttonSave = findViewById<Button>(R.id.button_save)
        val buttonExit = findViewById<Button>(R.id.button_exit)
        val recyclerView = findViewById<RecyclerView>(R.id.authRecycle)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        val toolbar = findViewById<Toolbar>(R.id.toolbarSettings)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(getSharedPreferences("AppSettings", MODE_PRIVATE))
        )[SettingsViewModel::class.java]

        recyclerView.adapter = authAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mViewModel.authList.observe(this) {
            val prev = ArrayList<AuthInfo>()
            for (item in it) {
                prev.add(AuthInfo(item.name, item.type, sharedPreferences.getString(item.name, "")!!))
            }
            authAdapter.setAuths(prev)
        }

        mViewModel.isLoading.observe(this) { isLoading ->
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mViewModel.getSecurityTypes()

        switchExamples.isChecked = sharedPreferences.getBoolean("enableExamples", true)
        switchPrev.isChecked = sharedPreferences.getBoolean("enablePrev", true)

        buttonSave.setOnClickListener {
            editor.putBoolean("enableExamples", switchExamples.isChecked)
            editor.putBoolean("enablePrev", switchPrev.isChecked)
            for (item in collectData(recyclerView)) {
                editor.putString(item.key, item.value)
            }
            editor.apply()
        }

        buttonExit.setOnClickListener {
            val intent = Intent(this, UrlActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun collectData(recyclerView: RecyclerView): MutableMap<String, String> {
        val data = mutableMapOf<String, String>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as AuthTypeAdapter.ViewHolder
            data[viewHolder.textView.text.toString()] = viewHolder.editText.text.toString()
        }
        return data
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}