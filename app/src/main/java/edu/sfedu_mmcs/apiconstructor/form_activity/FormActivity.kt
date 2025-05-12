package edu.sfedu_mmcs.apiconstructor.form_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo
import androidx.core.content.edit
import edu.sfedu_mmcs.apiconstructor.result_activity.ResultActivity
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo

class FormActivity: AppCompatActivity() {

    private val TAG = "FormActivity"
    private lateinit var myViewModel: FormViewModel
    private val listAdapter = FormContentAdapter()
    private val contentAdapter = FormContentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        myViewModel = ViewModelProvider(
            this,
            FormViewModelFactory(
                intent.getParcelableExtra("info", RouteInfo::class.java)!!,
                getSharedPreferences("AppSettings", MODE_PRIVATE)
            )
        )[FormViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.formsRecycle)
        val recyclerContentView = findViewById<RecyclerView>(R.id.formsContentRecycle)

        val prefs = getSharedPreferences(intent.getStringExtra("method")!! + intent.getStringExtra("route")!!.replace("/", "_"), MODE_PRIVATE)
        val fieldsDataJson = prefs.getString("fieldsData", null)
        val contentDataJson = prefs.getString("contentData", null)


        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (fieldsDataJson != null) {
            val fieldsData: ArrayList<ContentInfo> = Gson().fromJson(fieldsDataJson, object : TypeToken<ArrayList<ContentInfo>>() {}.type)
            listAdapter.setItems(fieldsData)
        } else {
            val list = intent.getParcelableArrayListExtra<ContentInfo>("fields")!!
            listAdapter.setItems(list)
        }

        recyclerContentView.adapter = contentAdapter
        recyclerContentView.layoutManager = LinearLayoutManager(this)
        if (contentDataJson != null) {
            val contentData: ArrayList<ContentInfo> = Gson().fromJson(contentDataJson, object : TypeToken<ArrayList<ContentInfo>>() {}.type)
            contentAdapter.setItems(contentData)
        } else {
            contentAdapter.setItems(intent.getParcelableArrayListExtra<ContentInfo>("content")!!)
        }

        myViewModel.responseRes.observe(this, Observer {
            Log.d(TAG, "onResponse: $it")
            showResultDialog(it)
        })

        val sendBtn = findViewById<Button>(R.id.sendBtn)
        sendBtn.text = intent.getStringExtra("route")
        sendBtn.setOnClickListener {
            val fieldsData = collectContentData(recyclerView)
            val contentData = collectContentData(recyclerContentView)

            prefs.edit() {
                putString("fieldsData", Gson().toJson(fieldsData.values))
                putString("contentData", Gson().toJson(contentData.values))
            }

            myViewModel.sendData(collectData(recyclerView), contentData)
        }

    }

    private fun collectData(recyclerView: RecyclerView): MutableMap<String, String> {
        val data = mutableMapOf<String, String>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as FormContentAdapter.ViewHolder
            data[viewHolder.editText.hint.toString()] = viewHolder.editText.text.toString()
        }
        return data
    }
    private fun collectContentData(recyclerView: RecyclerView): MutableMap<String, ContentInfo> {
        val data = mutableMapOf<String, ContentInfo>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as FormContentAdapter.ViewHolder
            data[viewHolder.editText.hint.toString()] = ContentInfo(viewHolder.editText.hint.toString(), viewHolder.editText.text.toString())
        }
        return data
    }

    private fun showResultDialog(text: String){
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("result", text)
        startActivity(intent)
    }
}