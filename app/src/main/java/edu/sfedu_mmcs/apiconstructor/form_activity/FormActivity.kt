package edu.sfedu_mmcs.apiconstructor.form_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.result_activity.ResultActivity
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo
import androidx.appcompat.widget.Toolbar

class FormActivity : AppCompatActivity() {

    private lateinit var myViewModel: FormViewModel
    private val listAdapter = FormContentAdapter()
    private val contentAdapter = FormContentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val routeInfo = intent.getParcelableExtra("info", RouteInfo::class.java)!!

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = routeInfo.method + routeInfo.route
            setDisplayHomeAsUpEnabled(true)
        }

        myViewModel = ViewModelProvider(
            this,
            FormViewModelFactory(
                routeInfo,
                getSharedPreferences("AppSettings", MODE_PRIVATE)
            )
        )[FormViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.formsRecycle)
        val recyclerContentView = findViewById<RecyclerView>(R.id.formsContentRecycle)

        val prefs = getSharedPreferences(
            routeInfo.method + routeInfo.route.replace("/", "_"),
            MODE_PRIVATE
        )
        val fieldsDataJson = prefs.getString("fieldsData", null)
        val contentDataJson = prefs.getString("contentData", null)

        val descTV = findViewById<TextView>(R.id.description)
        val descHead = findViewById<TextView>(R.id.descHead)
        if (routeInfo.description.isNotEmpty()) {
            descTV.text = routeInfo.description
        } else {
            descTV.visibility = View.GONE
            descHead.visibility = View.GONE
        }

        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (fieldsDataJson != null) {
            val fieldsData: ArrayList<ContentInfo> = Gson().fromJson(fieldsDataJson, object : TypeToken<ArrayList<ContentInfo>>() {}.type)
            listAdapter.setItems(fieldsData)
        } else {
            val list = routeInfo.fields
            listAdapter.setItems(list)
        }
        if (listAdapter.mList.size == 0){
            val paramHead = findViewById<TextView>(R.id.paramsHead)
            paramHead.visibility = View.GONE
        }

        recyclerContentView.adapter = contentAdapter
        recyclerContentView.layoutManager = LinearLayoutManager(this)
        if (contentDataJson != null) {
            val contentData: ArrayList<ContentInfo> = Gson().fromJson(contentDataJson, object : TypeToken<ArrayList<ContentInfo>>() {}.type)
            contentAdapter.setItems(contentData)
        } else {
            contentAdapter.setItems(routeInfo.content)
        }
        if (contentAdapter.mList.size == 0){
            val contHead = findViewById<TextView>(R.id.contHead)
            contHead.visibility = View.GONE
        }

        myViewModel.responseRes.observe(this, Observer {
            showResultDialog(it)
        })

        val sendBtn = findViewById<Button>(R.id.sendBtn)
        sendBtn.text = routeInfo.route
        sendBtn.setOnClickListener {
            val fieldsData = collectData(recyclerView)
            val contentData = collectData(recyclerContentView)

            val validationErrors = validateData(fieldsData, listAdapter.mList) + validateData(contentData, contentAdapter.mList)
            if (validationErrors.isNotEmpty()) {
                Toast.makeText(this, validationErrors.joinToString("\n"), Toast.LENGTH_LONG).show()
            } else {
                prefs.edit {
                    putString("fieldsData", Gson().toJson(listAdapter.mList.map { it.copy(example = fieldsData[it.name] ?: it.example) }))
                    putString("contentData", Gson().toJson(contentAdapter.mList.map { it.copy(example = contentData[it.name] ?: it.example) }))
                }
                myViewModel.sendData(fieldsData, contentData)
            }
        }
    }

    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the activity when the back button is pressed
        return true
    }

    private fun collectData(recyclerView: RecyclerView): Map<String, String> {
        val data = mutableMapOf<String, String>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder is FormContentAdapter.TextViewHolder) {
                val name = viewHolder.textView.text.toString()
                val value = viewHolder.editText.text.toString()
                data[name] = value
            } else if (viewHolder is FormContentAdapter.SpinnerViewHolder) {
                val name = viewHolder.textView.text.toString()
                val value = viewHolder.spinner.selectedItem.toString()
                data[name] = value
            }
        }
        return data
    }

    private fun validateData(data: Map<String, String>, contentInfos: List<ContentInfo>): List<String> {
        val errors = mutableListOf<String>()
        val dateTimeRegex = Regex(
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?(Z|[+-]\\d{2}:\\d{2})$"
        )

        for (contentInfo in contentInfos) {
            val value = data[contentInfo.name]
            if (contentInfo.required && value.isNullOrBlank()) {
                errors.add("${contentInfo.name} is required")
            } else if (!value.isNullOrBlank()) {
                if (contentInfo.enumValues?.isNotEmpty() == true) {
                    if (value !in contentInfo.enumValues) {
                        errors.add("${contentInfo.name} must be one of ${contentInfo.enumValues.joinToString()}")
                    }
                } else if (contentInfo.format != null) {
                    when (contentInfo.format) {
                        "int32", "int64", "integer" -> {
                            if (value.toIntOrNull() == null) {
                                errors.add("${contentInfo.name} must be an integer")
                            }
                        }
                        "float", "double" -> {
                            if (value.toDoubleOrNull() == null) {
                                errors.add("${contentInfo.name} must be a number")
                            }
                        }
                        "boolean" -> {
                            if (value != "true" && value != "false") {
                                errors.add("${contentInfo.name} must be 'true' or 'false'")
                            }
                        }
                        "date-time" -> {
                            if (!dateTimeRegex.matches(value)) {
                                errors.add("${contentInfo.name} must be a valid ISO 8601 date-time (e.g., 2023-10-15T14:30:00Z)")
                            }
                        }
                    }
                }
            }
        }
        return errors
    }

    private fun showResultDialog(text: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("result", text)
        startActivity(intent)
    }
}